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
                "src\\main\\resources\\compaction\\uniform_39");
        /*FileInputStream fis = new FileInputStream("inverseMap.out");
        ObjectInputStream oin = new ObjectInputStream(fis);
        ac.inverseMap = (Map<ArithmeticCompaction.Segment, Integer>) oin.readObject();

        *//*fis = new FileInputStream("map.out");
        oin = new ObjectInputStream(fis);
        ac.map = (Map<Integer, ArithmeticCompaction.Segment>) oin.readObject();*//*

        HashMap<Integer, ArithmeticCompaction.Segment> uniform_39 = new HashMap<>();
        ac.inverseMap.forEach((k, v) -> uniform_39.put(v, k));
        ac.map = uniform_39;*/
    }

    @After
    public void tearDownTest() {
    }

    @Test
    public void compactionTest() {
        ac.compaction();
        //ac.codes.forEach(k ->
        // System.out.println("\nСжатие: " + k.code + " " + ArithmeticCompaction.getBits(k.code)));
        checkList = ac.decompaction();
        assertEquals(ac.numbers, checkList);
        assertEquals(checkList.size(), ac.numbers.size());
        assertTrue(checkList.containsAll(ac.numbers));
    }

    @Test
    public void decompactionTest() {
        for (int i = 1; i <= 32; i++) {
            System.out.println(Math.pow(2, i));
        }
    }

    @Test
    public void defineSegmentsTest() {
    }

    @Test
    public void findOptimalTest() {
        System.out.println(ArithmeticCompaction.findOptimal(0.7968410994305306, 0.7968410994320245));
        System.out.println(ArithmeticCompaction.getBits(ArithmeticCompaction.findOptimal(0.7968410994305306, 0.7968410994320245)));
        //System.out.println(ArithmeticCompaction.getBits(ArithmeticCompaction.
        //     findOptimal(0.35271965559999996, 0.3527199472)));
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