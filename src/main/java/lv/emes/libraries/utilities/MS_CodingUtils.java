package lv.emes.libraries.utilities;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

/**
 * Module is designed to combine different common programming actions.
 *
 * @version 2.1.
 */
public final class MS_CodingUtils {

    private MS_CodingUtils() {
    }

    public static final String getSystemUserName = System.getProperty("user.name");
    public static final String getSystemUserCurrentWorkingDir = System.getProperty("user.dir") + "/";
    public static final String getSystemUserHomeDir = System.getProperty("user.home") + "/";
    public static final String getSystemOS = System.getProperty("os.name");


    public static String getIPAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "";
        }
    }

    public static int randomNumber(int aFrom, int aTill) {
        int swapper;
        if (aTill < aFrom) {
            swapper = aFrom;
            aFrom = aTill;
            aTill = swapper;
        }
        Random random = new Random();
        return random.nextInt(aTill - aFrom + 1) + aFrom;
    }

    /**
     * Checks if number <b>aNumber</b> is in interval [<b>aRangeMin</b>, <b>aRangeMax</b>].
     *
     * @param aNumber   = 5
     * @param aRangeMin = 1
     * @param aRangeMax = 5
     * @return true
     */
    public static boolean inRange(int aNumber, int aRangeMin, int aRangeMax) {
        return Math.min(aRangeMin, aRangeMax) <= aNumber && Math.max(aRangeMin, aRangeMax) >= aNumber;
    }

    /**
     * Simply prints object to console output using object's <b>toString</b> method.
     * Before <b>toString</b> method a text <code>"-----DEBUG: "+<b>text</b>+" -----"</code> will be printed to easy find this debug text in console window.
     *
     * @param obj  an object with <b>toString</b> method implemented.
     * @param text text to be printed with debug object (to use Ctrl + F to find text in console output).
     */
    public static void debugObjectConsoleOutput(Object obj, String text) {
        System.out.println("-----DEBUG: " + text + " -----");
        System.out.println(obj);
        System.out.println("-----DEBUG-----");
    }

    /**
     * Delays application activity for some time.
     * Method is using Thread.sleep().
     * <p><u>Warning</u>: do not use it anywhere else but main thread, cause it scuppers InterruptedException!
     *
     * @param miliseconds amount of miliseconds that will delay application.
     */
    public static void sleep(long miliseconds) {
        try {
            Thread.sleep(miliseconds);
        } catch (InterruptedException e) {
            return;
        }
    }

    /**
     * Delays application activity for some time.
     * Method is using Thread.sleep().
     *
     * @param miliseconds amount of miliseconds that will delay application.
     */
    public static void pause(long miliseconds) {
        sleep(miliseconds);
    }

    /**
     * Simply returns opposite value of given boolean variable <b>bool</b>.
     *
     * @param bool a boolean which we have to inverse.
     * @return boolean with opposite value of passed boolean.
     */
    public static Boolean inverseBoolean(Boolean bool) {
        return !bool;
    }

    /**
     * Asks user to input some line of string from console.
     *
     * @param askText text which will be printed to console before readln method.
     * @return user input line.
     */
    public static String readStringFromConsole(String askText) {
        System.out.println(askText);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            return br.readLine();
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * Simply reads user input line of string from console.
     *
     * @return user input line.
     */
    public static String readStringFromConsole() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            return br.readLine();
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * Helper method to show input dialog of desired input text field type.
     * @param inputTextField pre configured text field object (may be text or password field).
     * @param askText text which will be printed to pane label when dialog appears.
     * @return user input line.
     */
    private static String readStringFromUserInput(String askText, JTextField inputTextField) {
        //helper method
        JPanel panel = new JPanel();
        JLabel label = new JLabel(askText);
        //set focus to text field
        inputTextField.addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                JComponent component = event.getComponent();
                component.requestFocusInWindow();
                component.removeAncestorListener(this);
            }
            @Override
            public void ancestorRemoved(AncestorEvent event) {}
            @Override
            public void ancestorMoved(AncestorEvent event) {}
        });
        panel.add(label);
        panel.add(inputTextField);
        JOptionPane.showConfirmDialog(
                null, panel, "", JOptionPane.DEFAULT_OPTION);
        return String.valueOf(inputTextField.getText());
    }

    /**
     * Asks user to input some line of string from JOption pane.
     *
     * @param askText text which will be printed to pane label when dialog appears.
     * @return user input line.
     */
    public static String readStringFromJOptionPane(String askText) {
        return readStringFromUserInput(askText, new JTextField(10));
    }

    /**
     * Asks user to input some line of string from JOption pane, but user input will be masked with asterisk symbols (****).
     *
     * @param askText text which will be printed to pane label when dialog appears.
     * @return user input line.
     */
    public static String readStringFromJPasswordField(String askText) {
        return readStringFromUserInput(askText, new JPasswordField(10));
    }

    public static Double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();


        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * This method can be used to form constants that includes many different objects related to one particular concept.
     *
     * @param objects all the objects that will be included to result array.
     * @return an array of objects.
     */
    public static Object[] getArray(Object... objects) {
        return objects;
    }
}