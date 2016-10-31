package lv.emes.libraries.tools.platform;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.awt.*;

import static junit.framework.TestCase.assertFalse;
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

        insta.keyDown("up");
        assertTrue(insta.isKeyDown("up"));
        insta.keyUp("up");

        insta.keyDown("down");
        assertTrue(insta.isKeyDown("down"));
        insta.keyUp("down");

        insta.keyDown("right");
        assertTrue(insta.isKeyDown("right"));
        insta.keyUp("right");

        insta.keyDown("left");
        assertTrue(insta.isKeyDown("left"));
        insta.keyUp("left");

        insta.keyDown("pg_down");
        assertTrue(insta.isKeyDown("pg_down"));
        insta.keyUp("pg_down");

        insta.keyDown("pg_up");
        assertTrue(insta.isKeyDown("pg_up"));
        insta.keyUp("pg_up");
    }

    @Test
    public void test02CapsLock() throws InterruptedException {
        if (insta.isCapsLockToggled()) { //TODO check reasons why it isnt working
            insta.keyPress("caps");
//            Thread.sleep(5000);
            assertFalse(insta.isCapsLockToggled());
        } else {
//            Thread.sleep(5000);
            insta.keyPress("caps");
            assertTrue(insta.isCapsLockToggled());
        }
        insta.keyPress("caps"); //RETURN TO CURRENT STATE
    }
}
