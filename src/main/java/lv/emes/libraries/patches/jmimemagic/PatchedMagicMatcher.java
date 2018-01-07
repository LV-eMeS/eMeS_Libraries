package lv.emes.libraries.patches.jmimemagic;

import net.sf.jmimemagic.MagicDetector;
import net.sf.jmimemagic.MagicMatch;
import net.sf.jmimemagic.MagicMatcher;
import net.sf.jmimemagic.UnsupportedTypeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.oro.text.perl.Perl5Util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Patch for {@link MagicMatcher}.
 * <p>Overridden public methods:
 * <ul>
 * <li>test</li>
 * <li>addSubMatcher</li>
 * <li>setMatch</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 */
public class PatchedMagicMatcher extends MagicMatcher {

    private static Log log = LogFactory.getLog(PatchedMagicMatcher.class);

    private List<PatchedMagicMatcher> subMatchers = new ArrayList<>();
    private MagicMatch match = null;

    public PatchedMagicMatcher(MagicMatcher realMatcher) {
        super();
        setMatch(realMatcher.getMatch());
        setSubMatchers(realMatcher.getSubMatchers());
    }

    public MagicMatch test(File f, boolean onlyMimeMatch) throws IOException, UnsupportedTypeException {
        log.debug("test(File)");

        int offset = match.getOffset();
        String description = match.getDescription();
        String type = match.getType();

        log.debug("test(File): testing '" + f.getName() + "' for '" + description + "'");

        log.debug("test(File): \n=== BEGIN MATCH INFO ==");
        log.debug(match.print());
        log.debug("test(File): \n=== END MATCH INFO ====\n");

        RandomAccessFile file;
        file = new RandomAccessFile(f, "r");

        try {
            int length;

            switch (type) {
                case "byte":
                    length = 1;
                    break;
                case "short":
                case "leshort":
                case "beshort":
                    length = 4;
                    break;
                case "long":
                case "lelong":
                case "belong":
                    length = 8;
                    break;
                case "string":
                    length = match.getTest().capacity();
                    break;
                case "regex":
                    final int matchLength = match.getLength();
                    length = (matchLength == 0) ? (int) file.length() - offset : matchLength;

                    if (length < 0) {
                        length = 0;
                    }
                    break;
                case "detector":
                    length = (int) file.length() - offset;

                    if (length < 0) {
                        length = 0;
                    }
                    break;
                default:
                    throw new UnsupportedTypeException("unsupported test type '" + type + "'");
            }

            // we know this match won't work since there isn't enough data for the test
            if (length > (file.length() - offset)) {
                return null;
            }

            byte[] buf = new byte[length];
            file.seek(offset);

            int bytesRead = 0;
            int size;
            boolean done = false;

            while (!done) {
                size = file.read(buf, 0, length - bytesRead);

                if (size == -1) {
                    throw new IOException("reached end of file before all bytes were read");
                }

                bytesRead += size;

                if (bytesRead == length) {
                    done = true;
                }
            }

            log.debug("test(File): stream size is '" + buf.length + "'");

            MagicMatch match = null;
            MagicMatch submatch;

            if (testInternal(buf)) {
                // set the top level match to this one
                match = getMatch();

                log.debug("test(File): testing matched '" + description + "'");

                // set the data on this match
                if ((!onlyMimeMatch) && (subMatchers != null) && (subMatchers.size() > 0)) {
                    log.debug("test(File): testing " + subMatchers.size() + " submatches for '" +
                            description + "'");

                    for (int i = 0; i < subMatchers.size(); i++) {
                        log.debug("test(File): testing submatch " + i);

                        PatchedMagicMatcher m = subMatchers.get(i);

                        if ((submatch = m.test(f, false)) != null) {
                            log.debug("test(File): submatch " + i + " matched with '" +
                                    submatch.getDescription() + "'");
                            match.addSubMatch(submatch);
                        } else {
                            log.debug("test(File): submatch " + i + " doesn't match");
                        }
                    }
                }
            }

            return match;
        } finally {
            try {
                file.close();
            } catch (Exception ignored) {
            }
        }
    }

