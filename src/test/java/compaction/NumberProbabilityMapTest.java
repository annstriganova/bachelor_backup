package compaction;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

public class NumberProbabilityMapTest {
    NumberProbabilityMap sp;

    @Before
    public void setUp() {
        sp = new NumberProbabilityMap();
        sp.put(1, BigDecimal.valueOf(0.5));
        sp.put(2, BigDecimal.valueOf(0.1));
    }

    @Test
    public void getTest() {
        System.out.println(sp.get(2));
        System.out.println(sp.get(1));
    }

    @Test
    public void putTest() {
        sp.put(1, BigDecimal.valueOf(0.5));
        sp.put(2, BigDecimal.valueOf(0.1));
    }

    @Test
    public void keySetTest() {
        sp.keyArray().forEach(System.out::println);
    }

    @Test
    public void containsKeyTest() {
        System.out.println(sp.containsKey(2));
        System.out.println(sp.containsKey(1));
        System.out.println(sp.containsKey(3));
    }

    @Test
    public void iteratorTest() {
        for (NumberProbabilityMap.Entry e : sp) {
            System.out.println(e.number + " " + e.probability);
        }
    }
}