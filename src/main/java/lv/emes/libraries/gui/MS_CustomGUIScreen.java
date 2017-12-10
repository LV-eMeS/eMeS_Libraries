package lv.emes.libraries.gui;

/** 
 * There is infinite count of different screen types that GUI can show. This class represent common attributes for them all platform independently.
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
 * </ul>
 * @version 1.2.
 * @author eMeS
 */
public abstract class MS_CustomGUIScreen {

	public static final int _UNKNOWN_SCREEN = 0;
	public static final int _HELLO_SCREEN = 1;
	public static final int _GOODBYE_SCREEN = 2;
	public static final int _LOGIN_SCREEN = 3;
	public static final int _WAITING_SCREEN = 4;
	public static final int _ERROR_SCREEN = 5;
	public static final int _WARNING_SCREEN = 6;
	public static final int _INFORMATION_SCREEN = 7;
	public static final int _ACTION_SCREEN = 8;

	public static final int _STANDARD_SCREEN = 101;
	public static final int _MESSAGE_SCREEN = 102;
	public static final int _DIALOG = 103;

	private int type;

	/**
	 * Define behavior when screen shows up!<br>
	 * = (screen) -&gt; {actions to do after <b>show()</b>};
	 */
	public IFuncOnGUIScreenEvent onShow;
	/**
	 * Define behavior when screen is closed for good. You can free variables here, etc.<br>
	 * = (screen) -&gt; {actions to do before <b>close()</b>};
	 */
	public IFuncOnGUIScreenEvent onClose;

	/**
	 * Creates screen with defined type of screen. 
	 * Type must be specified, to determine, which type screen is running when using this interface from many inherited classes.
	 * @param type custom type number or one of this class statically defined types. If 0 then <b>MS_CustomGUIScreen.UNKNOWN_SCREEN</b>.
	 */
	public MS_CustomGUIScreen(int type) {
		this.type = type;
	}

	/**
	 * Does all the actions to fulfill screen showing and keeping on top till it's closed.
	 */
	protected abstract void doShow();

    /**
	 * Does all the necessary actions in order to close the screen and finish its work.
	 */
	protected abstract void doClose();

    /**
     * Shows the screen.
     * <br>When screen is shown <b>onShow</b> method is called.
     */
    public final void show() {
		this.doShow();
		if (onShow != null)
			onShow.doOnEvent(this);
	}

    /**
     * Closes the screen. For some GUI screens this method isn't applicable, so it's doing nothing or throws some
     * exception.
     * <br>Before closing <b>onClose</b> method is called.
     */
    public final void close() {
		if (onClose != null)
			onClose.doOnEvent(this);
		this.doClose();
	}
	
	public int getType() {
		return type;
	}
}
