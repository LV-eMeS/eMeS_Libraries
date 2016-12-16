package lv.emes.libraries.tools.platform;

import com.sun.jna.platform.KeyboardUtils;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import static lv.emes.libraries.tools.MS_KeyCodeDictionary.textToKeyCode;

/** 
 * Recognizes, what OS is device using and creates corresponding object to imitate keyboard button pressing and mouse controlling.
 * <p>Public methods:
 * <ul>
 *     <li>keyDown</li>
 *     <li>keyUp</li>
 *     <li>isKeyDown</li>
 *     <li>isCapsLockToggled</li>
 *     <li>getCurrentMouseCoords</li>
 *     <li>mouseLeftDown</li>
 *     <li>mouseLeftUp</li>
 *     <li>mouseLeftClick</li>
 *     <li>mouseRightDown</li>
 *     <li>mouseRightUp</li>
 *     <li>mouseRightClick</li>
 *     <li>mouseSetCoords</li>
 *     <li>mouseMove</li>
 * </ul>
 * @version 0.8.
 * @author eMeS
 */
public class MS_KeyStrokeExecutor implements IKeyStrokeExecutor {
	//PUBLIC STRUCTURES, EXCEPTIONS, PROPERTIES AND CONSTANTS

	//PRIVATE VARIABLES
	private static MS_KeyStrokeExecutor instanceVar = null;
	private Robot robot = null;

	//PUBLIC VARIABLES

	//CONSTRUCTORS
	/**
	 * Constructs robot for executing keystrokes and simulating mouse events.
	 * @throws AWTException if the platform configuration does not allow low-level input control.
	 * This exception is always thrown when GraphicsEnvironment.isHeadless() returns true.
	 */
	public MS_KeyStrokeExecutor() throws AWTException {
		robot = new Robot();
	}


	//STATIC CONSTRUCTORS
	/**
	 * If working with common executor instead of seperated instances.
	 * @return singletone instance of this object.
	 */
	public static MS_KeyStrokeExecutor getInstance() {
		if (instanceVar == null)
			try {
				instanceVar = new MS_KeyStrokeExecutor();
			} catch (AWTException e) {
				e.printStackTrace();
			}
		return instanceVar;
	}

	//PRIVATE METHODS
	/**
	 * From user input text generates valid key code.
	 * @param key String representing key value. Mapping is done using ...
	 * @return key code.
	 */
	private int translateKey(String key) {
		return textToKeyCode(key);
	}

	//PROTECTED METHODS

	//PUBLIC METHODS
	@Override
	public void keyDown(String key) {
		robot.keyPress(translateKey(key));
	}

	@Override
	public void keyUp(String key) {
		robot.keyRelease(translateKey(key));
	}

	@Override
	public void keyPress(String key) {
		keyDown(key);
		keyUp(key);
	}

	@Override
	public boolean isKeyDown(String key) {
		return KeyboardUtils.isPressed(translateKey(key));
	}

	@Override
	public boolean isCapsLockToggled() {
		return Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
	}

	@Override
	public void mouseLeftDown() {
		robot.mousePress(InputEvent.BUTTON1_MASK);
	}

	@Override
	public void mouseLeftUp() {
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
	}

	@Override
	public void mouseLeftClick() {
		mouseLeftDown();
		mouseLeftUp();
	}

	@Override
	public void mouseRightDown() {
		robot.mousePress(InputEvent.BUTTON3_MASK);
	}

	@Override
	public void mouseRightUp() {
		robot.mouseRelease(InputEvent.BUTTON3_MASK);
	}

	@Override
	public void mouseRightClick() {
		mouseRightDown();
		mouseRightUp();
	}

	@Override
	public void mouseWheelClick() {
		robot.mousePress(InputEvent.BUTTON2_MASK);
		robot.mouseRelease(InputEvent.BUTTON2_MASK);
	}

	@Override
	public void mouseSetCoords(Point coords) {
		robot.mouseMove(coords.x, coords.y);
	}

	@Override
	public void mouseMove(Point coords) {
		Point currentCoords = getCurrentMouseCoords();
		robot.mouseMove(currentCoords.x + coords.x, currentCoords.y + coords.y);
	}
	//STATIC METHODS

	@Override
	public Point getCurrentMouseCoords() {
		return MouseInfo.getPointerInfo().getLocation();
	}

	@Override
	public void mouseWheel(int steps) {
		robot.mouseWheel(steps);
	}
}
