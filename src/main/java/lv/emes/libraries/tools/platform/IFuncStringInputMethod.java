package lv.emes.libraries.tools.platform;

import lv.emes.libraries.tools.MS_Tools;

@FunctionalInterface
public interface IFuncStringInputMethod {
    String readString();

    //some default input methods
    IFuncStringInputMethod CONSOLE = MS_Tools::readlnStringFromConsole;
    //TODO input method for JavaFX
    //TODO input method for Android
}