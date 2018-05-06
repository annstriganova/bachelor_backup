package compaction;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static compaction.Code.MAX_BITS;

/*
Описание работы:
1) создать объект compaction.ArithmeticCompaction - в качестве параметра
передать массив Integer или путь к файлу
2) чтобы сжать: вызвать метод compaction() - в результате массив codes ранее созданного объекта
будет хранить объекты класса Code (вспомогательный класс, хранит размер
закодированной серии и полученный для неё код)
3) чтобы получить исходную последовательность чисел: вызвать метод decompaction(),


Вероятности подсчитываются при создании объекта ArithmeticCompaction, код выбирается пуьём вызова метода
findOptimal(double left, double right), по умолчанию исходный массив разбивается на части длиной 10 чисел
*/

public class ArithmeticCompaction implements Serializable {

    public ArrayList<Integer> numbers; // исходная выборка
    private NumberProbabilityMap n_p; // хранит пары число = вероятность
    private Map<Integer, Segment> n_s; // хранит пары цифра = соответвующий отрезок (сегмент)
    private Map<Segment, Integer> s_n; // хранит пары сегмент = цифра
    private ArrayList<Code> codes;
    private static final int BASIC_SIZE = 10;
    public static final int SCALE = 80;

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

    // Кодирвоание
    public void compaction() {
        defineSegments();
        BigDecimal left = BigDecimal.ZERO;
        BigDecimal right = BigDecimal.ONE;
        BigDecimal range;
        int counter = 0;
        int newSize = BASIC_SIZE;
        Code previousCode = null;
        boolean skipChecks = false;
        for (int i = 0; i < numbers.size(); i++) {
            int tmp = numbers.get(i);
            range = right.subtract(left);
            right = left.add(range.multiply(n_s.get(numbers.get(i)).right));
            left = left.add(range.multiply(n_s.get(numbers.get(i)).left));
            counter++;
            if (counter >= newSize) {
                Code currentCode = findOptimal(left, right);
                currentCode.setSize(counter);

                if ((numbers.size() - i) <= BASIC_SIZE) {
                    if (skipChecks ) {
                        codes.add(currentCode);
                        newSize = numbers.size() - i - 1;
                        left = BigDecimal.ZERO;
                        right = BigDecimal.ONE;
                        counter = 0;
                        previousCode = null;
                        continue;
                    }
                    skipChecks = true;
                    if (currentCode.bits < MAX_BITS) {
                        if (currentCode.isBetterThan(previousCode)) {
                            codes.add(currentCode);
                            left = BigDecimal.ZERO;
                            right = BigDecimal.ONE;
                            counter = 0;
                            newSize = numbers.size() - i - 1;
                            previousCode = null;
                            continue;
                        } else {
                            codes.add(previousCode);
                            i -= 3;
                            left = BigDecimal.ZERO;
                            right = BigDecimal.ONE;
                            counter = 0;
                            newSize = BASIC_SIZE;
                            previousCode = null;
                            continue;
                        }
                    } else {
                        if (previousCode != null) {
                            codes.add(previousCode);
                        } else {
                            codes.add(currentCode);
                            break;
                        }
                        i -= 3;
                        left = BigDecimal.ZERO;
                        right = BigDecimal.ONE;
                        counter = 0;
                        newSize = BASIC_SIZE;
                        previousCode = null;
                        continue;
                    }
                } else if (counter == BASIC_SIZE) {
                    if (currentCode.bits < MAX_BITS) {
                        newSize += 3;
                        previousCode = currentCode;
                        continue;
                    } else {
                        codes.add(currentCode);
                        left = BigDecimal.ZERO;
                        right = BigDecimal.ONE;
                        counter = 0;
                        newSize = BASIC_SIZE;
                        previousCode = null;
                        continue;
                    }
                } else {
                    if (currentCode.bits < MAX_BITS) {
                        if (currentCode.isBetterThan(previousCode)) {
                            newSize += 3;
                            previousCode = currentCode;
                            continue;
                        } else {
                            i -= 3;
                            codes.add(previousCode);
                            left = BigDecimal.ZERO;
                            right = BigDecimal.ONE;
                            counter = 0;
                            newSize = BASIC_SIZE;
                            previousCode = null;
                            continue;
                        }
                    } else {
                        i -= 3;
                        codes.add(previousCode);
                        left = BigDecimal.ZERO;
                        right = BigDecimal.ONE;
                        counter = 0;
                        newSize = BASIC_SIZE;
                        previousCode = null;
                        continue;
                    }
                }
            }

        }
        codes.forEach(k -> System.out.println(k.size));
    }

