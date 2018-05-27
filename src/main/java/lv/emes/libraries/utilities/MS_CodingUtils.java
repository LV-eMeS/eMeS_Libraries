package lv.emes.libraries.utilities;

import lv.emes.libraries.tools.IFuncAction;
import lv.emes.libraries.tools.MS_BadSetupException;
import lv.emes.libraries.tools.lists.MS_ListActionWorker;
import org.apache.commons.lang.StringUtils;

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
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

/**
 * Module is designed to combine different common quick coding operations.
 *
 * @version 2.5.
 */
public final class MS_CodingUtils {

    private MS_CodingUtils() {
    }

    public static final String getSystemUserName = System.getProperty("user.name");
    public static final String getSystemUserCurrentWorkingDir = System.getProperty("user.dir") + "/";
    public static final String getSystemUserHomeDir = System.getProperty("user.home") + "/";
    public static final String getSystemOS = System.getProperty("os.name");

    /**
     * @return IP address of this workstation or empty string "" in case host address cannot be determined.
     */
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
        return new Random().nextInt(aTill - aFrom + 1) + aFrom;
    }

    /**
     * Checks if number <b>aNumber</b> is in interval [<b>aRangeMin</b>, <b>aRangeMax</b>].
     *
     * @param aNumber   [5]
     * @param aRangeMin = [1]
     * @param aRangeMax = [5]
     * @return [true]
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
        } catch (InterruptedException ignored) {
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
    public static boolean inverseBoolean(boolean bool) {
        return !bool;
    }

    /**
     * Converts boolean to character '1' or '0'.
     *
     * @param bool true = '1'; false = '0'.
     * @return character as a number ('1' or '0') representing boolean value.
     */
    public static Character booleanToChar(boolean bool) {
        return bool ? '1' : '0';
    }

    /**
     * Converts character to boolean true or false.
     *
     * @param charValue '1' = true; '0' = false.
     * @return boolean representing character, as a number ('1' or '0'), value.
     */
    public static Boolean charToBoolean(char charValue) {
        return charValue == '1';
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
     *
     * @param inputTextField pre configured text field object (may be text or password field).
     * @param askText        text which will be printed to pane label when dialog appears.
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
            public void ancestorRemoved(AncestorEvent event) {
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
            }
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

    public static double round(double value, int precision) {
        if (precision < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(precision, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * This function removes fractional part for negative and positive numbers.
     *
     * @param value [1.234] [-1.567] [9.999999999999999999999999999999999999999999999999999]
     * @return [1.0] [-1.0] [9.0]
     */
    public static long truncate(Double value) {
        return new Long(StringUtils.substringBefore(value.toString(), "."));
    }

    /**
     * This function removes integer part from <b>value</b> leaving only fractional part.
     * To assure that, while performing subtracting operation between 2 double values, actual fractional part will
     * not get malformed, <b>precision</b> is applied to this method.
     * It should be closest number, of how long fractional part will be.
     *
     * @param value     [3.14] [-10.1] [0.0]
     * @param precision [2] [1] [0] <u>Note</u>: in case rounding is necessary while getting fractional part,
     *                  rounding down is performed.
     * @return [0.14000000000000012] [-0.1] [0.0]
     */
    public static double fractionalPart(double value, int precision) {
        boolean negative = false;
        double res;

        if (value < 0) {
            negative = true;
            value = -value;
        }

        BigDecimal valueWrapper = new BigDecimal(value).setScale(precision, RoundingMode.HALF_DOWN);
        valueWrapper = valueWrapper.subtract(new BigDecimal(truncate(value)));
        res = valueWrapper.doubleValue();
        if (negative)
            res = -res;
        return res;
    }

    /**
     * This method can be used to form constants that includes many different objects related to one particular concept.
     *
     * @param objects all the objects that will be included to result array.
     * @return an array of objects.
     */
    public static Object[] newArray(Object... objects) {
        return objects;
    }

    /**
     * Creates hash map with just one entry.
     *
     * @param key   key of entry.
     * @param value value of entry.
     * @param <K>   type of key.
     * @param <V>   type of value.
     * @return new map with presented key and value.
     */
    public static <K, V> Map<K, V> newSingletonMap(K key, V value) {
        Map<K, V> res = new HashMap<>();
        res.put(key, value);
        return res;
    }

    /**
     * Iterates through iterable elements and performs given action <b>action</b> while <b>breakLoop</b> flag,
     * which is passed as second argument of <b>action</b> bi-consumer is <b>false</b>.
     *
     * @param iterable NonNull iterable collection of elements of type <b>T</b>.
     * @param action   NonNull consumer, which accepts iterable element of type <b>T</b> and flag of type {@link AtomicBoolean}, with
     *                 initial value <b>false</b>. Iterating will continue unless the value of this flag will be set to
     *                 <b>true</b>, which will be signal to break iterating and thus next element will not be iterated.
     * @param <T>      type of iterable elements.
     */
    public static <T> void forEach(Iterable<T> iterable, BiConsumer<T, AtomicBoolean> action) {
        MS_ListActionWorker.forEach(iterable, action);
    }

    public static <T> List<T> arrayToList(T[] arr) {
        return new ArrayList<>(Arrays.asList(arr));
    }

    /**
     * Tries to execute some action for maximum of <b>maxTimesToRun</b> times.
     *
     * @param maxTimesToRun maximum attempt count to make execution done without errors (exceptions).
     *                      If execution failed within this number of re-runs, an {@link MS_ExecutionFailureException}
     *                      is thrown.
     * @param action        action to execute.
     * @throws MS_ExecutionFailureException if execution failed within given amount of <b>maxTimesToRun</b>
     *                                   or, if any exception occurred while performing execution of action.
     *                                   In that case cause exception is added to this exception as well.
     * @throws MS_BadSetupException if action is not provided or <b>maxTimesToRun</b> is negative or 0.
     */
    public static void executeWithRetry(int maxTimesToRun, IFuncAction action) throws MS_ExecutionFailureException {
        executeWithRetry(maxTimesToRun, action, null);
    }

    /**
     * Tries to execute some action for maximum of <b>maxTimesToRun</b> times.
     *
     * @param maxTimesToRun        maximum attempt count to make execution done without errors (exceptions).
     *                             If execution failed within this number of re-runs, an {@link MS_ExecutionFailureException}
     *                             is thrown.
     * @param action               action to execute.
     * @param actionBetweenRetries action to be performed between retries in case retry is needed.
     * @throws MS_ExecutionFailureException if execution failed within given amount of <b>maxTimesToRun</b>
     *                                   or, if any exception occurred while performing execution of action.
     *                                   In that case cause exception is added to this exception as well.
     * @throws MS_BadSetupException if action is not provided or <b>maxTimesToRun</b> is negative or 0.
     */
    public static void executeWithRetry(int maxTimesToRun, IFuncAction action, IFuncAction actionBetweenRetries) throws MS_ExecutionFailureException {
        if (action == null)
            throw new MS_BadSetupException("Action to execute must be provided");

        executeWithRetry(maxTimesToRun, maxTimesToRun, action, actionBetweenRetries);
    }

    /**
     * Iterates through map elements by Map's natural order and returns key of item at given <b>index</b>.
     * It's recommended to use this method for types of maps, where you can determine their element order, for
     * example, {@link java.util.LinkedHashMap} or {@link java.util.TreeMap}.
     *
     * @param map   given map. <b>Should not be null!</b>
     * @param index index (starting from 0) of Map's element, whose key we are trying to find.
     * @param <T>   type of Map's keys.
     * @return key of Map's element at index <b>index</b>.
     * @throws NullPointerException      in case given <b>map</b> is null.
     * @throws MS_BadSetupException in case index is out of Map's element index bounds.
     */
    public static <T> T getMapElementKey(Map<T, ?> map, int index) {
        T res = null;
        Iterator<T> iter = map.keySet().iterator();
        if (index < 0 || index > map.size() - 1) {
            throw new MS_BadSetupException(String.format("Index [%d] out of bounds. Actual size of map: [%s]", index, map.size()));
        }
        for (int i = 0; i <= index; i++) {
            res = iter.next();
        }
        return res;
    }

    /**
     * @param map   given map. <b>Should not be null!</b>
     * @param index index (starting from 0) of Map's element, whose key we are trying to find.
     * @param <T>   type of Map's keys.
     * @param <U>   type of Map's values.
     * @return map's element at index <b>index</b>.
     * @throws MS_BadSetupException in case index is out of Map's element index bounds.
     */
    public static <T, U> U getMapElementValue(Map<T, U> map, int index) {
        return map.get(getMapElementKey(map, index));
    }

    /**
     * Tests if some collection is empty or null.
     *
     * @param collection any collection of elements.
     * @param <T>        type of elements in collection.
     * @return true if collection is null or has no elements; false, if there is at least 1 element in collection.
     */
    public static <T> boolean isEmpty(Collection<T> collection) {
        return collection == null || collection.size() == 0;
    }

    //*** Private (static) methods ***

    private static void executeWithRetry(int initialTimesToRun, int timesToRunLeft, IFuncAction action, IFuncAction actionBetweenRetries) throws MS_ExecutionFailureException {
        if (timesToRunLeft > 1) {
            try {
                action.execute();
                //if something like this happens, it means that developer's code is not ready for execution repeatable,
                //cause either class setup is incorrect, either there is mistaken attempt to work with null objects
            } catch (NullPointerException | MS_BadSetupException e) {
                throw e;
            } catch (Exception e) {
                try {
                    if (actionBetweenRetries != null)
                        actionBetweenRetries.execute();
                } catch (Exception exIfActionBetweenRetriesFailed) {
                    throw new MS_ExecutionFailureException("Operation failed to execute when performing action to execute between retries." +
                            " At " + (initialTimesToRun - timesToRunLeft + 1) + " of " + initialTimesToRun + " running attempts.", exIfActionBetweenRetriesFailed);
                }
                executeWithRetry(initialTimesToRun, timesToRunLeft - 1, action, actionBetweenRetries);
            }
        } else if (timesToRunLeft == 1) {
            try {
                action.execute();
            } catch (Exception e) {
                throw new MS_ExecutionFailureException("Operation failed to execute within " + initialTimesToRun + " running attempts", e);
            }
        } else if (timesToRunLeft == 0) {
            throw new MS_BadSetupException("Nothing to do here. Please, check method's executeWithRetry documentation!");
        } else {
            throw new MS_BadSetupException("Negative amount of times to run is not acceptable. Please, check method's executeWithRetry documentation!");
        }
    }
}