package lv.emes.libraries.gui;

/** 
 * There is infinite count of different screen types that GUI can show. This class represent common attributes for them all platform independently.
 * <p>Public methods:
 * <ul>
 * <li>show</li>
 * <li>close</li>
 * <li>showPreviousScreen</li>
 * <li>showNextScreen</li>
 * <li>previousScreen</li>
 * <li>nextScreen</li>
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
 * <li>setPreviousScreen</li>
 * <li>setNextScreen</li>
 * <li>getPreviousScreen</li>
 * <li>getNextScreen</li>
 * </ul>
 * @version 1.2.
 * @author eMeS
 */
public abstract class MS_CustomGUIScreen {

	//PUBLIC STRUCTURES, EXCEPTIONS, PROPERTIES AND CONSTANTS	
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

	//PRIVATE VARIABLES
	private MS_CustomGUIScreen previousScreen;
	private MS_CustomGUIScreen nextScreen;
	private int type;

	//PUBLIC VARIABLES
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

	//CONSTRUCTORS
	/**
	 * Creates screen with defined type of screen. 
	 * Type must be specified, to determine, which type screen is running when using this interface from many inherited classes.
	 * @param type custom type number or one of this class statically defined types. If 0 then <b>MS_CustomGUIScreen.UNKNOWN_SCREEN</b>.
	 */
	public MS_CustomGUIScreen(int type) {
		this.type = type;
	}
	
	//STATIC CONSTRUCTORS

	//PRIVATE METHODS

	//PROTECTED METHODS

	//PUBLIC METHODS
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

    /**
	 * Hides current screen and shows defined previous screen. If previous screen is null then nothing happens.
	 */
	public final void showPreviousScreen() {
		if (previousScreen != null) {
			this.close();
			this.previousScreen.show();
		}
	}
	/**
	 * Hides current screen and shows defined next screen. If next screen is null then nothing happens.
	 */
	public final void showNextScreen() {
		if (nextScreen != null) {
			this.close();
			this.nextScreen.show();
		}
	}
	/**
	 * A synonym for <b>showPreviousScreen</b>.
	 * @see #showPreviousScreen
	 */
	public final void previousScreen() {
		this.showPreviousScreen();
	}
	/**
	 * A synonym for <b>showNextScreen</b>.
	 * @see #showNextScreen
	 */
	public final void nextScreen() {
		this.showNextScreen();
	}
	
	//getters and setters
	public void setPreviousScreen(MS_CustomGUIScreen screen) {
		previousScreen = screen;
	}
	public void setNextScreen(MS_CustomGUIScreen screen) {
		nextScreen = screen;
	}

	public MS_CustomGUIScreen getPreviousScreen() {
		return previousScreen;
	}

	public MS_CustomGUIScreen getNextScreen() {
		return nextScreen;
	}

	public int getType() {
		return type;
	}

	//STATIC METHODS
}
