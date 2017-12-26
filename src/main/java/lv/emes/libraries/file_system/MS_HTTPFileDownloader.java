package lv.emes.libraries.file_system;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Module consists of methods for I/O handling through HTTP.
 *
 * @version 1.0.
 */
public class MS_HTTPFileDownloader {

    /**
     * Downloads file from given <b>url</b> to local file system.
     * If local file already exists, it is replaced by downloaded one.
     *
     * @param url  direct internet URL to binary file.
     * @param dest local path to file.
     * @return true if success.
     */
    public static boolean downloadFile(String url, String dest) {
        URL website;
        try {
            website = new URL(url);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream(dest);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}