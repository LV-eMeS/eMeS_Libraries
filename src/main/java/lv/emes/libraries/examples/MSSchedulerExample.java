package lv.emes.libraries.examples;

import lv.emes.libraries.file_system.MS_FileSystemTools;
import lv.emes.libraries.file_system.MS_Logger;
import lv.emes.libraries.tools.threading.MS_Scheduler;
import lv.emes.libraries.utilities.MS_DateTimeUtils;

/**
 * Demonstrates, how to schedule action, which will execute after 1 hour.
 *
 * @author eMeS
 * @version 1.0.
 */
public class MSSchedulerExample {

    private static final String FILENAME = "D:\\tmp\\MSSchedulerExample.txt";
    private static MS_Logger LOG = new MS_Logger(FILENAME);

    public static void main(String[] args) {
        LOG.info("Scheduler started.");
        MS_Scheduler scheduler = new MS_Scheduler() //FIXME running event which will occur in 20 hours it was 15 seconds early
                .withTriggerTime(MS_DateTimeUtils.getCurrentDateTimeNow().plusSeconds(1))
                .withTriggerTime(MS_DateTimeUtils.getCurrentDateTimeNow().plusMinutes(1))
                .withTriggerTime(MS_DateTimeUtils.getCurrentDateTimeNow().plusHours(10))
                .withTriggerTime(MS_DateTimeUtils.getCurrentDateTimeNow().plusDays(1))
                .withActionOnException((e, time) -> {
                    LOG.error("Exception while running scheduled event started at: " + time, e);
                })
                .withActionOnInterruptedException((time) -> LOG.error("Exception while running scheduler."));

        scheduler.withAction((time) -> {
                    LOG.info("Event successfully finished. " + scheduler.getScheduledEventCount() + " events left to execute.");
                    MS_FileSystemTools.executeApplication(FILENAME, "");
                })
                .schedule()
                .waitFor();
        LOG.info("Schedule finished work.");
    }
}