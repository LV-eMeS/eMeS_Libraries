package lv.emes.libraries.tools.logging;

import lv.emes.libraries.tools.lists.MS_List;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author eMeS
 * @version 1.1.
 */
public class MS_MultiLoggingSetupTest {

    private static MS_LoggingRepository repo1 = new MS_FileLogger("f1");
    private static MS_LoggingRepository repo2 = new MS_FileLogger("f2");
    private static List<MS_LoggingRepository> basicList;
    private static MS_List<MS_LoggingRepository> eMeSList;
    private final static String DELIMITER_LINE = "###";

    @BeforeClass
    public static void initialize() {
        basicList = new ArrayList<>();
        basicList.add(repo1);
        basicList.add(repo2);

        eMeSList = new MS_List<>();
        eMeSList.add(repo1);
        eMeSList.add(repo2);
    }

    @Test
    public void testDefaults() {
        MS_MultiLoggingSetup config = new MS_MultiLoggingSetup();
        assertNull(config.getRepositories());
        assertEquals(MS_LoggingOperations._LINE, config.getDelimiterLineText());
    }

    @Test
    public void testDelimiterSetter() {
        assertEquals(DELIMITER_LINE, new MS_MultiLoggingSetup().withDelimiterLineText(DELIMITER_LINE).getDelimiterLineText());
    }

    @Test
    public void testListOfRepositories() {
        MS_MultiLoggingSetup config;
        config = new MS_MultiLoggingSetup().withRepositories(null);
        assertNull(config.getRepositories());

        config = new MS_MultiLoggingSetup().withRepositories((List<MS_LoggingRepository>) null);
        assertNull(config.getRepositories());

        config = new MS_MultiLoggingSetup().withRepositories(basicList);
        assertEquals(eMeSList, config.getRepositories());

        config = new MS_MultiLoggingSetup().withRepositories(eMeSList);
        assertEquals(eMeSList, config.getRepositories());

    }

    @Test
    public void testRepositoryAddingMixed() {
        MS_MultiLoggingSetup config;
        //adding list of repositories + 1 extra repository
        config = new MS_MultiLoggingSetup().withRepositories(eMeSList).withRepository(repo1);
        assertNotEquals(eMeSList, config.getRepositories());
        assertEquals(repo1, config.getRepositories().get(0));
        assertEquals(repo2, config.getRepositories().get(1));
        assertEquals(repo1, config.getRepositories().get(2));
    }
}