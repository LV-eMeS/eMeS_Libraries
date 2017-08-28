package lv.emes.libraries.tools.threading;

import lv.emes.libraries.utilities.MS_CodingUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MSThreadTest {

    @Test
    public void test01ThreadWorksIndependentlyOfMainThread() {
        MS_ThreadForTest thread = new MS_ThreadForTest()
                .withTimeout(5000).withExecutionTime(200)
                .withThreadName("Test thread")
                .start();

        //check that Thread is running for a while
        MS_CodingUtils.sleep(100);
        assertTrue(thread.isStarted());
        assertFalse(thread.isInterrupted());
        assertFalse(thread.isWorkCompleted());

        thread.waitFor();
        assertFalse(thread.isStarted());
        assertFalse(thread.isInterrupted());
        assertTrue(thread.isWorkCompleted());
    }

    @Test
    public void test02ThreadIsInterruptedByMainThread() {
        MS_ThreadForTest thread = new MS_ThreadForTest()
                .withTimeout(0).withExecutionTime(200)
                .withThreadName("Thread to interrupt")
                .start();

        thread.stop(); //interrupt thread
        assertFalse(thread.isStarted());
        assertTrue(thread.isInterrupted());
        assertFalse(thread.isWorkCompleted());
    }

    @Test
    public void test03ThreadIsInterruptedByTimeout() {
        MS_ThreadForTest thread = new MS_ThreadForTest()
                .withTimeout(50).withExecutionTime(200)
                .withThreadName("Thread that will be interrupted by timeout")
                .start();

        thread.waitFor();
        assertTrue(thread.isInterrupted());
        assertFalse(thread.isStarted());
        assertFalse(thread.isWorkCompleted());
    }

    @Test(expected = IllegalStateException.class)
    public void test04ExceptionWhenStartAlreadyStartedThread() {
        new MS_ThreadForTest()
                .withTimeout(0).withExecutionTime(5000)
                .start().start();
    }

    @Test(expected = IllegalStateException.class)
    public void test05ExceptionWhenStopThreadThatIsNotStarted() {
        MS_ThreadForTest thread = new MS_ThreadForTest()
                .withTimeout(0).withExecutionTime(5000);
        thread.stop();
    }

    @Test(expected = IllegalStateException.class)
    public void test06ExceptionWhenWaitForThreadThatIsNotStarted() {
        MS_ThreadForTest thread = new MS_ThreadForTest()
                .withTimeout(0).withExecutionTime(5000);
        thread.waitFor();
    }

    private static class MS_ThreadForTest extends MS_Thread<MS_ThreadForTest> {

        private long executionTime = 1000L;

        @Override
        protected void doOnExecution() {
            try {
                Thread.sleep(executionTime);
            } catch (InterruptedException e) {
                //don't care about interruption here
            }
        }

        @Override
        public MS_ThreadForTest getThis() {
            return this;
        }

        public MS_ThreadForTest withExecutionTime(long executionTime) {
            this.executionTime = executionTime;
            return getThis();
        }
    }
}
