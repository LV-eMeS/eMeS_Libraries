package lv.emes.libraries.patches.jmimemagic;

import net.sf.jmimemagic.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * As this jMimeMagic library is useful, but requires patching due to {@link OutOfMemoryError} error while
 * analyzing too big file, for example, <i>*.mp4</i> files, this class inherits all the basic {@link Magic}
 * functionality, but instead of {@link MagicMatcher} it uses patched matcher class {@link PatchedMagicMatcher}.
 *
 * @author eMeS
 * @version 1.0.
 */
public class MimeMagic extends Magic {

    private static Log log = LogFactory.getLog(Magic.class);
    private static HashMap hintMap = new HashMap();
    private static MagicParser magicParser = null;
    private static boolean initialized = false;

    private MimeMagic() { //not meant for instantiation
    }

    /**
     * create a parser and initialize it
     *
     * @throws MagicParseException DOCUMENT ME!
     */
    public static synchronized void initialize()
            throws MagicParseException {
        log.debug("initialize()");

        if (!initialized) {
            log.debug("initializing");
            magicParser = new MagicParser();
            magicParser.initialize();

            // build hint map
            Iterator i = magicParser.getMatchers().iterator();

            while (i.hasNext()) {
                PatchedMagicMatcher matcher = new PatchedMagicMatcher((MagicMatcher) i.next());
                String ext = matcher.getMatch().getExtension();

                if ((ext != null) && !ext.trim().equals("")) {
                    if (log.isDebugEnabled()) {
                        log.debug("adding hint mapping for extension '" + ext + "'");
                    }

                    addHint(ext, matcher);
                } else if (matcher.getMatch().getType().equals("detector")) {
                    String[] exts = matcher.getDetectorExtensions();

                    for (String ext1 : exts) {
                        if (log.isDebugEnabled()) {
                            log.debug("adding hint mapping for extension '" + ext1 + "'");
                        }

                        addHint(ext1, matcher);
                    }
                }
            }

            initialized = true;
        }
    }

    /**
     * Add a hint to use the specified matcher for the given extension
     *
     * @param extension DOCUMENT ME!
     * @param matcher   DOCUMENT ME!
     */
    @SuppressWarnings("unchecked")
    private static void addHint(String extension, PatchedMagicMatcher matcher) {
        if (hintMap.keySet().contains(extension)) {
            ArrayList a = (ArrayList) hintMap.get(extension);
            a.add(matcher);
        } else {
            ArrayList a = new ArrayList();
            a.add(matcher);
            hintMap.put(extension, a);
        }
    }

    /**
     * get a match from a stream of data
     *
     * @param data DOCUMENT ME!
     * @return DOCUMENT ME!
     * @throws MagicParseException         DOCUMENT ME!
     * @throws MagicMatchNotFoundException DOCUMENT ME!
     * @throws MagicException              DOCUMENT ME!
     */
    public static MagicMatch getMagicMatch(byte[] data)
            throws MagicParseException, MagicMatchNotFoundException, MagicException {
        return getMagicMatch(data, false);
    }

    /**
     * get a match from a stream of data
     *
     * @param data          DOCUMENT ME!
     * @param onlyMimeMatch DOCUMENT ME!
     * @return DOCUMENT ME!
     * @throws MagicParseException         DOCUMENT ME!
     * @throws MagicMatchNotFoundException DOCUMENT ME!
     * @throws MagicException              DOCUMENT ME!
     */
    public static MagicMatch getMagicMatch(byte[] data, boolean onlyMimeMatch)
            throws MagicParseException, MagicMatchNotFoundException, MagicException {
        log.debug("getMagicMatch(byte[])");

        if (!initialized) {
            initialize();
        }

        Collection matchers = magicParser.getMatchers();
        log.debug("getMagicMatch(byte[]): have " + matchers.size() + " matchers");

        PatchedMagicMatcher matcher;
        MagicMatch match;

        for (Object matcher1 : matchers) {
            matcher = new PatchedMagicMatcher((MagicMatcher) matcher1);

            log.debug("getMagicMatch(byte[]): trying to match: " +
                    matcher.getMatch().getMimeType());

            try {
                if ((match = matcher.test(data, onlyMimeMatch)) != null) {
                    log.debug("getMagicMatch(byte[]): matched " + matcher.getMatch().getMimeType());

                    return match;
                }
            } catch (IOException | UnsupportedTypeException e) {
                log.error("getMagicMatch(byte[]): " + e);
                throw new MagicException(e);
            }
        }

        throw new MagicMatchNotFoundException();
    }

