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

    private static String file = "1000_1000_20";

    @State(Scope.Thread)
    public static class CompactionState {
        public ArithmeticCompaction ac;

        @Setup(Level.Trial)
        public void doSetup() throws IOException {
            ac = new ArithmeticCompaction("C:\\IdeaProjects\\bachelor_paper\\" +
                    "src\\main\\resources\\compaction\\normal\\" + file);
        }

        public CompactionState() {
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime) // Среднее время выполнения операции
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public ArithmeticCompaction compactionTest(CompactionState cs) {
        cs.ac.compaction();
        return cs.ac;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime) // Среднее время выполнения операции
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public ArithmeticCompaction compactionDefaultTest(CompactionState cs) {
        cs.ac.compactionDefault();
        return cs.ac;
    }

    public static void main(String[] args) throws Exception {
        //org.openjdk.jmh.Main.main(args);
        Options opt = new OptionsBuilder()
                .include(BenchmarkRunner.class.getSimpleName())
                .resultFormat(ResultFormatType.TEXT)
                .result("C:\\IdeaProjects\\bachelor_paper\\src\\main\\java\\benchmark\\results\\" + file + ".txt")
                .build();

        new Runner(opt).run();
    }
}