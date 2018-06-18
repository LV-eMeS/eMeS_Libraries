package lv.emes.libraries.file_system;

import lv.emes.libraries.patches.jmimemagic.MimeMagic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;
import net.sf.jmimemagic.UnsupportedTypeException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.compress.utils.IOUtils;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.*;

/**
 * Module consists of methods for binary file I/O.
 * It can be used in various cases while doing file / stream data exchange or manipulations with bytes.
 *
 * @version 2.1.
 */
public class MS_BinaryTools {
    /**
     * Reads file from file system as byte array.
     *
     * @param aFileName path to a file.
     * @return byte array or null if failed to read file (most probably due to non-existent file).
     */
    public static byte[] readFileToByteArray(String aFileName) {
        byte[] res;
        try {
            res = org.apache.commons.io.FileUtils.readFileToByteArray(new File(aFileName));
        } catch (IOException e) {
            res = null;
        }
        return res;
    }

    /**
     * Reads file from file system.
     *
     * @param aFileName path to a file.
     * @return binary stream.
     * @throws FileNotFoundException when file doesn't exist.
     */
    public static FileInputStream readFile(String aFileName) throws FileNotFoundException {
        File file = new File(aFileName);
        return new FileInputStream(file);
    }

    /**
     * Copy given <b>in</b> stream into file <b>outputFile</b>.
     *
     * @param in         might be FileInputStream too. The stream that holds read binary file data.
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
     * Copies data stream without need to catch the exception if it occurs.
     * But still exception will be stack - traced.
     *
     * @param in  Stream that will be copied
     * @param out Stream in which <b>in</b> stream will be copied
     */
    public static void copyStreamSilently(InputStream in, OutputStream out) {
        try {
            copyStream(in, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Copies data stream. Exception will be stack - traced.
     *
     * @param in  Stream that will be copied
     * @param out Stream in which <b>in</b> stream will be copied
     * @throws IOException if an I/O error occurs.
     */
    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        IOUtils.copy(in, out);
        in.close();
        out.close();
    }

    /**
     * Converts input stream into byte array.
     *
     * @param input binary input stream.
     * @return byte array.
     */
    public static byte[] inputToBytes(InputStream input) {
        try {
            return IOUtils.toByteArray(input);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Converts input stream into UTF-8 formatted text.
     *
     * @param input binary input stream.
     * @return text if success, empty string if failure.
     */
    public static String inputToUTF8(InputStream input) {
        try {
            return org.apache.commons.io.IOUtils.toString(input, "UTF-8");
        } catch (IOException | NullPointerException e) {
            return "";
        }
    }

    /**
     * Converts byte array into stream.
     *
     * @param bytes byte array.
     * @return binary stream.
     */
    public static InputStream bytesToIntput(byte[] bytes) {
        return new ByteArrayInputStream(bytes);
    }

    /**
     * Converts String, that represents binary data, into byte array.
     * This String should be Base64 string.
     *
     * @param input formatted String type text.
     * @return byte array.
     */
    public static byte[] stringToBytes(String input) {
        return Base64.decodeBase64(input);
    }

    /**
     * Converts byte array into String.
     * This return String will be Base64 string.
     *
     * @param bytes byte array.
     * @return formatted String type text as binary data.
     */
    public static String bytesToString(byte[] bytes) {
        return Base64.encodeBase64String(bytes);
    }

    /**
     * Converts text <b>text</b> to Base64 formatted string.
     * @param text any non-Null string to be converted.
     * @return Base64 converted string or null if given text <b>text</b> was null.
     */
    public static String stringToBase64String(String text) {
        if (text == null) return null;
        return new String(Base64.encodeBase64(text.getBytes()));
    }

    /**
     * Converts Base64 formatted string to normal text.
     * @param base64Str any string that has been converted using Base64 algorithm.
     * @return text converted back from Base64 format or null if given Base64 string <b>base64Str</b> was null.
     */
    public static String base64StringToString(String base64Str) {
        if (base64Str == null) return null;
        return new String(Base64.decodeBase64(base64Str));
    }

    /**
     * Tries to get MIME type of given file <b>file</b>.
     * Method also uses {@link MimeMagic} extension hints.
     *
     * @param file file in file system, which MIME type will be determined.
     * @return MIME type as string.
     * Examples: "text/plain", "audio/*", "image/png", "video/mp4", "application/octet-stream"
     * @throws MagicParseException         if MimeMagic configuration initialization fails.
     *                                     {@link XMLReaderFactory} is used in order to read those configurations.
     *                                     This kind of exception might occur if there are problems reading XML by
     *                                     using this XML reader library.
     * @throws MagicMatchNotFoundException if MimeMagic algorithms are unable to determine MIME type of data.
     * @throws MagicException              if some error occurs while parsing data stream.
     *                                     This could be, for example, some {@link IOException} or {@link UnsupportedTypeException}.
     */
    public static String getMimeType(File file) throws MagicMatchNotFoundException, MagicException, MagicParseException {
        try {
            return MimeMagic.getMagicMatch(file, true).getMimeType();
        } catch (OutOfMemoryError e) {
            throw new MagicMatchNotFoundException("Magic match not found due to lack of heap space");
        }
    }

    /**
     * Checks whether file with file name <b>filename</b> is binary file (text file is not considered as binary file).
     * Basically this method is checking if file MIME type is not a text.
     * <p><u>Warning</u>: This method doesn't support archive or video file checking.
     * When ran against such files it will delay while reading file content
     * and in any case it will end up with {@link MagicMatchNotFoundException}.
     *
     * @param filename path to file.
     * @return true if file is binary file; false, if not or file not found, or it's impossible to detect,
     * whether it is or not a binary file.
     * @throws MagicMatchNotFoundException if file exists, but jMimeMagic cannot determine actual MIME type of file
     *                                     or file is too heavy to process.
     */
    public static boolean isBinaryFile(String filename) throws MagicMatchNotFoundException {
        try {
            String mimeType = getMimeType(new File(filename));
            return !mimeType.startsWith("text");
        } catch (MagicException | MagicParseException e) {
            return false;
        }
    }
}
