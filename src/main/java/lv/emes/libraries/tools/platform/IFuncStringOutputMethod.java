package lv.emes.libraries.tools.platform;

@FunctionalInterface
public interface IFuncStringOutputMethod {
    void writeString(String str);

    //some default output methods
    IFuncStringOutputMethod CONSOLE = (s) -> {
        System.out.println(s);
    };
    //TODO output method for JavaFX
    //TODO output method for Android
}