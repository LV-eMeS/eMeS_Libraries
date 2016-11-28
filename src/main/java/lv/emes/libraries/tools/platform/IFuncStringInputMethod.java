package lv.emes.libraries.tools.platform;

import static lv.emes.libraries.tools.MS_Tools.readlnStringFromConsole;

@FunctionalInterface
public interface IFuncStringInputMethod {
    String readString();

    //some default input methods
    IFuncStringInputMethod CONSOLE = () -> readlnStringFromConsole();
    //TODO input method for JavaFX
    //TODO input method for Android
}