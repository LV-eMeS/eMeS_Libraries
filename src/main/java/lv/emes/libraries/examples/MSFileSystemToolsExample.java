package lv.emes.libraries.examples;

import lv.emes.libraries.file_system.MS_FileSystemTools;

public class MSFileSystemToolsExample {

	public static void main(String[] args) {
		System.out.println(MS_FileSystemTools.getFilenameWithoutExtension("text.txt."));
		System.out.println(MS_FileSystemTools.getShortFilename("D:/tmp/test.bat"));
		System.out.println(MS_FileSystemTools.getDirectoryOfFile("D:/tmp/test.bat"));
		System.out.println(MS_FileSystemTools.getDirectoryOfFile("test.bat"));
		System.out.println(MS_FileSystemTools.fileExists("D:/tm"));
		System.out.println();

		System.out.println(MS_FileSystemTools.executeApplication("Notepad.exe", "")); //windows application
//		System.out.println(MS_FileSystemTools.executeApplication("Notepad.exe", "test.bat")); //windows application with params
//		MS_FileSystemTools.executeApplication("build.gradle", ""); //file in project root folder
//		MS_FileSystemTools.executeApplication("src/test/resources/test.bat", ""); //file in project's deeper folder
//		System.out.println(MS_FileSystemTools.executeApplication("src/test/resources/test.bat", "Everything works now!")); //file with arguments in project's deeper folder
//		MS_FileSystemTools.executeApplication("src/main/resources/MS_FileSystemTools.executeApplication capabilities.xls", ""); //file with spaces in filename in project's deeper folder (ALWAYS FAILING)
//		MS_FileSystemTools.executeApplication("http://stackoverflow.com/questions/13991007/execute-external-program-in-java", ""); //web link
//		MS_FileSystemTools.executeApplication("D:/tmp/mape_bez_atstarpem", ""); //folder in file system
//		MS_FileSystemTools.executeApplication("D:/tmp/Mape ar atstarpem/", ""); //folder in file system with spaces (ALWAYS FAILING)
//		MS_FileSystemTools.executeApplication("D:/tmp/garumzīmes", ""); //folder in file system with diacrytic symbols
//		MS_FileSystemTools.executeApplication("D:/tmp/test.bat", ""); //batch file without arguments in file system
//		MS_FileSystemTools.executeApplication("D:/tmp/test.bat", "aaa"); //batch file with arguments in file system
//		MS_FileSystemTools.executeApplication("D:/tmp/eMeS_Timer.exe", ""); //application in file system
//		MS_FileSystemTools.executeApplication("D:/tmp/eMeS_Timer.exe", "11"); //application with arguments
//		MS_FileSystemTools.executeApplication("D:/tmp/eMeS Timer.exe", "11"); //application with arguments and space in path
//		MS_FileSystemTools.executeApplication("D:/tmp/ttt.txt", ""); //text file
//		MS_FileSystemTools.executeApplication("D:/tmp/ttt.unknown", ""); //text file with unknown extension
	}
}