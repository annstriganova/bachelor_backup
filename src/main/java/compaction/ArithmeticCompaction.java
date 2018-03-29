package compaction;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/*
Описание работы:
1) создать объект compaction.ArithmeticCompaction - в качестве параметра
передать массив Integer или путь к файлу
2) чтобы сжать: вызвать метод compaction() - в результате массив codes ранее созданного объекта
будет хранить объекты класса Code (вспомогательный класс, хранит размер
закодированной серии и полученный для неё код)
3) чтобы получить исходную последовательность чисел: вызвать метод decompaction(),


Вероятности подсчитываются при создании объекта compaction.ArithmeticCompaction, код выбирается пуьём вызова метода
findOptimal(double left, double right), по умолчанию исходный массив разбивается на части длиной 10 чисел
*/

public class ArithmeticCompaction implements Serializable {

    public ArrayList<Integer> numbers; // исходная выборка
    private NumberProbabilityMap n_p; // хранит пары число = вероятность
    private Map<Integer, Segment> n_s; // хранит пары цифра = соответвующий отрезок (сегмент)
    private Map<Segment, Integer> s_n; // хранит пары сегмент = цифра
    private ArrayList<Code> codes;
    private static final int MAX_LIST_SIZE = 10;

    public ArithmeticCompaction(ArrayList<Integer> numbers) {
        this.numbers = numbers;
        n_p = new NumberProbabilityMap();
        initProbabilities(numbers);
        n_s = new HashMap<>();
        s_n = new HashMap<>();
        codes = new ArrayList<>();
    }

    public ArithmeticCompaction(String path) throws IOException {
        this(readFile(path));
    }

    // Интервал внутри отрезка [0,1)
    class Segment implements Comparable, Serializable {
        BigDecimal left;
        BigDecimal right;

        public Segment(BigDecimal left, BigDecimal right) {
            this.left = left;
            this.right = right;
        }


        @Override
        public int compareTo(Object obj) {
            Segment s = (Segment) obj;
            if (s.left.compareTo(this.left) < 0) {
                return 1;
            } else if (s.left.compareTo(this.left) > 0) {
                return -1;
            }
            return 0;
        }
    }

    class Code implements Serializable {
        BigDecimal code;
        Integer size;

        Code(BigDecimal code, Integer size) {
            this.code = code;
            this.size = size;
        }
    }

    // Сжатие, возвращаемое значение - искомое дробное число
    public void compaction() {
        defineSegments();
        BigDecimal left = BigDecimal.ZERO;
        BigDecimal right = BigDecimal.ONE;
        //double newRight, newLeft;
        BigDecimal range;
        int counter = 0;
        for (Integer number : numbers) {
            range = right.subtract(left);
            right = left.add(range.multiply(n_s.get(number).right));
            left = left.add(range.multiply(n_s.get(number).left));
            counter++;
            if (counter >= MAX_LIST_SIZE) {
                //System.out.println("Левая граница:  " + left + " " + getBits(left) + "\nПравая граница: " + right + " " + getBits(right));
                codes.add(new Code(findOptimal(left, right), counter));
                counter = 0;
                left = BigDecimal.ZERO;
                right = BigDecimal.ONE;
            }
        }
    }

    // "Расжатие"
    public ArrayList<Integer> decompaction() {
        ArrayList<Integer> samples = new ArrayList<>();
        ArrayList<Segment> segments = new ArrayList<>(n_s.values());
        segments.sort(Segment::compareTo);
        BigDecimal code;
        for (Code c : codes) {
            code = c.code;
            for (int i = 0; i < c.size; i++) {
                for (Segment segment : segments) {
                    if ((code.compareTo(segment.left) >= 0)
                            && (code.compareTo(segment.right) < 0)) {
                        samples.add(s_n.get(segment));
                        code = (code.subtract(segment.left))
                                .divide(segment.right.subtract(segment.left), 40, RoundingMode.HALF_UP);
                        break;
                    }
                }
            }
        }
        return samples;
    }

    /*
    Разбиение отрезка [0,1) на сегменты
    n_s хранит пары число = соответвующий отрезок (сегмент)
    s_n хранит пары сегмент = число
    */
    private void defineSegments() {
        BigDecimal d = BigDecimal.ZERO;
        Segment segment;
        for (NumberProbabilityMap.Entry e : n_p){
            //System.out.println(n + " left: " + d + "\t\tright:" + (d + p_n.get(n)) + "\t\tвер: " + p_n.get(n));
            segment = new Segment(d, d.add(e.probability));
            n_s.put(e.number, segment);
            s_n.put(segment, e.number);
            d = d.add(e.probability);
        }
    }

    private static BigDecimal findOptimal(BigDecimal left, BigDecimal right) {
        if (left.compareTo(right) == 0) {
            return left;
        }
        BigDecimal result = BigDecimal.ZERO, num;
        int i = 2;

        int brk = (int) (Math.pow(2, 64) - 1);
        for (int j = i; j <= brk; j++) {
            num = BigDecimal.ONE.divide(BigDecimal.valueOf(j), 40, RoundingMode.HALF_UP);
            if (result.compareTo(left) >= 0) {
                if (result.compareTo(right) < 0) {
                    //return result==right?left:result;
                    return result;
                } else {
                    result = result.subtract(num);
                }
            } else {
                result = result.add(num);
            }
        }
        return left;
        //return (left + right) / 2.0;
    }

    // Битовое представление числа
    public static Object getBits(Number n) {
        if (n instanceof Integer) {
            return Integer.toBinaryString((Integer) n);
        } else {
            StringBuilder bits = new StringBuilder("0.");
            Double num = (Double) n;
            for (int i = 0; i < 40; i++) {
                if (num.equals(0.0)) {
                    break;
                } else {
                    num *= 2;
                    if (num.compareTo(1.0) >= 0) {
                        bits.append(1);
                        num -= 1.0;
                    } else {
                        bits.append(0);
                    }
                }
            }
            return bits;
        }
    }

    /*
    Подсчёт вероятностей, на входе - исходная выборка.
    Результат  - заплонение n_p и p_n.
    */
    private void initProbabilities(List<Integer> digits) {
        ArrayList<Integer> copyList, duplicates;
        for (Integer d : digits) {
            if (!n_p.containsKey(d)) {
                copyList = new ArrayList<>(digits);
                duplicates = new ArrayList<>();
                duplicates.add(d);
                copyList.removeAll(duplicates);
                n_p.put(d, BigDecimal.valueOf(digits.size() - copyList.size())
                        .divide(BigDecimal.valueOf(digits.size()), 40, RoundingMode.HALF_UP));

            }
        }
    }

    private static ArrayList<Integer> readFile(String path) throws IOException {
        ArrayList<Integer> numbers = new ArrayList<>();
        Files.lines(Paths.get(path)
                , StandardCharsets.UTF_8).forEach(k -> numbers.add(new Integer(k)));
        return numbers;
    }

    public static void main(String[] args) {
    }
}