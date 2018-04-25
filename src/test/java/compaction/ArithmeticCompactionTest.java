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
                "src\\main\\resources\\compaction\\normal_30");
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
    public void findOptimalTest() {
        System.out.println(ac.findOptimal(BigDecimal.valueOf(0.1), BigDecimal.valueOf(0.2)));

        BigDecimal left = new BigDecimal("0.5334126427409100916188250436078511067074548230478240918559162729258751206625006350745336135142000711273687954072041863537452880687293603617334755880709241477417033146150993242899964436315602296397906825052263780927704110145811106030584768581820609358329522938576436518823350099070261717827566935934562820708225372148554590358278717675151145658690240308896001625760639468915646327626208741892326711714");
        BigDecimal righ = new BigDecimal("0.5334126427409371877593185320665887652622144294763833155514911344815322867449067723503553887720367830107199105827363714881252098286643296245491032871005436163186478860725549966976578773560940913478636388635051262510796118477874307778285830411826198597774729461972260326169791190367320092872021541431692323324696438551033887213280495859371030838794899151552100797634879506850243018510050974614303375163");
        System.out.println(ac.findOptimal(left,righ));
    }


    @Test
    public void getBitsTest() {
        System.out.println(ArithmeticCompaction.getBits(0.07524));
        System.out.println(ArithmeticCompaction.getBits(0.07524197279316319));
        System.out.println(ArithmeticCompaction.getBits(0.07612717247308276));
    }
}