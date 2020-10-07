package lv.emes.libraries.tools.math;

/**
 * Enum describes basic arithmetic operations.
 * <p>Getters and setters:
 * <ul>
 *     <li>getOperatorAsChar</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 * @since 2.4.0.
 */
public enum MS_ArithmeticOperation {

    ADDITION('+'),
    SUBTRACTION('-'),
    MULTIPLICATION('*'),
    DIVISION('/'),
    ;

    private final char operatorAsChar;

    MS_ArithmeticOperation(char operatorAsChar) {
        this.operatorAsChar = operatorAsChar;
    }

    public char getOperatorAsChar() {
        return operatorAsChar;
    }

    public static MS_ArithmeticOperation from(char operation) {
        switch (operation) {
            case '+': return ADDITION;
            case '-': return SUBTRACTION;
            case '*': return MULTIPLICATION;
            case '/': return DIVISION;
            default: throw new UnsupportedOperationException("Operation " + operation + " is not supported");
        }
    }
}
