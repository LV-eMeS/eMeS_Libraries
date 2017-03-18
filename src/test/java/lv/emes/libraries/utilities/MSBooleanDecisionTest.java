package lv.emes.libraries.utilities;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MSBooleanDecisionTest {

    @Test
    public void test01Decision() {
        int result;

        result = MS_BooleanDecision.getCase(true, false);
        assertEquals(result, MS_BooleanDecision.FIRST);

        result = MS_BooleanDecision.getCase(false, true);
        assertEquals(result, MS_BooleanDecision.SECOND);

        result = MS_BooleanDecision.getCase(false, false);
        assertEquals(result, MS_BooleanDecision.NONE);

        result = MS_BooleanDecision.getCase(true, true);
        assertEquals(result, MS_BooleanDecision.BOTH);
    }
}
