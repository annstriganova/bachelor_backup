import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

public class StatisticsCalculatorTest {

    private ArrayList<Double> metric;

    @Before
    public void setUp() throws IOException {
        metric = new ArrayList<>();
        /*
        metric.add(0.5672);
        metric.add(0.9089);
        metric.add(0.9089);
        metric.add(0.9089);*/
        Files.lines(Paths.get("src\\main\\resources\\general_server")
                , StandardCharsets.UTF_8).forEach(k -> metric.add(new Double(k)));
    }

    @After
    public void tearDown(){
    }

    @Test
    public void dispersionTest() {
    }

    @Test
    public void quantileTest() {
    }

    @Test
    public void mathExpectationTest() throws IOException {
        StatisticsCalculator.mathExpectation(metric, StatisticsCalculator.probabilities(metric));
    }

    @Test
    public void probabilitiesTest() {
        Map<Double, Double> p;
        p = StatisticsCalculator.probabilities(metric);
        p.forEach((k, v) -> System.out.println("Value: " + k + " Probability: " + v));
    }

    @Test
    public void sumTest() {
    }
}