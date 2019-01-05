package lv.emes.libraries.gui;

/**
 * This functional interface is for GUI event handling purposes. 
 * @author eMeS
 */
@FunctionalInterface
public interface IFuncOnGUIScreenEvent {
	void doOnEvent(MS_GUIScreen sender);
}