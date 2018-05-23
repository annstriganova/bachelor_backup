import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class StatisticsCalculator {

    public static Double dispersion(ArrayList<Double> numbers, Double mathExpect, Map<Double, Double> pMap) throws IOException {
        ArrayList<Double> s_p = new ArrayList<>();
        Double tmp;
        for (Double n : numbers) {
            tmp = Math.pow(n - mathExpect, 2.0) * pMap.get(n);
            s_p.add(tmp);
        }
        Double sum = sum(null, s_p);
        System.out.println("Дисперсия: " + sum);
        return sum;

    }

    public static Double quantile(String path, Double alfa) throws IOException {
        ArrayList<Double> numbers = new ArrayList<>();
        Files.lines(Paths.get(path)
                , StandardCharsets.UTF_8).forEach(k -> numbers.add(new Double(k)));
        numbers.sort(Double::compareTo);
        int k = (int) (alfa * (numbers.size() - 1) + 1);
        if (k > alfa * numbers.size()) {
            return numbers.get(k - 1);
        } else if (k > alfa * numbers.size()) {
            return numbers.get(k);
        }
        return (numbers.get(k - 1) + numbers.get(k)) / 2;
    }

    public static Double mathExpectation(ArrayList<Double> numbers, Map<Double, Double> pMap) throws IOException {
        ArrayList<Double> x_p = new ArrayList<>();
        HashSet<Double> set = new HashSet<>(numbers);
        for (Double n : set) {
            x_p.add(n * pMap.get(n));
        }
        Double sum = sum(null, x_p);
        System.out.println("Мат. ожидание: " + sum);
        return sum;
    }

    public static Map<Double, Double> probabilities(ArrayList<Double> numbers) {
        Map<Double, Double> map = new HashMap<>();
        ArrayList<Double> copyList, duplicates;
        for (Double d : numbers) {
            if (!map.containsKey(d)) {
                copyList = new ArrayList<>(numbers);
                duplicates = new ArrayList<>();
                duplicates.add(d);
                copyList.removeAll(duplicates);
                map.put(d, ((double) (numbers.size() - copyList.size())) / numbers.size());
            }
        }
        return map;
    }

    public static ArrayList<Double> readFile(String path) throws IOException {
        ArrayList<Double> numbers = new ArrayList<>();
        Files.lines(Paths.get(path)
                , StandardCharsets.UTF_8).forEach(k -> numbers.add(new Double(k)));
        return numbers;
    }

    public static Double sum(String path, ArrayList<Double> list) throws IOException {
        ArrayList<Double> numbers = null;
        if (path != null) {
            ArrayList<Double> finalNumbers = new ArrayList<>();
            Files.lines(Paths.get(path)
                    , StandardCharsets.UTF_8).forEach(k -> finalNumbers.add(new Double(k)));
            numbers = finalNumbers;
        } else {
            numbers = list;
        }
        Double sum = 0.0;
        for (Double n : numbers) {
            sum = sum + n;
        }
        return sum;
    }

   /* public static void main(String[] args) throws IOException {
        ArrayList<Double> general_server = readFile("src\\main\\resources\\general_server");
        ArrayList<Double> server_1 = readFile("src\\main\\resources\\server 1");
        ArrayList<Double> server_2 = readFile("src\\main\\resources\\server 2");
        Double meg = mathExpectation(general_server, probabilities(general_server));
        Double me1 = mathExpectation(server_1, probabilities(server_1));
        Double me2 = mathExpectation(server_2, probabilities(server_2));
        ArrayList<Double> list1 = new ArrayList<>();
        list1.add(me1);
        list1.add(me2);
        Double dme = StatisticsCalculator.sum(null, list1) / 2.0;
        System.out.println("Мат. ожидание, распр.: " + dme + '\n');

        StatisticsCalculator.dispersion(general_server, meg, probabilities(general_server));
        me1 = StatisticsCalculator.dispersion(server_1, me1, probabilities(server_1));
        me2 = StatisticsCalculator.dispersion(server_2, me2, probabilities(server_2));
        ArrayList<Double> list2 = new ArrayList<>();
        list2.add(me1);
        list2.add(me2);
        System.out.println("Дисперсия, распр.: " + StatisticsCalculator.sum(null, list2) / 2.0 + '\n');

        System.out.println("Квантиль: " + StatisticsCalculator.quantile("src\\main\\resources\\general_server", 0.8));
       *//* ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 23; i++) {
            list.add(String.valueOf(((int) ((49.0 + Math.random() * 2) * 100)) / 100.0));
        }
        Files.write(Paths.get("src\\\\main\\\\resources\\\\general_server"), list, StandardOpenOption.TRUNCATE_EXISTING);*//*
    }*/
}
