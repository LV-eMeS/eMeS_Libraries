package lv.emes.libraries.examples;

import lv.emes.libraries.tools.platform.MS_KeyStrokeExecutor;

import java.awt.*;

/** 
 * @version 1.0.
 * @author eMeS
 */
public class MSKeyStrokeExecutorExample {

	public static void main(String[] args) {
		System.out.println("Execution begins.");
		Robot rob = null;
		MS_KeyStrokeExecutor.getInstance().mouseSetCoords(new Point(111, 111));
		MS_KeyStrokeExecutor.getInstance().mouseMove(new Point(50, 50));
//		rob.mouseMove(111, 111);
//		MS_KeyStrokeExecutor.getInstance().keyPress("A");
//		rob.keyPress(65);
//		rob.keyRelease(65);
		System.out.println("Execution ends.");
	}
}
