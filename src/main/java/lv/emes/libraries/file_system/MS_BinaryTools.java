package lv.emes.libraries.file_system;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;

/** 
 * Module consists of methods for binary file I/O. 
 * It is mainly used to perform DB operations with binary file exchange.
 * @version 1.4.
 */
public class MS_BinaryTools {
	/**
	 * Reads file from file system. 
	 * @param aFileName path to a file.
	 * @return binary stream.
	 * @throws FileNotFoundException when file doesn't exist.
	 */
	public static FileInputStream readFile(String aFileName) throws FileNotFoundException {
		File file = new File(aFileName);
		FileInputStream inputStream = new FileInputStream(file);
		return inputStream;
	}
	
	/**
	 * Copy given <b>in</b> stream into file <b>outputFile</b>.
	 * @param in might be FileInputStream too. The stream that holds read binary file data.
	 * @param outputFile path to file that needs to be created.
	 * @throws IOException if an I/O error occurs. For example, when for some reason cannot convert <b>in</b> to output binary stream.
	 */
	public static void writeFile(InputStream in, String outputFile) throws IOException {
		OutputStream out = new FileOutputStream(outputFile);
		IOUtils.copy(in, out);
		in.close();
		out.close();
	}

	/**
	 * Copies data stream. Exception will be stack - traced.
	 * @param in Stream that will be copied
	 * @param out Stream in which <b>in</b> stream will be copied
	 */
	public static void copyStream(InputStream in, OutputStream out) {
		try {
			IOUtils.copy(in, out);
			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Converts input stream into byte array.
	 * @param input binary input stream.
	 * @return byte array.
	 */
	public static byte[] inputToBytes(InputStream input) {
		try {
			return IOUtils.toByteArray(input);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Converts byte array into stream.
	 * @param bytes byte array.
	 * @return binary stream.
	 */
	public static InputStream bytesToIntput(byte[] bytes) {
		return new ByteArrayInputStream(bytes);
	}
	
	/**
	 * Converts String into byte array.
	 * @param input formatted String type text .
	 * @return byte array.
	 */
	public static byte[] textToBytes(String input) {
		return Base64.decodeBase64(input);
	}
	
	/**
	 * Converts byte array into String.
	 * @param bytes byte array.
	 * @return formatted String type text as binary data.
	 */
	public static String bytesTotext(byte[] bytes) {
		return Base64.encodeBase64String(bytes);
	}
}