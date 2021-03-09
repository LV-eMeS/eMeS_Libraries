package lv.emes.libraries.tools;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author eMeS
 */
public class MS_BooleanFlagTest {

    @Test
    public void testBehavior() {
        MS_BooleanFlag flag = new MS_BooleanFlag(true);
        assertThat(flag.get()).isTrue();

        flag.setFalse();
        assertThat(flag.get()).isFalse();

        flag.setTrue();
        assertThat(flag.get()).isTrue();
    }

    @Test
    public void testSetForOnce() {
        MS_BooleanFlag flag = new MS_BooleanFlag(false);
        assertThat(flag.get()).isFalse();

        flag.setForOnce(true);
        //test that this does return new value, no matter, how many times we call getUntouched
        assertThat(flag.getUntouched()).isTrue();
        assertThat(flag.getUntouched()).isTrue();
        assertThat(flag.getUntouched()).isTrue();

        //now test, if after second call value will change back to original - false
        assertThat(flag.get()).isTrue();
        assertThat(flag.get()).isFalse();
        assertThat(flag.getUntouched()).isFalse(); //and untouched now is false
    }
}