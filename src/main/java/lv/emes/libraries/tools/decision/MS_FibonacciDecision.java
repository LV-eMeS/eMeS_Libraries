package lv.emes.libraries.tools.decision;

import lv.emes.libraries.tools.Lazy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Decision algorithm that starts evaluation from 1 and decides affirmatively if current state hits a Fibonacci number.
 * <p><u>Warning</u>: Maximum count of Fibonacci numbers that are supported by this class are 91, where last number
 * is <b>7540113804746346429L</b>. If this value is reached an overflow soon will happen, as numbers are stored in
 * {@link Long} data type.
 *
 * @author eMeS
 * @version 1.0.
 * @since 2.3.3.
 */
public class MS_FibonacciDecision extends MS_AbstractConditionalDecision<Long> {

    private Supplier<List<Long>> fibonacciNumbers = Lazy.lazily(() -> fibonacciNumbers = Lazy.value(computeFibonacciNumbers()));

    private List<Long> computeFibonacciNumbers() {
        List<Long> numbers = new ArrayList<>(91);
        numbers.add(1L);
        numbers.add(2L);
        for (int i = 4; i < 93; i++) {
            numbers.add(fibonacci(i));
        }
        return numbers;
    }

    @Override
    protected Long initiateState() {
        return 1L;
    }

    @Override
    protected Long evaluateNextValue(Long current) {
        return ++current;
    }

    @Override
    public boolean getCurrentDecision() {
        return fibonacciNumbers.get().contains(getCurrentState());
    }

    private static long fibonacci(int n) {
        long n1 = 1;
        long n2 = 1;
        long current = 2;
        for (int i = 3; i <= n; i++) {
            current = n1 + n2;
            n2 = n1;
            n1 = current;
        }
        return current;
    }
}
