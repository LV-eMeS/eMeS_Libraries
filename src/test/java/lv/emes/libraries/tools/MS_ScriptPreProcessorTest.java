package lv.emes.libraries.tools;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

public class MS_ScriptPreProcessorTest {

    private static final MS_ScriptPreProcessor preProcessor = new MS_ScriptPreProcessor();

    @Test
    public void testApply() {
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThatThrownBy(() -> preProcessor.apply(null)).isInstanceOf(NullPointerException.class);
            softly.assertThat(preProcessor.apply("")).isEqualTo("");
            softly.assertThat(preProcessor.apply("Integer(5)")).isEqualTo("5");
            softly.assertThat(preProcessor.apply("Integer(5 + 5)")).isEqualTo("10");
            softly.assertThat(preProcessor.apply("Integer(5 - 5)")).isEqualTo("0");
            softly.assertThat(preProcessor.apply("Integer(5 * 5)")).isEqualTo("25");
            softly.assertThat(preProcessor.apply("Integer(5 / 5)")).isEqualTo("1");
            softly.assertThat(preProcessor.apply("Integer(5-1) Integer(5/5)")).isEqualTo("4 1");
            softly.assertThat(preProcessor.apply("Integer(5-1)Integer(5/5)")).isEqualTo("41");
            softly.assertThat(preProcessor.apply("37 * Integer(5-1)#Integer(5/5)")).isEqualTo("37 * 4#1");
            softly.assertThat(preProcessor.apply("Integer(5-1)#Integer(5/5) + 47")).isEqualTo("4#1 + 47");
            softly.assertThat(preProcessor.apply("abcd#Integer(5-1)# Integer(5/5) + 983")).isEqualTo("abcd#4# 1 + 983");
            // Performs pairs of operations sequentially; arithmetic rules regarding operation order are not respected by preProcessor
            softly.assertThat(preProcessor.apply("Integer(2 * 10 + 15 / 5)")).isEqualTo("7"); // = 20 + 15 / 5 = 35 / 5 = 7
            // recursion is not supported
//            softly.assertThat(preProcessor.apply("mc#Integer(283 + Integer(Integer(3-1) * 183))&440#ML#")).isEqualTo("mc#649&440#ML#");
        });
    }
}