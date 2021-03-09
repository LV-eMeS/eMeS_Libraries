package lv.emes.libraries.utilities;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.assertj.core.api.Assertions.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MS_BooleanDecisionTest {

    @Test
    public void test01Decision() {
        String result;

        result = MS_BooleanDecision.getCase(true, false);
        assertThat(result).isEqualTo(MS_BooleanDecision._FIRST);

        result = MS_BooleanDecision.getCase(false, true);
        assertThat(result).isEqualTo(MS_BooleanDecision._SECOND);

        result = MS_BooleanDecision.getCase(false, false);
        assertThat(result).isEqualTo(MS_BooleanDecision._NONE);

        result = MS_BooleanDecision.getCase(true, true);
        assertThat(result).isEqualTo(MS_BooleanDecision._BOTH);

        assertThat(result.length()).isEqualTo(2);
    }

    @Test
    public void test02ComplicatedDecision3Levels() {
        String result;

        result = MS_BooleanDecision.getCase(false, false, false);
        assertThat(result).isEqualTo(MS_BooleanDecision._NONE_OF_3);

        result = MS_BooleanDecision.getCase(true, false, false);
        assertThat(result).isEqualTo(MS_BooleanDecision._FIRST_OF_3);

        result = MS_BooleanDecision.getCase(false, true, false);
        assertThat(result).isEqualTo(MS_BooleanDecision._SECOND_OF_3);

        result = MS_BooleanDecision.getCase(false, false, true);
        assertThat(result).isEqualTo(MS_BooleanDecision._THIRD_OF_3);

        result = MS_BooleanDecision.getCase(true, true, false);
        assertThat(result).isEqualTo(MS_BooleanDecision._FIRST_AND_SECOND_OF_3);

        result = MS_BooleanDecision.getCase(false, true, true);
        assertThat(result).isEqualTo(MS_BooleanDecision._SECOND_AND_THIRD_OF_3);

        result = MS_BooleanDecision.getCase(true, false, true);
        assertThat(result).isEqualTo(MS_BooleanDecision._FIRST_AND_THIRD_OF_3);

        result = MS_BooleanDecision.getCase(true, true, true);
        assertThat(result).isEqualTo(MS_BooleanDecision._ALL_3);

        assertThat(result.length()).isEqualTo(3);
    }

    @Test
    public void test03ComplicatedDecision4Levels() {
        String result;

        result = MS_BooleanDecision.getCase(true, false, false, true);
        assertThat(result).isEqualTo("1001");

        result = MS_BooleanDecision.getCase(false, true, false, true);
        assertThat(result).isEqualTo("0101");

        result = MS_BooleanDecision.getCase(false, false, false, true);
        assertThat(result).isEqualTo("0001");

        result = MS_BooleanDecision.getCase(true, true, true, true);
        assertThat(result).isEqualTo("1111");

        assertThat(result.length()).isEqualTo(4);
    }
}
