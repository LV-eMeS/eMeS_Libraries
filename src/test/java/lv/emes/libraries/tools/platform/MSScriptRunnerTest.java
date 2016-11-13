package lv.emes.libraries.tools.platform;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * @author eMeS
 * @version 1.0.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MSScriptRunnerTest {
    private MS_ScriptRunner runner = new MS_ScriptRunner();

    @Test
    public void test01NameOfTest() {
//        MS_ScriptRunner.runScript("test#omm#;ammm;#di#100d0#a#b#c#pause#50#yo!#1#2#3#4");
        //TODO check one more time
        MS_ScriptRunner.runScript("di#5000#ml#mr#mld#pause#mlu");
    }
}