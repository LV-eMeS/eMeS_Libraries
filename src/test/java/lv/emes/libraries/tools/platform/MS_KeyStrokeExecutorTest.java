package lv.emes.libraries.tools.platform;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.awt.*;

import static junit.framework.TestCase.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MS_KeyStrokeExecutorTest {
	public static MS_KeyStrokeExecutor executorInstance = null;

    @BeforeClass
    public static void setUp() throws AWTException {
        executorInstance = new MS_KeyStrokeExecutor();
    }

    @AfterClass
    public static void tearDown() {

    }

    @Test
    public void test01KeyDown() {
        executorInstance.keyDown("shift");
        assertTrue(executorInstance.isKeyDown("shift"));
        executorInstance.keyUp("shift");

        executorInstance.keyDown("ctrl");
        assertTrue(executorInstance.isKeyDown("ctrl"));
        executorInstance.keyUp("ctrl");

        executorInstance.keyDown("alt");
        assertTrue(executorInstance.isKeyDown("alt"));
        executorInstance.keyUp("alt");

        //Following 2 blocks are not working at time due to Java bug.
//        executorInstance.keyDown("delete");
//        assertTrue(executorInstance.isKeyDown("delete"));
//        executorInstance.keyUp("delete");
//
//
//        executorInstance.keyDown("f4");
//        assertTrue(executorInstance.isKeyDown("f4"));
//        executorInstance.keyUp("f4");
    }

//    @Test
//    /**
//     * Not working at time due to Java bug.
//     */
//    public void test02CapsLock() throws InterruptedException {
//        if (executorInstance.isCapsLockToggled()) {
//            executorInstance.keyPress("caps");
////            Thread.sleep(5000);
//            assertFalse(executorInstance.isCapsLockToggled());
//        } else {
////            Thread.sleep(5000);
//            executorInstance.keyPress("caps");
//            assertTrue(executorInstance.isCapsLockToggled());
//        }
//        executorInstance.keyPress("caps"); //RETURN TO CURRENT STATE
//    }
}
