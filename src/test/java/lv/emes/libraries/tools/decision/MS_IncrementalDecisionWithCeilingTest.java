package lv.emes.libraries.tools.decision;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MS_IncrementalDecisionWithCeilingTest {

    @Test
    public void testDecision() {
        MS_IncrementalDecisionWithCeiling decision = new MS_IncrementalDecisionWithCeiling(3L);
        assertThat(decision.getCurrentState()).isEqualTo(0L);
        assertThat(decision.getCurrentDecision()).isEqualTo(true);

        assertThat(decision.test()).isTrue();
        assertThat(decision.getCurrentState()).isEqualTo(1L);

        assertThat(decision.test()).isTrue();
        assertThat(decision.getCurrentState()).isEqualTo(2L);

        assertThat(decision.test()).isTrue();
        assertThat(decision.getCurrentState()).isEqualTo(3L);

        assertThat(decision.test()).isFalse();
        assertThat(decision.getCurrentState()).isEqualTo(4L);
    }
}