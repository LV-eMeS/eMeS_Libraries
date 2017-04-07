package lv.emes.libraries.tools.platform;

import lv.emes.libraries.tools.MS_CodingTools;

@FunctionalInterface
public interface IFuncStringInputMethod {
    String readString(String askText);

    //some default input methods
    IFuncStringInputMethod CONSOLE = MS_CodingTools::readStringFromConsole;
    IFuncStringInputMethod JOPTION_PANE = MS_CodingTools::readStringFromJOptionPane;
    //TODO input method for JavaFX
    //TODO input method for Android
    //masked inputs
    IFuncStringInputMethod JOPTION_PANE_MASKED = MS_CodingTools::readStringFromJPasswordField;
    //TODO input method for JavaFX with input mask *****
    //TODO input method for Android with input mask *****
}