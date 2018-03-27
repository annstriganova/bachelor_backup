import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/*
Описание работы:
1) создать объект ArithmeticCompaction - в качестве параметра
передать массив Integer или путь к файлу
2) чтобы сжать: вызвать метод compaction() - в результате массив codes ранее созданного объекта
будет хранить объекты класса Code (вспомогательный класс, хранит размер
закодированной серии и полученный для неё код)
3) чтобы получить исходную последовательность чисел: вызвать метод decompaction(),


Вероятности подсчитываются при создании объекта ArithmeticCompaction, код выбирается пуьём вызова метода
findOptimal(double left, double right), по умолчанию исходный массив разбивается на части длиной 10 чисел
*/

public class ArithmeticCompaction implements Serializable {

    public ArrayList<Integer> numbers; // цифры исходного числа
    public Map<Integer, Double> probability; // вероятности (частоты) появления цифр в исходном числе
    public Map<Integer, Segment> map; // хранит пары цифра = соответвующий отрезок (сегмент)
    public Map<Segment, Integer> inverseMap; // хранит пары сегмент = цифра
    public ArrayList<Code> codes;
    private static final int MAX_LIST_SIZE = 13;

    public ArithmeticCompaction(ArrayList<Integer> numbers) {
        this.numbers = numbers;
        this.probability = probabilities(numbers);
        map = new HashMap<>();
        inverseMap = new HashMap<>();
        codes = new ArrayList<>();
    }

    public ArithmeticCompaction(String path) throws IOException {
        this(readFile(path));
    }

    // Интервал внутри отрезка [0,1)
    class Segment implements Comparable, Serializable {
        double left;
        double right;

        public Segment(double left, double right) {
            this.left = left;
            this.right = right;
        }


        @Override
        public int compareTo(Object obj) {
            Segment s = (Segment) obj;
            if (s.left < this.left) {
                return 1;
            } else if (s.left > this.left) {
                return -1;
            }
            return 0;
        }
    }

    class Code implements Serializable {
        Double code;
        Integer size;

        public Code(Double code, Integer size) {
            this.code = code;
            this.size = size;
        }
    }

    // Сжатие, возвращаемое значение - искомое дробное число
    public void compaction() {
        defineSegments();
        double left = 0.0;
        double right = 1.0;
        //double newRight, newLeft;
        double range;
        int counter = 0;
        for (Integer number : numbers) {
            range = right - left;
            right = left + range * map.get(number).right;
            left = left + range * map.get(number).left;
            counter++;
            if (counter >= MAX_LIST_SIZE) {
                System.out.println("Левая граница:  " + left + " " + getBits(left) + "\nПравая граница: " + right + " " + getBits(right));
                codes.add(new Code(findOptimal(left, right), counter));
                counter = 0;
                left = 0.0;
                right = 1.0;
            }
        }
    }

    // "Расжатие"
    public ArrayList<Integer> decompaction() {
        ArrayList<Integer> samples = new ArrayList<>();
        ArrayList<Segment> segments = new ArrayList<>(map.values());
        segments.sort(Segment::compareTo);
        double code;
        for (Code c : codes) {
            code = c.code;
            for (int i = 0; i < c.size; i++) {
                for (Segment segment : segments) {
                    if ((code >= segment.left) && (code < segment.right)) {
                        samples.add(inverseMap.get(segment));
                        code = (code - segment.left) / (segment.right - segment.left);
                        break;
                    }
                }
            }
        }
        return samples;
    }

    /*
    Разбитие отрезка [0,1) на сегменты
    map хранит пары цифра = соответвующий отрезок (сегмент)
    inverseMap хранит пары сегмент = цифра
    */
    public void defineSegments() {
        double d = 0;
        Segment segment;
        // HashMap, отсортированная по возрастанию вероятностей
        Map<Integer, Double> sorted = probability.entrySet().stream()
                .sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        Set<Integer> numbers = sorted.keySet();
        for (Integer n : numbers) {
            //System.out.println(n + " left: " + d + "\t\tright:" + (d + probability.get(n)) + "\t\tвер: " + probability.get(n));
            segment = new Segment(d, d + probability.get(n));
            map.put(n, segment);
            inverseMap.put(segment, n);
            d = d + probability.get(n);
        }
    }

    public static double findOptimal(double left, double right) {
        if (left == right) {
            return left;
        }
        Double result = 0.0, num;
        int i = 2;

        int brk = (int) Math.pow(2, 31);
        for (int j = i; j <= brk; j++) {
            num = 1.0 / j;
            if (result >= left) {
                if (result < right) {
                    //return result==right?left:result;
                    return result;
                } else {
                    result -= num;
                }
            } else {
                result += num;
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
    Подсчёт вероятностей, на входе - массив цифр исходного числа
    на выходе - цифра = вероятность появления
    */
    public static Map<Integer, Double> probabilities(List<Integer> digits) {
        Map<Integer, Double> map = new HashMap<>();
        ArrayList<Integer> copyList, duplicates;
        for (Integer d : digits) {
            if (!map.containsKey(d)) {
                copyList = new ArrayList<>(digits);
                duplicates = new ArrayList<>();
                duplicates.add(d);
                copyList.removeAll(duplicates);
                map.put(d, (digits.size() - copyList.size()) / (double) digits.size());
            }
        }
        return map;
    }

    public static ArrayList<Integer> readFile(String path) throws IOException {
        ArrayList<Integer> numbers = new ArrayList<>();
        Files.lines(Paths.get(path)
                , StandardCharsets.UTF_8).forEach(k -> numbers.add(new Integer(k)));
        return numbers;
    }

    // Преобразование Integer в массив цифр
    public static List<Integer> toDigits(Integer n) {
        return n.toString()
                .chars()
                .mapToObj(c -> Character.digit(c, 10))
                .collect(Collectors.toList());//Integer.toString(n).toCharArray();
    }

    public static void main(String[] args) throws Exception {
        ArithmeticCompaction ac = new ArithmeticCompaction("C:\\IdeaProjects\\bachelor_paper\\" +
                "src\\main\\resources\\compaction\\normal");
        ac.compaction();
        /*FileOutputStream fos = new FileOutputStream("inverseMap.out");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(ac.inverseMap);
        oos.flush();
        oos.close();

        fos = new FileOutputStream("map.out");
        oos = new ObjectOutputStream(fos);
        oos.writeObject(ac.map);
        oos.flush();
        oos.close();*/
    }
}