package lv.emes.libraries.examples;

import lv.emes.libraries.tools.platform.MS_RobotKeyStrokeExecutor;

import java.awt.*;

/**
 * @version 1.0.
 * @author eMeS
 */
public class MSKeyStrokeExecutorExample {

	public static void main(String[] args) {
		System.out.println("Execution begins.");
		MS_RobotKeyStrokeExecutor.getInstance().mouseSetCoords(new Point(111, 111));
		MS_RobotKeyStrokeExecutor.getInstance().mouseMove(new Point(50, 50));
		MS_RobotKeyStrokeExecutor.getInstance().keyPress("A");
		MS_RobotKeyStrokeExecutor.getInstance().keyPress("a");

		MS_RobotKeyStrokeExecutor.getInstance().keyDown("up");
		System.out.println(MS_RobotKeyStrokeExecutor.getInstance().isKeyDown("up"));
		MS_RobotKeyStrokeExecutor.getInstance().keyUp("up");
		System.out.println("Execution ends.");

		MS_RobotKeyStrokeExecutor.getInstance().keyDown("win");
		MS_RobotKeyStrokeExecutor.getInstance().keyPress("D");
		MS_RobotKeyStrokeExecutor.getInstance().keyUp("win");
		System.out.println(MS_RobotKeyStrokeExecutor.getInstance().isCapsLockToggled());
	}
}
