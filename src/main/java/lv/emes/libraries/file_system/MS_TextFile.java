package lv.emes.libraries.file_system;

import lv.emes.libraries.tools.lists.MS_List;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Module provides common operations with files and folders.
 * Public methods:
 * <ul>
 *     <li>openForReading</li>
 *     <li>close</li>
 *     <li>readln</li>
 *     <li>writeln</li>
 *     <li>write</li>
 *     <li>appendln</li>
 *     <li>append</li>
 *     <li>importStringListFromFile</li>
 *     <li>exportStringListToFile</li>
 *     <li>getFilename</li>
 * </ul>
 * Static methods:
 * <ul>
 *     <li>getProjectDirectory</li>
 *     <li>fileExists</li>
 *     <li>createEmptyFile</li>
 *     <li>getResourceFileTextAsString</li>
 * </ul>
 * @version 2.2.
 */
public class MS_TextFile {
    private PrintWriter fFileWriter = null; //main object that will perform line WRITING.
    private BufferedReader fFileReader = null; //main object that will perform line READING.
    private PrintWriter fFileAppender = null; //main object that will perform line APPENDING.

    private BufferedWriter fbw = null;
    private FileWriter ffw = null;
    private BufferedWriter fba = null;
    private FileWriter ffa = null;

    protected String fFilename;

    //KONSTRUKTORI

    /**
     * @param aFilename name of file, which will be processed by current object.
     *                  If full path name is not provided, file will be created or will be searched in root directory of executable application.
     */
    public MS_TextFile(String aFilename) {
        fFilename = aFilename;
    }

    /**
     * Opens stream to be readed as text.
     *
     * @param stream reference to stream.
     */
    public MS_TextFile(InputStream stream) {
        if (stream != null)
            fFileReader = new BufferedReader(new InputStreamReader(stream));
    }

    private void closeThisFileWriting() {
        if (fFileWriter != null) {
            fFileWriter.close();
            fFileWriter = null;
            fbw = null;
            ffw = null;
        }
    }

    private void closeThisFileAppending() {
        if (fFileAppender != null) {
            fFileAppender.close();
            fFileAppender = null;
            fba = null;
            ffa = null;
        }
    }

    protected void closeThisFileReading() {
        if (fFileReader != null) {
            try {
                fFileReader.close();
            } catch (IOException ignored) {
            } finally {
                fFileReader = null;
            }
        }
    }

