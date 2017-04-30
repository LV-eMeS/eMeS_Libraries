package lv.emes.libraries.gui;

/** 
 * There is infinite count of different screen types that GUI can show. This class represent common attributes for them all platform independently.
 * <p>Public methods:
 * -show
 * -hide
 * -close
 * -showPreviousScreen
 * -showNextScreen
 * -previousScreen
 * -nextScreen
 * <p>Protected methods:
 * -initialize
 * -finalize
 * <p>Properties:
 * -onShow
 * -onHide
 * -onClose
 * <p>Setters and getters:
 * -setPreviousScreen
 * -setNextScreen
 * @version 1.1.
 * @author eMeS
 */
public abstract class MSCustomGUIScreen {
	//PUBLIC STRUCTURES, EXCEPTIONS, PROPERTIES AND CONSTANTS	
	public static final int _UNKNOWN_SCREEN = 0;
	public static final int _HELLO_SCREEN = 1;
	public static final int _GOODBYE_SCREEN = 2;
	public static final int _LOGIN_SCREEN = 3;
	public static final int _WAITING_SCREEN = 4;
	public static final int _ERROR_SCREEN = 5;
	public static final int _WARNING_SCREEN = 6;
	public static final int _INFORMATION_SCREEN = 7;
	
	public static final int _STANDARD_SCREEN = 101;
	public static final int _MESSAGE_SCREEN = 102;
	public static final int _DIALOG = 103;

	//PRIVATE VARIABLES
	private MSCustomGUIScreen previousScreen;
	private MSCustomGUIScreen nextScreen;
	private int type;

	//PUBLIC VARIABLES
	/**
	 * Define behavior when screen shows up!<br>
	 * = (screen) -&gt; {methods after <b>initialize()</b>, but before <b>show()</b>};
	 */
	public IFuncOnGUIScreenEvent onShow;
	/**
	 * Define behavior when screen is hidden, e.g., controls shift to another screen, but this is running background.<br>
	 * = (screen) -&gt; {methods after <b>hide()</b>};
	 */
	public IFuncOnGUIScreenEvent onHide;
	/**
	 * Define behavior when screen is closed for good. You can free variables here, etc.<br>
	 * = (screen) -&gt; {methods after <b>close()</b>, but before <b>finalize()</b>};
	 */
	public IFuncOnGUIScreenEvent onClose;

	//CONSTRUCTORS
	/**
	 * Creates screen with defined type of screen. 
	 * Type must be specified, to determine, which type screen is running when using this interface from many inherited classes.
	 * @param type custom type number or one of this class statically defined types. If 0 then <b>MSCustomGUIScreen.UNKNOWN_SCREEN</b>.
	 */
	public MSCustomGUIScreen(int type) {
		this.type = type;
	}
	
	//STATIC CONSTRUCTORS

	//PRIVATE METHODS

	//PROTECTED METHODS

	//PUBLIC METHODS
	/**
	 * Can be overridden to define actions before showing this kind of screen.
	 */
	protected void initialize() {}

    /**
	 * Can be overridden to define actions when this kind of screen is finishing its work (closing).
	 */
	protected void finalize() {}

    public void show() {
		this.initialize();
		if (onShow != null)
			onShow.doOnEvent(this);
	}
	public void hide() {
		if (onHide != null)
			onHide.doOnEvent(this);
	}

    public void close() {
		if (onClose != null)
			onClose.doOnEvent(this);
		this.finalize();
	}

    /**
	 * Hides current screen and shows defined previous screen. If previous screen is null then nothing happens.
	 */
	public void showPreviousScreen() {
		if (previousScreen != null) {
			this.hide();
			this.previousScreen.show();
		}
	}
	/**
	 * Hides current screen and shows defined next screen. If next screen is null then nothing happens.
	 */
	public void showNextScreen() {
		if (nextScreen != null) {
			this.hide();
			this.nextScreen.show();
		}
	}
	/**
	 * A synonym for <b>showPreviousScreen</b>.
	 * @see #showPreviousScreen
	 */
	public void previousScreen() {
		this.showPreviousScreen();
	}
	/**
	 * A synonym for <b>showNextScreen</b>.
	 * @see #showNextScreen
	 */
	public void nextScreen() {
		this.showNextScreen();
	}
	
	//getters and setters
	public void setPreviousScreen(MSCustomGUIScreen screen) {
		previousScreen = screen;
	}
	public void setNextScreen(MSCustomGUIScreen screen) {
		nextScreen = screen;
	}

	public int getType() {
		return type;
	}

	//STATIC METHODS
}