    // Декодирование
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
                                .divide(segment.right.subtract(segment.left), SCALE, RoundingMode.HALF_UP);
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
        for (NumberProbabilityMap.Entry e : n_p) {
            //System.out.println(n + " left: " + d + "\t\tright:" + (d + p_n.get(n)) + "\t\tвер: " + p_n.get(n));
            segment = new Segment(d, d.add(e.probability));
            n_s.put(e.number, segment);
            s_n.put(segment, e.number);
            d = d.add(e.probability);
        }
    }

    Code findOptimal(BigDecimal left, BigDecimal right) {
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal num;
        BigDecimal i = BigDecimal.ONE;
        BigDecimal two = BigDecimal.valueOf(2);
        int bitCounter = 0;
        do {
            i = i.multiply(two);
            num = BigDecimal.ONE.divide(i, SCALE, RoundingMode.HALF_UP);
            if (result.compareTo(left) > 0) {
                if (result.compareTo(right) >= 0) {
                    result = result.subtract(num);
                }
            } else {
                result = result.add(num);
            }
            bitCounter++;
        } while (!belongsToInterval(left, right, result) && (bitCounter <= MAX_BITS));
        /*System.out.println("Left\t\t\tRight\t\t\tResult\t\t\tIterations");
        System.out.println(left + "\t\t\t" + right + "\t\t\t" + result + "\t\t\t" + bitCounter);
        System.out.println('\n');*/
        if ((bitCounter >= MAX_BITS) || (left.compareTo(right) == 0)) {
            return new Code(left, bitCounter);
        }
        return new Code(result, bitCounter);
    }

    public Integer calculateBitsOf(BigDecimal left) {
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal num;
        BigDecimal i = BigDecimal.ONE;
        BigDecimal two = BigDecimal.valueOf(2);
        int bitCounter = 0;
        while ((result.compareTo(left) != 0) && (bitCounter <= MAX_BITS)) {
            i = i.multiply(two);
            num = BigDecimal.ONE.divide(i, SCALE, RoundingMode.HALF_UP);
            if (result.compareTo(left) > 0) {
                result = result.subtract(num);
            } else {
                result = result.add(num);
            }
            bitCounter++;
        }
        return bitCounter;
    }

    private static boolean belongsToInterval(BigDecimal left, BigDecimal right, BigDecimal result) {
        return result.compareTo(left) >= 0 && result.compareTo(right) < 0;
    }

    // Количесвто бит
    public int getBits(BigDecimal n) {

        //StringBuilder bits = new StringBuilder("0.");
        BigDecimal TWO = BigDecimal.valueOf(2);
        BigDecimal ZERO = new BigDecimal("0.0");
        int counter = 0;
        //for (int i = 0; i < 40; i++) {
        while (!n.equals(ZERO)) {
            n = n.multiply(TWO);
            if (n.compareTo(BigDecimal.ONE) >= 0) {
                //bits.append(1);
                n = n.subtract(BigDecimal.ONE);
            } /*else {
                    bits.append(0);
                }*/
            counter++;
        }
        //}
        return counter;
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
                        .divide(BigDecimal.valueOf(digits.size()), SCALE, RoundingMode.HALF_UP));

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