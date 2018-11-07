package lv.emes.libraries.communication.http;

import lv.emes.libraries.tools.IFuncHandledSupplier;
import lv.emes.libraries.tools.MS_BadSetupException;
import lv.emes.libraries.utilities.MS_CodingUtils;
import lv.emes.libraries.utilities.MS_ExecutionFailureException;
import lv.emes.libraries.utilities.MS_StringUtils;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Polling mechanism to get result from asynchronous operations.
 *
 * @param <T> type of resulting object of the poll.
 * @author eMeS
 * @since 1.0
 * @since 2.1.10
 */
public class MS_Polling<T> {

    public static final Consumer<MS_Polling> NO_POLLING_ACTION = (p) -> {
    };

    private int maxPollingAttempts, currentAttempt, attemptsLeft;
    private long sleepInterval, timeSlept;
    private IFuncHandledSupplier<T> action;
    private Predicate<T> check;
    private Consumer<MS_Polling> actionBeforeEachRetry = NO_POLLING_ACTION;
    private Consumer<MS_Polling> actionBetweenRetries = NO_POLLING_ACTION;
    private String statusMessage = "Pooling status: PENDING";
    private T result;

    public T poll() throws MS_ExecutionFailureException {
        // Validation
        Objects.requireNonNull(action, "Polling action is mandatory for performing poll, therefore it cannot be null");
        Objects.requireNonNull(check, "Polling result checking action is mandatory for performing poll, therefore it cannot be null");
        if (maxPollingAttempts == 0)
            throw new MS_BadSetupException("Nothing to poll here. Please, set maxPollingAttempts > 0");
        if (!MS_CodingUtils.inRange(maxPollingAttempts, 1, 100))
            throw new MS_BadSetupException("Nothing to poll here. Maximum amount of polling attempts must be [1..100]");

        attemptsLeft = maxPollingAttempts;
        boolean isPollStatusPending;

        do { // Polling
            currentAttempt++;
            actionBeforeEachRetry.accept(this);
            this.result = null;
            try {
                this.result = action.get();
            } catch (Exception e) {
                adaptException(e);
            }

            attemptsLeft--;
            isPollStatusPending = !check.test(this.result);
            if (isPollStatusPending) {
                actionBetweenRetries.accept(this);
                try {
                    Thread.sleep(sleepInterval);
                } catch (InterruptedException e) {
                    adaptException(e);
                }
                timeSlept += sleepInterval;
            }
        } while (attemptsLeft > 0 && isPollStatusPending);

        if (attemptsLeft == 0 && isPollStatusPending) { //Expiration check
            throw new MS_ExecutionFailureException(this.getStatusMessage() + ". Poll failed due to expiration in " +
                    this.getCurrentAttemptNumber() + " attempts ("
                    + MS_StringUtils.convertMillisToSecsString(this.getMaximumPollingTime()) +
                    " seconds).");
        }

        return this.result;
    }

    //*** Setters (builder syntax) ***

    public MS_Polling<T> withMaxPollingAttempts(int maxPollingAttempts) {
        if (!MS_CodingUtils.inRange(maxPollingAttempts, 1, 100))
            throw new MS_BadSetupException("Maximum amount of polling attempts must be [1..100]. Please, try something more realistic!");
        this.maxPollingAttempts = maxPollingAttempts;
        this.attemptsLeft = maxPollingAttempts;
        return this;
    }

    public MS_Polling<T> withSleepInterval(long sleepInterval) {
        this.sleepInterval = sleepInterval;
        return this;
    }

    public MS_Polling<T> withSleepInterval(Duration sleepInterval) {
        this.sleepInterval = sleepInterval.toMillis();
        return this;
    }

    public MS_Polling<T> withSleepInterval(long amount, TemporalUnit timeUnit) {
        return this.withSleepInterval(Duration.of(amount, timeUnit));
    }

    public MS_Polling<T> withAction(IFuncHandledSupplier<T> action) {
        Objects.requireNonNull(action, "Polling action is mandatory for performing poll, therefore it cannot be null");
        this.action = action;
        return this;
    }

    public MS_Polling<T> withCheck(Predicate<T> checker) {
        Objects.requireNonNull(checker, "Polling result checking action is mandatory for performing poll, therefore it cannot be null");
        this.check = checker;
        return this;
    }

    public MS_Polling<T> withActionBeforeEachRetry(Consumer<MS_Polling> actionBeforeEachRetry) {
        if (actionBeforeEachRetry == null) this.actionBeforeEachRetry = NO_POLLING_ACTION;
        else this.actionBeforeEachRetry = actionBeforeEachRetry;
        return this;
    }

    public MS_Polling<T> withActionBetweenRetries(Consumer<MS_Polling> actionBetweenRetries) {
        if (actionBetweenRetries == null) this.actionBetweenRetries = NO_POLLING_ACTION;
        else this.actionBetweenRetries = actionBetweenRetries;
        return this;
    }

    public MS_Polling<T> withStatusMessage(String statusMessage) {
        if (statusMessage == null) statusMessage = "";
        this.statusMessage = statusMessage;
        return this;
    }

    //*** Getters ***

    public int getMaxPollingAttempts() {
        return maxPollingAttempts;
    }

    public int getAttemptsLeft() {
        return attemptsLeft;
    }

    public long getSleepInterval() {
        return sleepInterval;
    }

    public long getTimeSlept() {
        return timeSlept;
    }

    public T getResult() {
        return result;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public long getMaximumPollingTime() {
        return maxPollingAttempts * sleepInterval;
    }

    public int getCurrentAttemptNumber() {
        return currentAttempt;
    }

    //*** Private methods ***

    private void adaptException(Exception incoming) throws MS_ExecutionFailureException {
        throw new MS_ExecutionFailureException("Poll failed when performing action to execute." +
                " At " + (maxPollingAttempts - attemptsLeft + 1) + " of " + maxPollingAttempts + " running attempts.", incoming);
    }
}
