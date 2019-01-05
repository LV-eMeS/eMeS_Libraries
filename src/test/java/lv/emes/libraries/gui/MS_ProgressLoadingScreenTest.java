package lv.emes.libraries.gui;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

public class MS_ProgressLoadingScreenTest {

    private TestProgressLoadingScreen loadingScreen = new TestProgressLoadingScreen();

    @Test
    public void testDefaults() {
        assertThat(loadingScreen.getType()).isEqualTo(MS_GUIScreen._WAITING_SCREEN);
        assertThat(loadingScreen.getProgress()).isEqualTo(0);
        assertThat(loadingScreen.getWorkCount()).isEqualTo(100);
    }

    @Test
    public void testSetProgress() {
        loadingScreen.setProgress(-1.5);
        assertThat(loadingScreen.getProgress()).isEqualTo(0);
        loadingScreen.setProgress(201.5);
        assertThat(loadingScreen.getProgress()).isEqualTo(1);

        loadingScreen.setProgress(-45);
        assertThat(loadingScreen.getProgress()).isEqualTo(0);
        loadingScreen.setProgress(256);
        assertThat(loadingScreen.getProgress()).isEqualTo(1);

        loadingScreen.setProgress(58);
        assertThat(loadingScreen.getProgress()).isEqualTo(0.58);
        loadingScreen.setProgress(0.3333);
        assertThat(loadingScreen.getProgress()).isEqualTo(0.3333);
    }

    @Test
    public void testUpdateProgress() {
        loadingScreen.updateProgress(14);
        assertThat(loadingScreen.getProgress()).isEqualTo(0.14);

        loadingScreen.updateProgress(0.36);
        assertThat(loadingScreen.getProgress()).isEqualTo(0.5);

        loadingScreen.setWorkCount(200); //change work count to check that scaling works
        // Now +20 will give us only 10% increase
        loadingScreen.updateProgress(20);
        assertThat(loadingScreen.getProgress()).isEqualTo(0.6);

        //Test that by updating we cannot exceed 100%
        loadingScreen.updateProgress(0.8);
        assertThat(loadingScreen.getProgress()).isEqualTo(1);
    }

    @Test
    public void testRefreshProgressIsNotVisible() {
        loadingScreen.setWorkCount(2);
        loadingScreen.setProgress(.5);
        assertThat(loadingScreen.getProgress()).isEqualTo(.5);
        assertThat(loadingScreen.getWorkCount()).isEqualTo(2);
        //refreshProgress method is not triggered, therefore nothing is captured and we are receiving default values
        assertThat(loadingScreen.captureWorkDone.get()).isEqualTo(0);
        assertThat(loadingScreen.captureTotalWork.get()).isEqualTo(0);
        assertThat(loadingScreen.captureProgressPercentage.get()).isEqualTo(0);
    }

    @Test
    public void testRefreshProgressIsVisible() {
        loadingScreen.show();
        loadingScreen.setProgress(.5);
        assertThat(loadingScreen.captureWorkDone.get()).isEqualTo(50);
        assertThat(loadingScreen.captureTotalWork.get()).isEqualTo(100);
        assertThat(loadingScreen.captureProgressPercentage.get()).isEqualTo(.5);

        loadingScreen.updateProgress(.25);
        assertThat(loadingScreen.captureWorkDone.get()).isEqualTo(75);
        assertThat(loadingScreen.captureTotalWork.get()).isEqualTo(100);
        assertThat(loadingScreen.captureProgressPercentage.get()).isEqualTo(.75);

        loadingScreen.setWorkCount(17);
        assertThat(loadingScreen.captureWorkDone.get()).isEqualTo(12);
        assertThat(loadingScreen.captureTotalWork.get()).isEqualTo(17);
        assertThat(loadingScreen.captureProgressPercentage.get()).isCloseTo(0.7058823529411765, offset(.001)); //12 / 17

        loadingScreen.updateProgress(1);
        assertThat(loadingScreen.captureWorkDone.get()).isEqualTo(13);
        assertThat(loadingScreen.captureTotalWork.get()).isEqualTo(17);
        assertThat(loadingScreen.captureProgressPercentage.get()).isCloseTo(.7647, offset(.0001)); //13 / 17
    }


    private static class TestProgressLoadingScreen extends MS_ProgressLoadingScreen {
        public AtomicLong captureWorkDone = new AtomicLong(0);
        public AtomicLong captureTotalWork = new AtomicLong(0);
        public AtomicReference<Double> captureProgressPercentage = new AtomicReference<>(0d);

        @Override
        public void refreshProgress(long workDone, long totalWork, double currentProgress) {
            captureWorkDone.set(workDone);
            captureTotalWork.set(totalWork);
            captureProgressPercentage.set(currentProgress);
        }

        @Override
        protected void doShow() {

        }

        @Override
        protected void doClose() {

        }
    }
}