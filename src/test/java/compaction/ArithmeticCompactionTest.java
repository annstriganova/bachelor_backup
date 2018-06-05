package compaction;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ArithmeticCompactionTest {

    private ArithmeticCompaction ac;
    private ArrayList<Integer> checkList;

    @Before
    public void setUpTest() throws Exception {
        ac = new ArithmeticCompaction("C:\\IdeaProjects\\bachelor_paper\\" +
                "src\\main\\resources\\compaction\\normal\\100000_1000_10");
    }

    @Test
    public void compactionAdaptiveTest() {
        ac.compactionAdaptive();
        checkList = ac.decompaction();
        assertEquals(ac.numbers, checkList);
        assertEquals(checkList.size(), ac.numbers.size());
        assertTrue(checkList.containsAll(ac.numbers));
    }

    @Test
    public void compactionOptimalTest() {
        ac.compactionOptimal();
        checkList = ac.decompaction();
        assertEquals(ac.numbers, checkList);
        assertEquals(checkList.size(), ac.numbers.size());
        assertTrue(checkList.containsAll(ac.numbers));
    }

    @Test
    public void compactionClassicTest() {
        ac.compactionClassic();
        checkList = ac.decompaction();
        assertEquals(ac.numbers, checkList);
        assertEquals(checkList.size(), ac.numbers.size());
        assertTrue(checkList.containsAll(ac.numbers));
    }

    @Test
    public void findOptimalTest() {
        Code code1 = ac.findOptimal(BigDecimal.valueOf(0.2), BigDecimal.valueOf(0.3));
        Code code2 = ac.findOptimal(BigDecimal.valueOf(0.1), BigDecimal.valueOf(0.2));
        Code code3 = ac.findOptimal(BigDecimal.valueOf(0.4), BigDecimal.valueOf(0.6));
        assertEquals(BigDecimal.valueOf(0.25),code1.code);
        assertEquals(BigDecimal.valueOf(0.125),code2.code);
        assertEquals(BigDecimal.valueOf(0.5),code3.code);
    }


    @Test
    public void getBitsTest() {
        System.out.println(ac.getBits(BigDecimal.valueOf(0.5)));
        System.out.println(ac.getBits(BigDecimal.valueOf(0.07524)));
        /*System.out.println(ac.getBits(BigDecimal.valueOf(0.07524)));
        System.out.println(ac.getBits(BigDecimal.valueOf(0.07524197279316319)));
        System.out.println(ac.getBits(BigDecimal.valueOf(0.07612717247308276)));*/
    }

    @Test
    public void calculateBitsOfTest(){
        //System.out.println(ac.calculateBitsOf(BigDecimal.valueOf(0.25)));
    }
}