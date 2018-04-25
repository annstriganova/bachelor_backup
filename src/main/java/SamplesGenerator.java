import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class SamplesGenerator {

    public static ArrayList<Double> generateSamples(int size) {
        ArrayList<Double> samples = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            samples.add(((int) ((49.0 + Math.random() * 2) * 1000)) / 1000.0);
        }
        return samples;
    }

    public static void writeToFile(ArrayList<Double> samples) throws IOException {
        ArrayList<String> samplesString = new ArrayList<>();
        for (Double d : samples) {
            samplesString.add(String.valueOf(d));
        }
        Files.write(Paths.get("src\\main\\resources\\general_server"), samplesString, StandardOpenOption.TRUNCATE_EXISTING);
        ArrayList<String> greater = new ArrayList<>();
        ArrayList<String> less = new ArrayList<>();
        double mE = StatisticsCalculator.mathExpectation(samples, StatisticsCalculator.probabilities(samples));
        samples.forEach(k -> {
            if (k > mE) {
                greater.add(String.valueOf(k));
            }else{
                less.add(String.valueOf(k));
            }
        });
        Files.write(Paths.get("src\\main\\resources\\server 1"), greater, StandardOpenOption.TRUNCATE_EXISTING);
        Files.write(Paths.get("src\\main\\resources\\server 2"), less, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public static void main(String[] args) throws IOException {
        writeToFile(generateSamples(26));
    }
}
