package lv.emes.libraries.tools;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author eMeS
 */
public class MS_BooleanFlagTest {

    @Test
    public void testBehavior() {
        MS_BooleanFlag flag = new MS_BooleanFlag(true);
        assertTrue(flag.get());

        flag.setFalse();
        assertFalse(flag.get());

        flag.setTrue();
        assertTrue(flag.get());
    }

    @Test
    public void testSetForOnce() {
        MS_BooleanFlag flag = new MS_BooleanFlag(false);
        assertFalse(flag.get());

        flag.setForOnce(true);
        //test that this does return new value, no matter, how many times we call getUntouched
        assertTrue(flag.getUntouched());
        assertTrue(flag.getUntouched());
        assertTrue(flag.getUntouched());

        //now test, if after second call value will change back to original - false
        assertTrue(flag.get());
        assertFalse(flag.get());
        assertFalse(flag.getUntouched()); //and untouched now is false
    }
}