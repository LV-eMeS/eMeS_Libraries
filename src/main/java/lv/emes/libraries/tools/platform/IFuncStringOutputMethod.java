package lv.emes.libraries.tools.platform;

import javax.swing.*;

@FunctionalInterface
public interface IFuncStringOutputMethod {
    void writeString(String str);

    //some default output methods
    IFuncStringOutputMethod CONSOLE = System.out::println;
    IFuncStringOutputMethod JOPTION_PANE = (str) -> JOptionPane.showMessageDialog(null, str);
    //TODO output method for JavaFX
    //TODO output method for Android
}