    public void addSubMatcher(MagicMatcher m) {
        super.addSubMatcher(m);
        subMatchers.add(new PatchedMagicMatcher(m));
    }

    @SuppressWarnings("unchecked")
    public void setSubMatchers(Collection a) {
        super.setSubMatchers(a);
        subMatchers.clear();
        a.forEach(matcher -> subMatchers.add(new PatchedMagicMatcher((MagicMatcher) matcher)));
    }

    public void setMatch(MagicMatch match) {
        super.setMatch(match);
        this.match = match;
    }

    //*** PRIVATE METHODS ***

    private boolean testInternal(byte[] data) {
        log.debug("testInternal(byte[])");

        if (data.length == 0) {
            return false;
        }

        String type = match.getType();
        String test = new String(match.getTest().array());
        String mimeType = match.getMimeType();
        String description = match.getDescription();

        ByteBuffer buffer = ByteBuffer.allocate(data.length);

        if (type != null && test.length() > 0) {
            switch (type) {
                case "string":
                    buffer = buffer.put(data);

                    return testString(buffer);
                case "byte":
                    buffer = buffer.put(data);

                    return testByte(buffer);
                case "short":
                    buffer = buffer.put(data);

                    return testShort(buffer);
                case "leshort":
                    buffer = buffer.put(data);
                    buffer.order(ByteOrder.LITTLE_ENDIAN);

                    return testShort(buffer);
                case "beshort":
                    buffer = buffer.put(data);
                    buffer.order(ByteOrder.BIG_ENDIAN);

                    return testShort(buffer);
                case "long":
                    buffer = buffer.put(data);

                    return testLong(buffer);
                case "lelong":
                    buffer = buffer.put(data);
                    buffer.order(ByteOrder.LITTLE_ENDIAN);

                    return testLong(buffer);
                case "belong":
                    buffer = buffer.put(data);
                    buffer.order(ByteOrder.BIG_ENDIAN);

                    return testLong(buffer);
                case "regex":
                    return testRegex(new String(data));
                case "detector":
                    buffer = buffer.put(data);

                    return testDetector(buffer);
                default:
                    log.error("testInternal(byte[]): invalid test type '" + type + "'");
                    break;
            }
        } else {
            log.error("testInternal(byte[]): type or test is empty for '" + mimeType + " - " +
                    description + "'");
        }

        return false;
    }

    /**
     * test the data against the test byte
     *
     * @param data the data we are testing
     * @return if we have a match
     */
    private boolean testByte(ByteBuffer data) {
        log.debug("testByte()");

        String test = new String(match.getTest().array());
        char comparator = match.getComparator();
        long bitmask = match.getBitmask();

        byte b = data.get(0);
        b = (byte) (b & bitmask);
        log.debug("testByte(): decoding '" + test + "' to byte");

        int tst = Integer.decode(test).byteValue();
        byte t = (byte) (tst & 0xff);
        log.debug("testByte(): applying bitmask '" + bitmask + "' to '" + tst + "', result is '" +
                t + "'");
        log.debug("testByte(): comparing byte '" + b + "' to '" + t + "'");

        switch (comparator) {
            case '=':
                return t == b;

            case '!':
                return t != b;

            case '>':
                return t > b;

            case '<':
                return t < b;
        }

        return false;
    }

    /**
     * test the data against the byte array
     *
     * @param data the data we are testing
     * @return if we have a match
     */
    private boolean testString(ByteBuffer data) {
        log.debug("testString()");

        ByteBuffer test = match.getTest();
        char comparator = match.getComparator();

        byte[] b = data.array();
        byte[] t = test.array();

        boolean diff = false;
        int i;

        for (i = 0; i < t.length; i++) {
            log.debug("testing byte '" + b[i] + "' from '" + new String(data.array()) +
                    "' against byte '" + t[i] + "' from '" + new String(test.array()) + "'");

            if (t[i] != b[i]) {
                diff = true;

                break;
            }
        }

        switch (comparator) {
            case '=':
                return !diff;

            case '!':
                return diff;

            case '>':
                return t[i] > b[i];

            case '<':
                return t[i] < b[i];
        }

        return false;
    }