    /**
     * Creates mechanism for line reading from file.
     *
     * @return true, if file successfully opened or is already opened.
     */
    private boolean pCreateFileLinkForReading() {
        if (fFileReader != null) return true;
        try {
            FileReader ffr = new FileReader(fFilename);
            fFileReader = new BufferedReader(ffr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Creates mechanism for new line writing to file.
     *
     * @return true, if file successfully opened or is already opened.
     */
    private boolean pCreateFileLinkForWriting() {
        if (fFileReader != null) return true;

        try {
            createFolderForFileIfNeeded(fFilename);

            //now to create link for file writer
            ffw = new FileWriter(fFilename, false); //true, if append
            fbw = new BufferedWriter(ffw);
            fFileWriter = new PrintWriter(fbw);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Creates mechanism for new line appending to file.
     *
     * @return true, if file successfully opened or is already opened.
     */
    private boolean pCreateFileLinkForAppending() {
        if (fFileAppender != null) return true;
        try {
            createFolderForFileIfNeeded(fFilename);

            ffa = new FileWriter(fFilename, true); //true, JO append
            fba = new BufferedWriter(ffa);
            fFileAppender = new PrintWriter(fba);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Creates folder for file to be created in if a folder doesn't exist.
     * @param filename name of file that will be laying in folder that will be created.
     */
    private void createFolderForFileIfNeeded(String filename) {
        //do checking if directory exists to always create file properly altogether with directory
        String directoryOfFile = MS_FileSystemTools.getDirectoryOfFile(filename);
        if (! MS_FileSystemTools.directoryExists(directoryOfFile))
            MS_FileSystemTools.createNewDirectory(directoryOfFile);
    }

    //PUBLIC METHODS

    /**
     * Opens file just for reading.
     *
     * @return true if file exists and is successfully opened.
     */
    public boolean openForReading() {
        if (fFileWriter != null) closeThisFileWriting();
        if (fFileAppender != null) closeThisFileAppending();
        if (fFileReader != null)
            return false;
        else
            return pCreateFileLinkForReading();
    }

    /**
     * Closes file. Doesn't matter if file was opened for reading or writing.
     */
    public void close() {
        closeThisFileWriting();
        closeThisFileAppending();
        closeThisFileReading();
    }

    /**
     * Reads one line from the file. If file isn't opened, method will open the file.
     * For reading all the file by loop consider using construction: <p><code>while ( (str = reader.readln()) != null) {...}</code>!
     *
     * @return current line in file or null if end of file is reached or file is empty.
     */
    public String readln() {
        return readln(false);
    }

    /**
     * Reads one line from the file. If file isn't opened, method will open the file.
     * For reading all the file by loop consider using construction: <p><code>while ( (str = reader.readln()) != null) {...}</code>!
     *
     * @param aCloseAfterRead if true file will be close after successful reading of line.
     * @return current line in file or null if end of file is reached or file is empty.
     */
    public String readln(boolean aCloseAfterRead) {
        String res = null;
        boolean isFileOpened = true;

        if (fFileWriter != null) //if has been opened, sorry, must close!
            this.closeThisFileWriting();
        else if (fFileAppender != null) //if has been opened for appending, sorry, must close!
            this.closeThisFileAppending();
        else if (fFileReader == null) //if reader doesn't exist, open it!
            isFileOpened = openForReading();

        if (isFileOpened) { //if successfully opened the read it!
            try {
                res = fFileReader.readLine();
            } catch (IOException e) {
            } finally {
                if (aCloseAfterRead)
                    closeThisFileReading();
            }
        }
        return res;
    }

    /**
     * Writes the file contents by line that contains <b>aText</b>. If file exist, the contents of file is overridden.
     * If file was already opened for reading, it will be closed and deleted, then recreated.
     * Warning: changes in file will be saved only after file closing, so <b>aCloseAfterWrite = true</b> should be used when file appending is finished.
     *
     * @param aText text that will be written to new line in file.
     * @return true if writing successful.
     */
    public boolean writeln(String aText) {
        return writeln(aText, false);
    }

    /**
     * Writes the file contents by text that contains <b>aText</b> adding it to a text where cursor (in file) stands. If file exist, the contents of file is overridden in first time open for writing.
     * If file was already opened for reading, it will be closed and deleted, then recreated.
     * Warning: changes in file will be saved only after file closing.
     *
     * @param aText text that will be written to line file where cursor stands.
     * @return true if writing successful.
     */
    public boolean write(String aText) {
        return write(aText, false);
    }

    /**
     * Writes the file contents by line that contains <b>aText</b>. If file exist, the contents of file is overridden in first time open for writing.
     * If file was already opened for reading, it will be closed and deleted, then recreated.
     * Warning: changes in file will be saved only after file closing, so <b>aCloseAfterWrite = true</b> should be used when file writing is finished.
     *
     * @param aText            text that will be written to new line in file.
     * @param aCloseAfterWrite if true file will be close after successful appending of line.
     * @return true if writing successful.
     */
    public boolean writeln(String aText, boolean aCloseAfterWrite) {
        if (fFileWriter == null) {
            this.close();
            if (!pCreateFileLinkForWriting())
                return false;
        }

        fFileWriter.println(aText);

        if (aCloseAfterWrite)
            closeThisFileWriting();
        return true;
    }

    public boolean write(String aText, boolean aCloseAfterWrite) {
        if (fFileWriter == null) {
            this.close();
            if (!pCreateFileLinkForWriting())
                return false;
        }

        fFileWriter.print(aText);

        if (aCloseAfterWrite)
            closeThisFileWriting();
        return true;
    }

    /**
     * Appends the file contents by line that contains <b>aText</b>. If file doesn't exist, a new file is created.
     * If file isn't opened, method opens a file for appending. After appending file <u>is still opened</u>.
     * Warning: changes in file will be saved only after file closing, so <b>aCloseAfterWrite = true</b> should be used when file appending is finished.
     *
     * @param aText text that will be appended to new line in file.
     * @return true if appending successful.
     */
    public boolean appendln(String aText) {
        return appendln(aText, false);
    }

    /**
     * Appends the file contents by text that contains <b>aText</b>. If file doesn't exist, a new file is created.
     * If file isn't opened, method opens a file for appending. After appending file <u>is still opened</u>.
     * Warning: changes in file will be saved only after file closing, so <b>aCloseAfterWrite = true</b> should be used when file appending is finished.
     *
     * @param aText text that will be appended to existing line in file regarding on where cursor stands.
     * @return true if appending successful.
     */
    public boolean append(String aText) {
        return append(aText, false);
    }

    /**
     * Appends the file contents by line that contains <b>aText</b>. If file doesn't exist, a new file is created.
     * If file isn't opened, method opens a file for appending. After appending file is still opened unless <b>aCloseAfterAppend = true</b>.
     * Warning: changes in file will be saved only after file closing, so <b>aCloseAfterWrite = true</b> should be used when file appending is finished.
     *
     * @param aText             text that will be appended to new line in file.
     * @param aCloseAfterAppend if true file will be close after successful appending of line.
     * @return true if appending successful.
     */
    public boolean appendln(String aText, boolean aCloseAfterAppend) {
        if (fFileAppender == null) {
            this.close();
            if (!pCreateFileLinkForAppending())
                return false;
        }

        fFileAppender.println(aText);

        if (aCloseAfterAppend)
            closeThisFileAppending();
        return true;
    }

    /**
     * Appends the file contents by text that contains <b>aText</b>. If file doesn't exist, a new file is created.
     * If file isn't opened, method opens a file for appending. After appending file is still opened unless <b>aCloseAfterAppend = true</b>.
     * Warning: changes in file will be saved only after file closing, so <b>aCloseAfterWrite = true</b> should be used when file appending is finished.
     *
     * @param aText             text that will be appended to new line in file.
     * @param aCloseAfterAppend if true file will be close after successful appending of line.
     * @return true if appending successful.
     */
    public boolean append(String aText, boolean aCloseAfterAppend) {
        if (fFileAppender == null) {
            this.close();
            if (!pCreateFileLinkForAppending())
                return false;
        }

        fFileAppender.print(aText);

        if (aCloseAfterAppend)
            closeThisFileAppending();
        return true;
    }

    public String getFilename() {
        return fFilename;
    }

    /**
     * Reads all the file and creates string list from contents line by line creating string list's element by element.
     * <br><u>Note</u>: before and after this method file will do <b>close</b> to ensure that whole file will be read and
     * that after this method file will be unlocked to append it, rewrite or even delete.
     *
     * @return list that contains all the lines of the file.
     * @see MS_TextFile#close()
     */
    public List<String> importStringListFromFile() {
        List<String> res = new MS_List<String>();
        String row;
        close(); //to be sure that whole file will be read
        while ((row = this.readln()) != null)
            res.add(row);
        close();
        return res;
    }

    /**
     * Reads all the file and saves it into String type object list.
     *
     * @param aFileName path to text file.
     * @return list in which each element represents a line in file.
     */
    public static List<String> importStringListFromFile(String aFileName) {
        MS_TextFile file = new MS_TextFile(aFileName);
        return file.importStringListFromFile();
    }

    /**
     * Exports list of strings to this particular text file.
     *
     * @param aStringList list of strings.
     */
    public void exportStringListToFile(List<String> aStringList) {
        fFileWriter = null;
        for (String row : aStringList)
            this.writeln(row);
        this.close();
    }

    //STATISKAS METODES

    /**
     * Exports list of strings to text file.
     *
     * @param aFileName   path to a file that will be created.
     * @param aStringList list of string where each string represents a line in file.
     */
    public static void exportStringListToFile(String aFileName, List<String> aStringList) {
        MS_TextFile file = new MS_TextFile(aFileName);
        file.exportStringListToFile(aStringList);
    }

    /**
     * @return path to a directory where application is launched.
     * @see lv.emes.libraries.file_system.MS_FileSystemTools#getProjectDirectory
     */
    public static String getProjectDirectory() {
        return MS_FileSystemTools.getProjectDirectory();
    }

    public static boolean fileExists(String aFileName) {
        return MS_FileSystemTools.fileExists(aFileName);
    }

    public static boolean fileExists(File aFile) {
        return MS_FileSystemTools.fileExists(aFile);
    }

    /**
     * Atomically creates a new, empty file named by this abstract pathname if and only if a file with this name does not yet exist. The check for the existence of the file and the creation of the file if it does not exist are a single operation that is atomic with respect to all other filesystem activities that might affect the file.
     * Note: this method should not be used for file-locking, as the resulting protocol cannot be made to work reliably. The FileLock facility should be used instead.
     *
     * @param aFileName - path + short filename, in other words: full path to file.
     * @return true if the named file did not exist and was successfully created; false if the named file already exists
     */
    public static boolean createEmptyFile(String aFileName) {
        return MS_FileSystemTools.createEmptyFile(aFileName);
    }

    /**
     * Returns content of given file as string (each line delimited with <b>aLineDelimiter</b>).
     *
     * @param aFilename      path to a file.
     * @param aLineDelimiter "", " ", "\n" or ",", or even something else.
     * @return whole file as plaintext where each line is divided by specified <b>aLineDelimiter</b>.
     * If file is not found then returns empty string.
     */
    public static String getFileTextAsString(String aFilename, String aLineDelimiter) {
        MS_TextFile file = new MS_TextFile(aFilename);
        StringBuilder res = new StringBuilder();
        String tmp;
        //try to read first line in the beginning
        if ((tmp = file.readln()) != null)
            res.append(tmp);
        //now if there is more lines in file do appending with delimiter
        while ((tmp = file.readln()) != null) {
            res.append(aLineDelimiter);
            res.append(tmp);
        }
        file.close();
        return res.toString();
    }

    /**
     * Returns content of given file as string as it is. Reads file as resource stream.
     * <br><u>Note</u>: resource must be for read only and it must be prepared before launching any script that gets it.
     * @param filename path to file as resource (do not use full filename, instead use path as it is located in classpath).
     * @return whole file as plain text as it is.
     */
    public static String getResourceFileTextAsString(String filename) {
        InputStream iStream = MS_TextFile.class.getClassLoader().getResourceAsStream(filename);
        return getStreamTextAsString(iStream);
    }

    /**
     * Returns content of given stream as UTF-8 encoded string.
     * @param stream a stream of text file or another stream that could be converted to string.
     * @return whole stream as plain UTF-8 encoded text as it is. Null if input is null or I/O error occurs.
     */
    public static String getStreamTextAsString(InputStream stream) {
        byte[] bytes;
        try {
            bytes = IOUtils.toByteArray(stream);
        } catch (Exception e) {
            return null;
        }
        try {
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return new String(bytes, Charset.defaultCharset());
        }
    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
    }

    /**
     * Checks whether file with file name <b>filename</b> is text file.
     * @param filename path to file.
     * @return true if file is text file; false, if not or file not found.
     */
    public static boolean isTextFile(String filename) {
        String mimeType = "";
        try {
            mimeType = Magic.getMagicMatch(new File(filename), true).getMimeType();
        } catch (MagicMatchNotFoundException | MagicException | MagicParseException e) {
            e.printStackTrace();
            return false;
        }
        return mimeType.startsWith("text");
    }
}