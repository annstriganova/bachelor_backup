package compaction;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ArithmeticCompactionTest {

    private ArithmeticCompaction ac;
    private ArrayList<Integer> checkList;

    @Before
    public void setUpTest() throws Exception {
        ac = new ArithmeticCompaction("C:\\IdeaProjects\\bachelor_paper\\" +
                "src\\main\\resources\\compaction\\normal");
    }

    @After
    public void tearDownTest() {
    }

    @Test
    public void compactionTest() {
        ac.compaction();
        //ac.codes.forEach(k ->
        // System.out.println("\nСжатие: " + k.code + " " + compaction.ArithmeticCompaction.getBits(k.code)));
        checkList = ac.decompaction();
        assertEquals(ac.numbers, checkList);
        assertEquals(checkList.size(), ac.numbers.size());
        assertTrue(checkList.containsAll(ac.numbers));
    }

    @Test
    public void decompactionTest() {

    }

    @Test
    public void defineSegmentsTest() {
    }

    @Test
    public void findOptimalTest() {

    }


    @Test
    public void getBitsTest() {
        System.out.println(ArithmeticCompaction.getBits(0.07524));
        System.out.println(ArithmeticCompaction.getBits(0.07524197279316319));
        System.out.println(ArithmeticCompaction.getBits(0.07612717247308276));
    }

    @Test
    public void probabilitiesTest() {

    }

    @Test
    public void toDigitsTest() {
    }
}