    /**
     * test the data against a short
     *
     * @param data the data we are testing
     * @return if we have a match
     */
    private boolean testShort(ByteBuffer data) {
        log.debug("testShort()");

        short val;
        String test = new String(match.getTest().array());
        char comparator = match.getComparator();
        long bitmask = match.getBitmask();

        val = byteArrayToShort(data);

        // apply bitmask before the comparison
        val = (short) (val & (short) bitmask);

        short tst;

        try {
            tst = Integer.decode(test).shortValue();
        } catch (NumberFormatException e) {
            log.error("testShort(): " + e);

            return false;

            //if (test.length() == 1) {
            //	tst = new Integer(Character.getNumericValue(test.charAt(0))).shortValue();
            //}
        }

        log.debug("testShort(): testing '" + Long.toHexString(val) + "' against '" +
                Long.toHexString(tst) + "'");

        switch (comparator) {
            case '=':
                return val == tst;

            case '!':
                return val != tst;

            case '>':
                return val > tst;

            case '<':
                return val < tst;
        }

        return false;
    }

    /**
     * test the data against a long
     *
     * @param data the data we are testing
     * @return if we have a match
     */
    private boolean testLong(ByteBuffer data) {
        log.debug("testLong()");

        long val;
        String test = new String(match.getTest().array());
        char comparator = match.getComparator();
        long bitmask = match.getBitmask();

        val = byteArrayToLong(data);

        // apply bitmask before the comparison
        val = val & bitmask;

        long tst = Long.decode(test);

        log.debug("testLong(): testing '" + Long.toHexString(val) + "' against '" + test +
                "' => '" + Long.toHexString(tst) + "'");

        switch (comparator) {
            case '=':
                return val == tst;

            case '!':
                return val != tst;

            case '>':
                return val > tst;

            case '<':
                return val < tst;
        }

        return false;
    }

    /**
     * test the data against a regex
     *
     * @param text the data we are testing
     * @return if we have a match
     */
    private boolean testRegex(String text) {
        log.debug("testRegex()");

        String test = new String(match.getTest().array());
        char comparator = match.getComparator();

        Perl5Util utility = new Perl5Util();
        log.debug("testRegex(): searching for '" + test + "'");

        if (comparator == '=') {
            return utility.match(test, text);
        } else if (comparator == '!') {
            return !utility.match(test, text);
        }

        return false;
    }

    /**
     * test the data using a detector
     *
     * @param data the data we are testing
     * @return if we have a match
     */
    private boolean testDetector(ByteBuffer data) {
        log.debug("testDetector()");

        String detectorClass = new String(match.getTest().array());

        try {
            log.debug("loading class: " + detectorClass);

            Class c = Class.forName(detectorClass);
            MagicDetector detector = (MagicDetector) c.newInstance();
            String[] types = detector.process(data.array(), match.getOffset(), match.getLength(),
                    match.getBitmask(), match.getComparator(), match.getMimeType(),
                    match.getProperties());

            if ((types != null) && (types.length > 0)) {
                // the match object has no mime type set, so set from the detector class processing
                match.setMimeType(types[0]);

                return true;
            }
        } catch (ClassNotFoundException e) {
            log.error("failed to load detector: " + detectorClass, e);
        } catch (InstantiationException e) {
            log.error("specified class is not a valid detector class: " + detectorClass, e);
        } catch (IllegalAccessException e) {
            log.error("specified class cannot be accessed: " + detectorClass, e);
        }

        return false;
    }

    private long byteArrayToLong(ByteBuffer data) {
        return (long) data.getInt(0);
    }

    private short byteArrayToShort(ByteBuffer data) {
        return data.getShort(0);
    }
}
