package lv.emes.libraries.tools.platform;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.awt.*;

import static junit.framework.TestCase.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MSKeyStrokeExecutorTest {
	public static MS_KeyStrokeExecutor insta = null;

    @BeforeClass
    public static void setUp() throws AWTException {
        insta = new MS_KeyStrokeExecutor();
    }

    @AfterClass
    public static void tearDown() {

    }

    @Test
    public void test01KeyDown() {
        insta.keyDown("shift");
        assertTrue(insta.isKeyDown("shift"));
        insta.keyUp("shift");

        insta.keyDown("ctrl");
        assertTrue(insta.isKeyDown("ctrl"));
        insta.keyUp("ctrl");

        insta.keyDown("alt");
        assertTrue(insta.isKeyDown("alt"));
        insta.keyUp("alt");
    }

//    @Test
//    /**
//     * Not working at time due to Java bug.
//     */
//    public void test02CapsLock() throws InterruptedException {
//        if (insta.isCapsLockToggled()) {
//            insta.keyPress("caps");
////            Thread.sleep(5000);
//            assertFalse(insta.isCapsLockToggled());
//        } else {
////            Thread.sleep(5000);
//            insta.keyPress("caps");
//            assertTrue(insta.isCapsLockToggled());
//        }
//        insta.keyPress("caps"); //RETURN TO CURRENT STATE
//    }
}
