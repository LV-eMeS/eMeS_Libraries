package lv.emes.libraries.tools.logging;

import lv.emes.libraries.file_system.MS_FileSystemTools;
import lv.emes.libraries.file_system.MS_TextFile;
import lv.emes.libraries.testdata.TestData;
import lv.emes.libraries.utilities.MS_CodingUtils;
import lv.emes.libraries.utilities.MS_StringUtils;
import lv.emes.libraries.utilities.MS_TestUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests multi logger functionality by using {@link MS_FileLogger} and custom repositories, which
 * stores their events in message and exception lists.
 *
 * @author eMeS
 * @version 1.0.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MS_MultiLoggerTest {

    private static final String TEST_TEMP_DIR_PATH = TestData.TEMP_DIR + "MS_MultiLoggerTest/";
    private static final String FILE_LOGGER_PATH = TEST_TEMP_DIR_PATH + "fileLogger.log";
    private final static String DELIMITER_LINE = "###";
    private final static long TIME_FOR_LOGGERS_TO_EXECUTE = 150;

    private MS_MultiLogger logger;
    private MS_InMemoryLoggingRepository inMemoryLogger1;
    private MS_InMemoryLoggingRepository inMemoryLogger2;
    private static final MS_TextFile checkerFile = new MS_TextFile(FILE_LOGGER_PATH);

    @Before
    public void setUp() {
        MS_FileLogger fileLogger = new MS_FileLogger(FILE_LOGGER_PATH);
        inMemoryLogger1 = new MS_InMemoryLoggingRepository();
        inMemoryLogger2 = new MS_InMemoryLoggingRepository();
        logger = new MS_MultiLogger(new MS_MultiLoggingSetup().withDelimiterLineText(DELIMITER_LINE)
                .withRepository(fileLogger).withRepository(inMemoryLogger1).withRepository(inMemoryLogger2));
    }

    @AfterClass
    public static void finalizeTestConditions() {
        checkerFile.close();
        MS_FileSystemTools.deleteDirectory(TEST_TEMP_DIR_PATH);
    }

    @Test
    public void test01Line() {
        logger.line();
        MS_CodingUtils.sleep(TIME_FOR_LOGGERS_TO_EXECUTE);
        String lineInFile = checkerFile.readln();

        assertThat(lineInFile).isEqualTo(DELIMITER_LINE);
        MS_LoggingEvent loggedEvent1 = inMemoryLogger1.getEventList().get(0);
        MS_LoggingEvent loggedEvent2 = inMemoryLogger2.getEventList().get(0);
        assertThat(loggedEvent1.getMessage()).isEqualTo(DELIMITER_LINE);
        assertThat(loggedEvent2.getMessage()).isEqualTo(DELIMITER_LINE);

        //check some rules regarding what should not be logged in this case
        assertThat(loggedEvent1.getType()).isEqualTo(MS_LoggingEventTypeEnum.UNSPECIFIED);
        assertThat(loggedEvent2.getType()).isEqualTo(MS_LoggingEventTypeEnum.UNSPECIFIED);
        assertThat(loggedEvent1.getError()).isEqualTo(null);
        assertThat(loggedEvent2.getError()).isEqualTo(null);
    }

    @Test
    public void test02SimpleMessages() {
        String infoEvent = "Testing info";
        String warningEvent = "Testing warning";
        String errorEvent = "Testing error";

        logger.info(infoEvent);
        MS_CodingUtils.sleep(TIME_FOR_LOGGERS_TO_EXECUTE);
        logger.warn(warningEvent);
        MS_CodingUtils.sleep(TIME_FOR_LOGGERS_TO_EXECUTE);
        logger.error(errorEvent);
        MS_CodingUtils.sleep(TIME_FOR_LOGGERS_TO_EXECUTE);

        //compare both in-memory lists
        assertThat(inMemoryLogger1.getEventList().count()).isEqualTo(3);
        assertThat(inMemoryLogger2.getEventList().count()).isEqualTo(inMemoryLogger1.getEventList().count());

        //compare file content with events that should've been logged
        String lineInFile;
        lineInFile = checkerFile.readln();
        assertThat(MS_StringUtils.textContains(lineInFile, MS_LoggingEventTypeEnum.INFO.name())).isTrue();
        assertThat(MS_StringUtils.textContains(lineInFile, infoEvent)).isTrue();

        lineInFile = checkerFile.readln();
        assertThat(MS_StringUtils.textContains(lineInFile, MS_LoggingEventTypeEnum.WARN.name())).isTrue();
        assertThat(MS_StringUtils.textContains(lineInFile, warningEvent)).isTrue();

        lineInFile = checkerFile.readln();
        assertThat(MS_StringUtils.textContains(lineInFile, MS_LoggingEventTypeEnum.ERROR.name())).isTrue();
        assertThat(MS_StringUtils.textContains(lineInFile, errorEvent)).isTrue();
    }

    @Test
    public void test03Error() {
        String errorEvent = "Testing error";
        String errorMessage = "Checked exception error message";
        logger.error(errorEvent, new MS_TestUtils.MS_CheckedException(errorMessage));
        //here we check only in-memory loggers, so it will go faster
        MS_CodingUtils.sleep(TIME_FOR_LOGGERS_TO_EXECUTE / 2);

        MS_LoggingEvent loggedEvent1 = inMemoryLogger1.getEventList().get(0);
        MS_LoggingEvent loggedEvent2 = inMemoryLogger2.getEventList().get(0);
        assertThat(loggedEvent1.getType()).isEqualTo(MS_LoggingEventTypeEnum.ERROR);
        assertThat(loggedEvent2.getType()).isEqualTo(MS_LoggingEventTypeEnum.ERROR);
        assertThat(loggedEvent1.getMessage()).isEqualTo(errorEvent);
        assertThat(loggedEvent2.getMessage()).isEqualTo(errorEvent);
        assertThat(loggedEvent1.getError().getClass()).isEqualTo(MS_TestUtils.MS_CheckedException.class);
        assertThat(loggedEvent2.getError().getClass()).isEqualTo(MS_TestUtils.MS_CheckedException.class);
        assertThat(loggedEvent1.getError().getMessage()).isEqualTo(errorMessage);
        assertThat(loggedEvent2.getError().getMessage()).isEqualTo(errorMessage);
        //check if time is equal for both loggers, cause time should be taken
        //from multi logger instead of generating it at logger repository level
        assertThat(loggedEvent2.getTime()).isEqualTo(loggedEvent1.getTime());
    }
}