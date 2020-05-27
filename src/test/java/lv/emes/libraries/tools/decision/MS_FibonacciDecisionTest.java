package lv.emes.libraries.tools.decision;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MS_FibonacciDecisionTest {

    @Test
    public void testFibonacciDecision() {
        MS_FibonacciDecision decision = new MS_FibonacciDecision();
        assertThat(decision.getCurrentState()).isEqualTo(1L);
        assertThat(decision.getCurrentDecision()).isEqualTo(true);

        assertThat(decision.test()).isTrue();
        assertThat(decision.getCurrentState()).isEqualTo(2L);

        assertThat(decision.test()).isTrue();
        assertThat(decision.getCurrentState()).isEqualTo(3L);

        assertThat(decision.test()).isFalse();
        assertThat(decision.getCurrentState()).isEqualTo(4L);
        assertThat(decision.test()).isTrue();
        assertThat(decision.getCurrentState()).isEqualTo(5L);

        assertThat(decision.test()).isFalse();
        assertThat(decision.getCurrentState()).isEqualTo(6L);
        assertThat(decision.test()).isFalse();
        assertThat(decision.getCurrentState()).isEqualTo(7L);
        assertThat(decision.test()).isTrue();
        assertThat(decision.getCurrentState()).isEqualTo(8L);
    }
}