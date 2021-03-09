package lv.emes.libraries.tools.logging;

import lv.emes.libraries.tools.lists.MS_List;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author eMeS
 * @version 1.1.
 */
public class MS_MultiLoggingSetupTest {

    private static final MS_LoggingRepository repo1 = new MS_FileLogger("f1");
    private static final MS_LoggingRepository repo2 = new MS_FileLogger("f2");
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
        assertThat(config.getRepositories()).isNull();
        assertThat(config.getDelimiterLineText()).isEqualTo(MS_LoggingOperations._LINE);
    }

    @Test
    public void testDelimiterSetter() {
        assertThat(new MS_MultiLoggingSetup().withDelimiterLineText(DELIMITER_LINE).getDelimiterLineText()).isEqualTo(DELIMITER_LINE);
    }

    @Test
    public void testListOfRepositories() {
        MS_MultiLoggingSetup config;
        config = new MS_MultiLoggingSetup().withRepositories(null);
        assertThat(config.getRepositories()).isNull();

        config = new MS_MultiLoggingSetup().withRepositories((List<MS_LoggingRepository>) null);
        assertThat(config.getRepositories()).isNull();

        config = new MS_MultiLoggingSetup().withRepositories(basicList);
        assertThat(config.getRepositories()).isEqualTo(eMeSList);

        config = new MS_MultiLoggingSetup().withRepositories(eMeSList);
        assertThat(config.getRepositories()).isEqualTo(eMeSList);

    }

    @Test
    public void testRepositoryAddingMixed() {
        MS_MultiLoggingSetup config;
        //adding list of repositories + 1 extra repository
        config = new MS_MultiLoggingSetup().withRepositories(eMeSList).withRepository(repo1);
        assertThat(config.getRepositories()).isNotEqualTo(eMeSList);
        assertThat(config.getRepositories().get(0)).isEqualTo(repo1);
        assertThat(config.getRepositories().get(1)).isEqualTo(repo2);
        assertThat(config.getRepositories().get(2)).isEqualTo(repo1);
    }
}