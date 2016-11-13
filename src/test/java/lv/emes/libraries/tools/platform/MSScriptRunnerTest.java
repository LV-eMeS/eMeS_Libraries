package lv.emes.libraries.tools.platform;

import org.junit.*;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertTrue;

/**
 * @author eMeS
 * @version 1.0.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MSScriptRunnerTest {
    private MS_ScriptRunner runner = new MS_ScriptRunner();

    @Test
    public void test01NameOfTest() {
        MS_ScriptRunner.runScript("test#omm#;ammm;#di#100d0#a#b#c#pause#50#yo!#1#2#3#4");
        assertTrue(true);
    }
}
