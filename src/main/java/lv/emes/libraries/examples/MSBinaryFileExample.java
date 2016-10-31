package lv.emes.libraries.examples;

import lv.emes.libraries.file_system.MS_BinaryTools;

import java.io.FileInputStream;
import java.io.IOException;

public class MSBinaryFileExample {

	public static void main(String[] args) {
		//Copying file
		FileInputStream stream;
		try {
			stream = MS_BinaryTools.readFile("src/test/resources/test_pic.png");
			MS_BinaryTools.writeFile(stream, "src/test/resources/test_pic_example.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
