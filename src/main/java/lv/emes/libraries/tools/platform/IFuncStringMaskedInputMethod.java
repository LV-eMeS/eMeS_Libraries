package lv.emes.libraries.tools.platform;

import static lv.emes.libraries.tools.MS_Tools.readlnStringFromConsole;

@FunctionalInterface
public interface IFuncStringMaskedInputMethod {
    String readString();

    //some default input methods
    IFuncStringMaskedInputMethod CONSOLE = () -> readlnStringFromConsole();
    //TODO input method for JavaFX with input mask *****
    //TODO input method for Android with input mask *****
}