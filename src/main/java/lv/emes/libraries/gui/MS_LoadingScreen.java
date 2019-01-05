package lv.emes.libraries.gui;

/**
 * Waiting screen that should be used when there are some process that takes unknown amount of time/effort to finish.
 * <p>Public methods:
 * <ul>
 * <li>show</li>
 * <li>close</li>
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
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 * @since 2.2.1
 */
public abstract class MS_LoadingScreen extends MS_GUIScreen {

    public MS_LoadingScreen() {
        super(_WAITING_SCREEN);
    }
}
