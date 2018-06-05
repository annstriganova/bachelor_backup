package benchmark;

import compaction.ArithmeticCompaction;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class BenchmarkRunner {

    @State(Scope.Benchmark)
    public static class CompactionState {
        ArithmeticCompaction ac;
        @Param({"10", "20", "30", "40", "50", "60", "70", "80", "90", "100", "150"})
        public String fileName;

        @Setup(Level.Trial)
        public void doSetup() throws IOException {
            ac = new ArithmeticCompaction("C:\\IdeaProjects\\bachelor_paper\\" +
                    "src\\main\\resources\\compaction\\normal\\100000_1000_" + fileName);
        }

        public CompactionState() {
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime) // Среднее время выполнения операции
    @Fork(value = 4) // Количество JVM
    @Warmup(iterations = 5) // Количество "холостых" вызовов
    @Measurement(iterations = 6) // Количество для измерения
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public ArithmeticCompaction compactionClassicTest(CompactionState cs) {
        cs.ac.compactionClassic();
        return cs.ac;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime) // Среднее время выполнения операции
    @Fork(value = 4) // Количество JVM
    @Warmup(iterations = 5) // Количество "холостых" вызовов
    @Measurement(iterations = 6) // Количество для измерения
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public ArithmeticCompaction compactionOptimalTest(CompactionState cs) {
        cs.ac.compactionOptimal();
        return cs.ac;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime) // Среднее время выполнения операции
    @Fork(value = 4) // Количество JVM
    @Warmup(iterations = 5) // Количество "холостых" вызовов
    @Measurement(iterations = 6) // Количество для измерения
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public ArithmeticCompaction compactionAdaptiveTest(CompactionState cs) {
        cs.ac.compactionAdaptive();
        return cs.ac;
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(BenchmarkRunner.class.getSimpleName())
                .resultFormat(ResultFormatType.TEXT)
                .result("C:\\IdeaProjects\\bachelor_paper\\src\\main\\java\\benchmark\\results\\100000_1000.txt")
                .build();

        new Runner(opt).run();
    }
}