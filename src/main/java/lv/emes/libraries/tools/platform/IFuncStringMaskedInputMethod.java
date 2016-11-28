package lv.emes.libraries.tools.platform;

@FunctionalInterface
public interface IFuncStringMaskedInputMethod {
    String readString();

    //some default input methods
//    IFuncStringMaskedInputMethod CONSOLE = MS_Tools::readlnStringFromConsole;
    //TODO input method for JavaFX with input mask *****
    //TODO input method for Android with input mask *****
}