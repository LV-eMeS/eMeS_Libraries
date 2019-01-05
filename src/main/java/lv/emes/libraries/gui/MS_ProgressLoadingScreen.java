package lv.emes.libraries.gui;

/**
 * Waiting screen that should be used when there are some process that takes already known amount of time/effort to finish.
 * <p>Public methods:
 * <ul>
 * <li>show</li>
 * <li>close</li>
 * <li>refreshProgress (<i>abstract method</i>)</li>
 * </ul>
 * <p>Protected methods:
 * <ul>
 * <li>doShow</li>
 * <li>doClose</li>
 * </ul>
 * <p>Properties:
 * <ul>
 * <li>onShow</li>
 * <li>onClose</li>
 * </ul>
 * <p>Setters and getters:
 * <ul>
 * <li>getType</li>
 * <li>isVisible</li>
 * <li>setWorkCount</li>
 * <li>setProgress</li>
 * <li>updateProgress</li>
 * <li>getProgress</li>
 * <li>getWorkCount</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 * @since 2.2.1
 */
public abstract class MS_ProgressLoadingScreen extends MS_LoadingScreen {

    private double progress = 0;
    private long workCount = 100;

    public MS_ProgressLoadingScreen() {
        super();
    }

    //*** Public methods ***

    public abstract void refreshProgress(long workDone, long totalWork, double currentProgress);

    //*** Setters and updaters ***

    public void setWorkCount(long workCount) {
        if (workCount <= 0) this.workCount = 1;
        else this.workCount = workCount;

        long progressPosition = calculateProgressPosition();
        this.progress = (double) progressPosition / workCount;
        if (isVisible()) refreshProgress(progressPosition, workCount, progress);
    }

    public void setProgress(double progress) {
        if (progress < 0) this.progress = 0;
        else if (progress > 1) this.progress = 1;
        else this.progress = progress;
        if (isVisible()) refreshProgress(calculateProgressPosition(), workCount, progress);
    }

    public void setProgress(long progress) {
        this.setProgress((double) progress / workCount);
    }

    public void updateProgress(double increment) {
        this.setProgress(increment + progress);
    }

    public void updateProgress(long increment) {
        this.updateProgress((double) increment / workCount);
    }

    //*** Getters ***

    public double getProgress() {
        return progress;
    }

    public long getWorkCount() {
        return workCount;
    }

    //*** Helper methods ***

    private long calculateProgressPosition() {
        return (long) Math.floor(progress * workCount);
    }
}