    /**
     * get a match from a file
     *
     * @param file           the file to match content in
     * @param extensionHints whether or not to use extension to optimize order of content tests
     * @return the MagicMatch object representing a match in the file
     * @throws MagicParseException         DOCUMENT ME!
     * @throws MagicMatchNotFoundException DOCUMENT ME!
     * @throws MagicException              DOCUMENT ME!
     */
    public static MagicMatch getMagicMatch(File file, boolean extensionHints)
            throws MagicParseException, MagicMatchNotFoundException, MagicException {
        return getMagicMatch(file, extensionHints, false);
    }

    /**
     * get a match from a file
     *
     * @param file           the file to match content in
     * @param extensionHints whether or not to use extension to optimize order of content tests
     * @param onlyMimeMatch  only try to get mime type, no submatches are processed when true
     * @return the MagicMatch object representing a match in the file
     * @throws MagicParseException         DOCUMENT ME!
     * @throws MagicMatchNotFoundException DOCUMENT ME!
     * @throws MagicException              DOCUMENT ME!
     */
    public static MagicMatch getMagicMatch(File file, boolean extensionHints, boolean onlyMimeMatch)
            throws MagicParseException, MagicMatchNotFoundException, MagicException {
        log.debug("getMagicMatch(File)");

        if (!initialized) {
            initialize();
        }

        long start = System.currentTimeMillis();

        PatchedMagicMatcher matcher;
        MagicMatch match;

        // check for extension hints
        ArrayList checked = new ArrayList();

        if (extensionHints) {
            log.debug("trying to use hints first");

            String name = file.getName();
            int pos = name.lastIndexOf('.');

            if (pos > -1) {
                String ext = name.substring(pos + 1, name.length());

                if ((ext != null) && !ext.equals("")) {
                    if (log.isDebugEnabled()) {
                        log.debug("using extension '" + ext + "' for hinting");
                    }

                    Collection c = (Collection) hintMap.get(ext);

                    if (c != null) {
                        Iterator i = c.iterator();

                        while (i.hasNext()) {
                            matcher = new PatchedMagicMatcher((MagicMatcher) i.next());

                            log.debug("getMagicMatch(File): trying to match: " +
                                    matcher.getMatch().getDescription());

                            try {
                                if ((match = matcher.test(file, onlyMimeMatch)) != null) {
                                    log.debug("getMagicMatch(File): matched " +
                                            matcher.getMatch().getDescription());

                                    if (log.isDebugEnabled()) {
                                        long end = System.currentTimeMillis();
                                        log.debug("found match in '" + (end - start) +
                                                "' milliseconds");
                                    }

                                    return match;
                                }
                            } catch (UnsupportedTypeException | IOException e) {
                                log.error("getMagicMatch(File): " + e);
                                throw new MagicException(e);
                            }

                            // add to the already checked list
                            checked.add(matcher);
                        }
                    }
                } else {
                    log.debug("no file extension, ignoring hints");
                }
            } else {
                log.debug("no file extension, ignoring hints");
            }
        }

        Collection matchers = magicParser.getMatchers();
        log.debug("getMagicMatch(File): have " + matchers.size() + " matches");

        Iterator i = matchers.iterator();

        while (i.hasNext()) {
            matcher = new PatchedMagicMatcher((MagicMatcher) i.next());

            if (!checked.contains(matcher)) {
                log.debug("getMagicMatch(File): trying to match: " +
                        matcher.getMatch().getDescription());

                try {
                    if ((match = matcher.test(file, onlyMimeMatch)) != null) {
                        log.debug("getMagicMatch(File): matched " +
                                matcher.getMatch().getDescription());

                        if (log.isDebugEnabled()) {
                            long end = System.currentTimeMillis();
                            log.debug("found match in '" + (end - start) + "' milliseconds");
                        }

                        return match;
                    }
                } catch (UnsupportedTypeException | IOException e) {
                    log.error("getMagicMatch(File): " + e);
                    throw new MagicException(e);
                }
            } else {
                log.debug("getMagicMatch(File): already checked, skipping: " +
                        matcher.getMatch().getDescription());
            }
        }

        throw new MagicMatchNotFoundException();
    }
}
