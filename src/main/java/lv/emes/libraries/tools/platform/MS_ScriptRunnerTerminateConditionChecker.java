package lv.emes.libraries.tools.platform;

/**
 * Listens for user keystrokes until detects keystroke that terminates script running.
 *
 * @author eMeS
 * @version 1.0.
 */
class MS_ScriptRunnerTerminateConditionChecker implements Runnable {

    private MS_ScriptRunner runner;

    public MS_ScriptRunnerTerminateConditionChecker(MS_ScriptRunner runner) {
        this.runner = runner;
    }

    @Override
    public void run() {
        final int DEFAULT_SLEEP_TIME = 100;
        boolean keyStrokeDetected = false;
        while (!keyStrokeDetected) {
            try {
                Thread.sleep(DEFAULT_SLEEP_TIME);
            } catch (InterruptedException e) {
                return; //nothing more to do if interrupted
            }
            keyStrokeDetected = runner.scriptRunningTerminated();
        }

        //if went out of loop then it's definately terminated by user, so inform runner about it!
        runner.terminateScriptRunning();
    }
}
