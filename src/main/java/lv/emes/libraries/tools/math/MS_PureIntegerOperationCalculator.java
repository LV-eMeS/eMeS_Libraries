package lv.emes.libraries.tools.math;

/**
 * Calculator that works with 2 integers and provides result that is also an integer, no matter, what the operation is.
 * <p>Public methods:
 * <ul>
 *     <li>calculate</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 * @since 2.4.0.
 */
public class MS_PureIntegerOperationCalculator extends MS_BasicArithmeticOperationCalculator<Integer, Integer> {

    @Override
    protected Integer calculateAddition(Integer number1, Integer number2) {
        return number1 + number2;
    }

    @Override
    protected Integer calculateSubtraction(Integer number1, Integer number2) {
        return number1 - number2;
    }

    @Override
    protected Integer calculateMultiplication(Integer number1, Integer number2) {
        return number1 * number2;
    }

    @Override
    protected Integer calculateDivision(Integer number1, Integer number2) {
        return number1 / number2;
    }
}
