package lv.emes.libraries.utilities;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MSBooleanDecisionTest {

    @Test
    public void test01Decision() {
        String result;

        result = MS_BooleanDecision.getCase(true, false);
        assertEquals(MS_BooleanDecision._FIRST, result);

        result = MS_BooleanDecision.getCase(false, true);
        assertEquals(MS_BooleanDecision._SECOND, result);

        result = MS_BooleanDecision.getCase(false, false);
        assertEquals(MS_BooleanDecision._NONE, result);

        result = MS_BooleanDecision.getCase(true, true);
        assertEquals(MS_BooleanDecision._BOTH, result);

        assertEquals(2, result.length());
    }

    @Test
    public void test02ComplicatedDecision3Levels() {
        String result;

        result = MS_BooleanDecision.getCase(false, false, false);
        assertEquals(MS_BooleanDecision._NONE_OF_3, result);

        result = MS_BooleanDecision.getCase(true, false, false);
        assertEquals(MS_BooleanDecision._FIRST_OF_3, result);

        result = MS_BooleanDecision.getCase(false, true, false);
        assertEquals(MS_BooleanDecision._SECOND_OF_3, result);

        result = MS_BooleanDecision.getCase(false, false, true);
        assertEquals(MS_BooleanDecision._THIRD_OF_3, result);

        result = MS_BooleanDecision.getCase(true, true, false);
        assertEquals(MS_BooleanDecision._FIRST_AND_SECOND_OF_3, result);

        result = MS_BooleanDecision.getCase(false, true, true);
        assertEquals(MS_BooleanDecision._SECOND_AND_THIRD_OF_3, result);

        result = MS_BooleanDecision.getCase(true, false, true);
        assertEquals(MS_BooleanDecision._FIRST_AND_THIRD_OF_3, result);

        result = MS_BooleanDecision.getCase(true, true, true);
        assertEquals(MS_BooleanDecision._ALL_3, result);

        assertEquals(3, result.length());
    }

    @Test
    public void test03ComplicatedDecision4Levels() {
        String result;

        result = MS_BooleanDecision.getCase(true, false, false, true);
        assertEquals("1001", result);

        result = MS_BooleanDecision.getCase(false, true, false, true);
        assertEquals("0101", result);

        result = MS_BooleanDecision.getCase(false, false, false, true);
        assertEquals("0001", result);

        result = MS_BooleanDecision.getCase(true, true, true, true);
        assertEquals("1111", result);

        assertEquals(4, result.length());
    }
}
