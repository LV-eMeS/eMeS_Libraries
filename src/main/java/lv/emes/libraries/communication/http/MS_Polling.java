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
 * @version 1.2
 * @since 2.1.10
 */
public class MS_Polling<T> {

    public static final int DEFAULT_MAX_POLLING_ATTEMPTS = 60; // 30 seconds running twice per second
    public static final long DEFAULT_SLEEP_INTERVAL = 500;
    public static final int MAX_POLLING_ATTEMPTS = 1000;

    public static final Consumer<MS_Polling> NO_POLLING_ACTION = (p) -> {
    };
    public static final Consumer<MS_Polling> DEFAULT_ACTION_BETWEEN_RETRIES_LOG_STATUS_TO_CONSOLE = (p) -> {
        System.out.println(String.format(
                "%s.\nAttempts made: " + p.getCurrentAttemptNumber() +
                        " | Already slept for " +
                        MS_StringUtils.convertMillisToSecsString(p.getTimeSlept()) + " / (MAX) " +
                        MS_StringUtils.convertMillisToSecsString(p.getMaximumPollingTime()) + " seconds."
                , p.getStatusMessageProducer() == null ? p.getStatusMessage() : p.getStatusMessageProducer().produceMessage(p)));
    };

    private int maxPollingAttempts, currentAttempt, attemptsLeft;
    private long sleepInterval, timeSlept;
    private IFuncHandledSupplier<T> action;
    private Predicate<T> check;
    private Consumer<MS_Polling> actionBeforeEachRetry = NO_POLLING_ACTION;
    private Consumer<MS_Polling> actionBetweenRetries = NO_POLLING_ACTION;
    private String statusMessage = "Pooling status: PENDING";
    private StatusMessageProducer statusMessageProducer = null;
    private T result;

    /**
     * Performs polling of concrete <b>action</b> (performs action repeatedly)
     * until specified conditions (in <b>check</b>) are met.
     * If conditions are not met in given time / attempts an exception is thrown.
     *
     * @return value that represents result of performed action - usually after success it is usable for further operations.
     * @throws MS_ExecutionFailureException <ol>
     *                                      <li>if some exception happens while performing <b>action</b>;</li>
     *                                      <li>if given time / attempts to finish polling exceeds;</li>
     *                                      <li>if polling thread is terminated.</li>
     *                                      </ol>
     */
    public T poll() throws MS_ExecutionFailureException {
        // Validation
        Objects.requireNonNull(action, "Polling action is mandatory for performing poll, therefore it cannot be null");
        Objects.requireNonNull(check, "Polling result checking action is mandatory for performing poll, therefore it cannot be null");
        if (maxPollingAttempts == 0)
            throw new MS_BadSetupException("Nothing to poll here. Please, set maxPollingAttempts > 0");
        if (!MS_CodingUtils.inRange(maxPollingAttempts, 1, MAX_POLLING_ATTEMPTS))
            throw new MS_BadSetupException("Nothing to poll here. Maximum amount of polling attempts must be [1..1000]");

        attemptsLeft = maxPollingAttempts;
        currentAttempt = 0;
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
            throw new MS_ExecutionFailureException(this.getStatusMessageProducer() == null ? this.getStatusMessage() : this.getStatusMessageProducer().produceMessage(this)
                    + ". Poll failed due to expiration in " + this.getCurrentAttemptNumber() + " attempts ("
                    + MS_StringUtils.convertMillisToSecsString(this.getMaximumPollingTime()) + " seconds).");
        }

        return this.result;
    }

    //*** Setters (builder syntax) ***

    public MS_Polling<T> withDefaultAttemptsAndSleepInterval() {
        return this.withMaxPollingAttempts(DEFAULT_MAX_POLLING_ATTEMPTS).withSleepInterval(DEFAULT_SLEEP_INTERVAL);
    }

    /**
     * Sets maximum count of attempts to get polling result.
     *
     * @param maxPollingAttempts [1..100].
     * @return reference to polling itself.
     */
    public MS_Polling<T> withMaxPollingAttempts(int maxPollingAttempts) {
        if (!MS_CodingUtils.inRange(maxPollingAttempts, 1, 1000))
            throw new MS_BadSetupException("Maximum amount of polling attempts must be [1..1000]. Please, set more realistic value!");
        this.maxPollingAttempts = maxPollingAttempts;
        this.attemptsLeft = maxPollingAttempts;
        return this;
    }

    /**
     * Sets sleep interval in milliseconds.
     *
     * @param sleepInterval time between polling retries when polling is idle.
     * @return reference to polling itself.
     */
    public MS_Polling<T> withSleepInterval(long sleepInterval) {
        this.sleepInterval = sleepInterval;
        return this;
    }

    /**
     * Sets sleep interval in given duration.
     *
     * @param sleepInterval time between polling retries when polling is idle.
     * @return reference to polling itself.
     */
    public MS_Polling<T> withSleepInterval(Duration sleepInterval) {
        this.sleepInterval = sleepInterval.toMillis();
        return this;
    }

    /**
     * Sets sleep interval in given amount of time.
     *
     * @param amount   units of time.
     * @param timeUnit time unit measure.
     * @return reference to polling itself.
     */
    public MS_Polling<T> withSleepInterval(long amount, TemporalUnit timeUnit) {
        return this.withSleepInterval(Duration.of(amount, timeUnit));
    }

    /**
     * Sets action of polling.
     *
     * @param action an action that results as some value that in success scenario can be used after polling.
     * @return reference to polling itself.
     */
    public MS_Polling<T> withAction(IFuncHandledSupplier<T> action) {
        Objects.requireNonNull(action, "Polling action is mandatory for performing poll, therefore it cannot be null");
        this.action = action;
        return this;
    }

    /**
     * @param checker action that is performed between retries and determines if poll is successful.
     * @return reference to polling itself.
     */
    public MS_Polling<T> withCheck(Predicate<T> checker) {
        Objects.requireNonNull(checker, "Polling result checking action is mandatory for performing poll, therefore it cannot be null");
        this.check = checker;
        return this;
    }

    /**
     * @param actionBeforeEachRetry action that is executed before each of polling retries.
     * @return reference to polling itself.
     */
    public MS_Polling<T> withActionBeforeEachRetry(Consumer<MS_Polling> actionBeforeEachRetry) {
        if (actionBeforeEachRetry == null) this.actionBeforeEachRetry = NO_POLLING_ACTION;
        else this.actionBeforeEachRetry = actionBeforeEachRetry;
        return this;
    }

    /**
     * @param actionBetweenRetries action that is executed after each of polling retries.
     * @return reference to polling itself.
     */
    public MS_Polling<T> withActionBetweenRetries(Consumer<MS_Polling> actionBetweenRetries) {
        if (actionBetweenRetries == null) this.actionBetweenRetries = NO_POLLING_ACTION;
        else this.actionBetweenRetries = actionBetweenRetries;
        return this;
    }

    /**
     * @param statusMessage message that will be printed in exception message - it represents context of polling itself.
     * @return reference to polling itself.
     */
    public MS_Polling<T> withStatusMessage(String statusMessage) {
        if (statusMessage == null) statusMessage = "";
        this.statusMessage = statusMessage;
        return this;
    }

    public interface StatusMessageProducer {
        String produceMessage(MS_Polling poll);
    }

    public MS_Polling<T> withStatusMessageProducer(StatusMessageProducer producer) {
        this.statusMessageProducer = producer;
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

    public StatusMessageProducer getStatusMessageProducer() {
        return statusMessageProducer;
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
