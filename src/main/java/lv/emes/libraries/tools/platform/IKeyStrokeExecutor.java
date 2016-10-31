package lv.emes.libraries.tools.platform;

import java.awt.Point;

/** 
 * An interface that describes actions to be done to imitate keyboard button pressing or mouse controlling.
 * <p>Public methods:
 * -keyDown
 * -keyUp
 * -keyPress
 * -isKeyDown
 * -isCapsLockToggled
 * -getCurrentMouseCoords
 * -mouseLeftDown
 * -mouseLeftUp
 * -mouseLeftClick
 * -mouseRightDown
 * -mouseRightUp
 * -mouseRightClick
 * -mouseSetCoords
 * -mouseMove
 * -mouseWheel
 * @version 1.0.
 * @author eMeS
 */
public interface IKeyStrokeExecutor {
	/**
	 * Imitates keyboard key pressing down and hold it.
	 * @param key code of valid keyboard key.
	 */
	public void keyDown(String key);
	/**
	 * Imitates keyboard key releasing.
	 * @param key code of valid keyboard key.
	 */
	public void keyUp(String key);
	/**
	 * Imitates keyboard key hitting (pressing down and releasing it immediately).
	 * @param key code of valid keyboard key.
	 */
	public void keyPress(String key);
	
	/**
	 * Tests, if key of keyboard is pressed.
	 * @param key code of valid keyboard key.
	 */
	public boolean isKeyDown(String key);
	
	/**
	 * Tests, if Caps Lock key is toggled.
	 * @return true if caps lock on.
	 */
	public boolean isCapsLockToggled();
	
	/**
	 * @return mouse pointer coordinates in current position.
	 */
	public Point getCurrentMouseCoords();
	
	/**
	 * Do mouse left click and hold it.
	 */
	public void mouseLeftDown();
	
	/**
	 * Release mouse left click.
	 */
	public void mouseLeftUp();
	
	/**
	 * Do mouse left click and release it immediately.
	 */
	public void mouseLeftClick();
	
	/**
	 * Do mouse right click and hold it.
	 */
	public void mouseRightDown();
	
	/**
	 * Release mouse right click.
	 */
	public void mouseRightUp();
	
	/**
	 * Do mouse right click and release it immediately.
	 */
	public void mouseRightClick();
	
	/**
	 * Sets mouse pointer to a specific coordinates on the screen (be aware of user's screen resolution!).
	 * @param coords X starting from screen's left edge with value 0; Y starting from screen's top edge with value 0.
	 */
	public void mouseSetCoords(Point coords);
	/**
	 * Moves mouse for specified distance. Simply moves mouse pointer for (X, Y) pixels.
	 * @param coords X starting from screen's left edge with value 0; Y starting from screen's top edge with value 0.
	 */
	public void mouseMove(Point coords);
	
	/**
	 * Rotates mouse wheel up or down for count of <b>steps</b>. 
	 * @param steps wheel up if given value is negative and down, if value is positive.
	 */
	public void mouseWheel(int steps);
}