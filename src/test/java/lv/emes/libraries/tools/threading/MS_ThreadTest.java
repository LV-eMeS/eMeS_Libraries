package lv.emes.libraries.tools.threading;

import lv.emes.libraries.utilities.MS_CodingUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MS_ThreadTest {

    @Test
    public void test01ThreadWorksIndependentlyOfMainThread() {
        MS_ThreadForTest thread = new MS_ThreadForTest()
                .withTimeout(5000).withExecutionTime(200)
                .withThreadName("Test thread")
                .start();

        //check that Thread is running for a while
        MS_CodingUtils.sleep(100);
        assertThat(thread.isStarted()).isTrue();
        assertThat(thread.isInterrupted()).isFalse();
        assertThat(thread.isWorkCompleted()).isFalse();

        thread.waitFor();
        assertThat(thread.isStarted()).isFalse();
        assertThat(thread.isInterrupted()).isFalse();
        assertThat(thread.isWorkCompleted()).isTrue();
    }

    @Test
    public void test02ThreadIsInterruptedByMainThread() {
        MS_ThreadForTest thread = new MS_ThreadForTest()
                .withTimeout(0).withExecutionTime(200)
                .withThreadName("Thread to interrupt")
                .start();

        thread.stop(); //interrupt thread
        assertThat(thread.isStarted()).isFalse();
        assertThat(thread.isInterrupted()).isTrue();
        assertThat(thread.isWorkCompleted()).isFalse();
    }

    @Test
    public void test03ThreadIsInterruptedByTimeout() {
        MS_ThreadForTest thread = new MS_ThreadForTest()
                .withTimeout(50).withExecutionTime(200)
                .withThreadName("Thread that will be interrupted by timeout")
                .start();

        thread.waitFor();
        assertThat(thread.getTimeout()).isEqualTo(50);
        assertThat(thread.isInterrupted()).isTrue();
        assertThat(thread.isStarted()).isFalse();
        assertThat(thread.isWorkCompleted()).isFalse();
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

    @Test
    public void test07ThreadIsInterruptedByMainThread() {
        MS_ThreadForTest thread = new MS_ThreadForTest()
                .withExecutionTime(2000)
                .withThreadName("Thread that will be interrupted by main thread")
                .start();

        MS_CodingUtils.sleep(20);
        thread.stop();
        assertThat(thread.isInterrupted()).isTrue();
        assertThat(thread.isStarted()).isFalse();
        assertThat(thread.isWorkCompleted()).isFalse();
    }

    @Test(expected = ClassCastException.class)
    public void test08ExceptionInDoOnExecutionMethod() throws Exception {
        ClassCastException falseException = new ClassCastException();
        AtomicReference<Exception> exceptionThrown = new AtomicReference<>();
        new MS_Thread() {
            @Override
            public Object getThis() {
                return this;
            }

            @Override
            protected void doOnExecution() {
                throw falseException;
            }
        }
        .withActionOnRuntimeException(exceptionThrown::set)
        .start();
        MS_CodingUtils.sleep(11);
        throw exceptionThrown.get();
    }

    @Test
    public void test09ManyThreadsInterruptedByName() {
        String threadName = "Name";
        AtomicInteger threadCount = new AtomicInteger(0);
        MS_ThreadForTest t1 = new MS_ThreadForTest().withThreadName(threadName).withExecutionTime(5000).start();
        MS_ThreadForTest t2 = new MS_ThreadForTest().withThreadName(threadName).withExecutionTime(5000).start();
        MS_ThreadForTest t3 = new MS_ThreadForTest().withThreadName(threadName).withExecutionTime(5000).start();
        Thread.getAllStackTraces().forEach((thread, traceEl) -> {
            if (threadName.equals(thread.getName())) {
                threadCount.incrementAndGet();
                thread.interrupt();
            }
        });
        assertThat(t1.getThreadName()).isEqualTo(threadName);
        assertThat(t2.getThreadName()).isEqualTo(threadName);
        assertThat(t3.getThreadName()).isEqualTo(threadName);
        assertThat(threadCount.get()).isEqualTo(3);

        MS_CodingUtils.sleep(11);
        assertThat(t1.isInterrupted()).isTrue();
        assertThat(t2.isInterrupted()).isTrue();
        assertThat(t3.isInterrupted()).isTrue();
    }

    @Test
    public void test10ThreadInterruptedBeforeJoining() {
        MS_ThreadForTest thread = new MS_ThreadForTest()
                .withExecutionTime(200)
                .withThreadName("Thread that will be interrupted by another thread")
                .start();

        //now to create thread that will terminate this thread
        new MS_Thread() {
            @Override
            public Object getThis() {
                return this;
            }
            @Override
            protected void doOnExecution() throws RuntimeException {
                thread.stop();
            }
        }.start();

        thread.waitFor();
        assertThat(thread.isInterrupted()).isTrue();
        assertThat(thread.isStarted()).isFalse();
        assertThat(thread.isWorkCompleted()).isFalse();
    }

    @Test
    public void test11ThreadExecutedTwice() {
        MS_ThreadForTest thread = new MS_ThreadForTest()
                .withExecutionTime(22)
                .withThreadName("Thread to run twice")
                .start();

        thread.waitFor();
        assertThat(thread.isStarted()).isFalse();
        assertThat(thread.isWorkCompleted()).isTrue();

        //second time
        thread.start();
        assertThat(thread.isStarted()).isTrue();
        assertThat(thread.isWorkCompleted()).isFalse();

        thread.waitFor();
        assertThat(thread.isStarted()).isFalse();
        assertThat(thread.isWorkCompleted()).isTrue();
    }

    private static class MS_ThreadForTest extends MS_Thread<MS_ThreadForTest> {

        private long executionTime = 1000L;

        @Override
        protected void doOnExecution() throws InterruptedException {
            Thread.sleep(executionTime);
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
