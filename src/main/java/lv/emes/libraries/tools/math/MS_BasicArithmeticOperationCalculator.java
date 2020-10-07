package lv.emes.libraries.tools.math;

import java.util.Objects;

/**
 * Calculator that accepts 2 numbers and by performing one of {@link MS_ArithmeticOperation} with these numbers
 * can provide result of the operation.
 * <p>Public methods:
 * <ul>
 *     <li>calculate</li>
 * </ul>
 * <p>Methods to implement:
 * <ul>
 *     <li>calculateAddition</li>
 *     <li>calculateSubtraction</li>
 *     <li>calculateMultiplication</li>
 *     <li>calculateDivision</li>
 * </ul>
 *
 * @param <T> type of numbers the class can operate with.
 * @param <R> type of result.
 * @author eMeS
 * @version 1.0.
 * @since 2.4.0.
 */
public abstract class MS_BasicArithmeticOperationCalculator<T, R> {

    public R calculate(T number1, T number2, MS_ArithmeticOperation operation) {
        Objects.requireNonNull(number1);
        Objects.requireNonNull(number2);
        Objects.requireNonNull(operation);
        switch (operation) {
            case ADDITION: return calculateAddition(number1, number2);
            case SUBTRACTION: return calculateSubtraction(number1, number2);
            case MULTIPLICATION: return calculateMultiplication(number1, number2);
            case DIVISION: return calculateDivision(number1, number2);
            default: throw new UnsupportedOperationException("Operation " + operation + " is not supported");
        }
    }

    protected abstract R calculateAddition(T number1, T number2);
    protected abstract R calculateSubtraction(T number1, T number2);
    protected abstract R calculateMultiplication(T number1, T number2);
    protected abstract R calculateDivision(T number1, T number2);
}
