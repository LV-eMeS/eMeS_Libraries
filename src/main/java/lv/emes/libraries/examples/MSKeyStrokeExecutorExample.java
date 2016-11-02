package lv.emes.libraries.examples;

import lv.emes.libraries.tools.platform.MS_KeyStrokeExecutor;

import java.awt.*;

import static junit.framework.TestCase.assertTrue;

/** 
 * @version 1.0.
 * @author eMeS
 */
public class MSKeyStrokeExecutorExample {

	public static void main(String[] args) {
		System.out.println("Execution begins.");
		MS_KeyStrokeExecutor.getInstance().mouseSetCoords(new Point(111, 111));
		MS_KeyStrokeExecutor.getInstance().mouseMove(new Point(50, 50));
		MS_KeyStrokeExecutor.getInstance().keyPress("A");
		MS_KeyStrokeExecutor.getInstance().keyPress("a");

		MS_KeyStrokeExecutor.getInstance().keyDown("up");
		System.out.println(MS_KeyStrokeExecutor.getInstance().isKeyDown("up"));
		MS_KeyStrokeExecutor.getInstance().keyUp("up");
		System.out.println("Execution ends.");
	}
}
