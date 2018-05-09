import compaction.ArithmeticCompaction;

import java.io.IOException;

public class Benchmark {

    private ArithmeticCompaction ac;

    public Benchmark(int size) throws IOException {
        ac = new ArithmeticCompaction("C:\\IdeaProjects\\bachelor_paper\\" +
                "src\\main\\resources\\compaction\\normal\\" + size + "_500_20");
    }

    public void timeTest() {
        long start = System.currentTimeMillis();
        for (int i = 1; i <= 1000000; i++) {
            ac.compaction();
        }
        long end = System.currentTimeMillis();
        System.out.println("\nКодирование: " + (end - start));

        start = System.currentTimeMillis();
        for (int i = 1; i <= 1000000; i++) {
            ac.decompaction();
        }
        end = System.currentTimeMillis();
        System.out.println("Декодирование: " + (end - start));

    }

    public static void main(String[] args) throws IOException {
        Benchmark b = new Benchmark(1000);
        b.timeTest();
    }
}
