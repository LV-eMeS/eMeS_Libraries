package lv.emes.libraries.tools.logging;

import lv.emes.libraries.storage.MS_RepositoryDataExchangeException;
import lv.emes.libraries.testdata.TestData;
import lv.emes.libraries.utilities.MS_CodingUtils;
import lv.emes.libraries.utilities.MS_TestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Map;

import static lv.emes.libraries.tools.logging.MS_RemoteLoggingRepository.MAX_SECRET_LENGTH;
import static org.junit.Assert.*;

/**
 * @author eMeS
 * @version 1.0.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MS_RemoteLoggingRepositoryTest {

    private static final String PRODUCT_OWNER = "eMeS";
    private static final String PRODUCT_NAME = "Testing";
    private static final String SECRET_KEY = "Testing eMeS remote logging repository 2018...";
//    private static final String HOSTNAME  = TestData.HTTP_PREFIX + TestData.TESTING_SERVER_HOSTAME;
    private static final String HOSTNAME  = TestData.HTTP_PREFIX + "localhost";

    private static MS_RemoteLoggingRepository repository;
    private static MS_InMemoryLoggingRepository loggedEvents;

    @BeforeClass
    public static void initialize() {
        repository = new MS_RemoteLoggingRepository(PRODUCT_OWNER, PRODUCT_NAME,
                new LoggingRemoteServerProperties()
                        .withHost(HOSTNAME)
                        .withSecret(SECRET_KEY));
        loggedEvents = new MS_InMemoryLoggingRepository();
        MS_MultiLoggingSetup setup = new MS_MultiLoggingSetup().withRepository(loggedEvents).withRepository(repository);
        MS_MultiLogger logger = new MS_MultiLogger(setup);

        //do some initial logging to both remote and in-memory repositories
        logger.info("Info: Starting remote logging repository test");
        logger.warning("Warning: Those logging events will be stored to real logging server");
        logger.error("Error: Tests will fail in initialization part if server will be unreachable");
        logger.error("Error: Lines as delimiters are not supported for remote logging repository",
                new MS_TestUtils.MS_UnCheckedException2("Those lines will be ignored there"));
        logger.line();
        MS_CodingUtils.sleep(2000); //lets give some meaningful time for logging server to take requests
    }

    @Test
    public void test01RepoInitialized() {
        assertTrue("Repository is not initialized", repository.isInitialized());
    }

    @Test
    public void test02ConstructorParametersSetProperly() {
        assertEquals(PRODUCT_OWNER, repository.getProductOwner());
        assertEquals(PRODUCT_NAME, repository.getProductName());
        assertEquals(PRODUCT_OWNER, repository.getRepositoryRoot());
        assertEquals(PRODUCT_NAME, repository.getRepositoryCategoryName());
    }

    @Test
    public void test03FindAllEvents() {
        Map<Instant, MS_LoggingEvent> events = repository.findAll();
        loggedEvents.getEventList().forEachItem((event, i) -> {
            if (!event.getType().equals(LoggingEventTypeEnum.UNSPECIFIED))
                assertEquals("Not found item at index: " + i
                                + "\nExpected items:\n" + loggedEvents.getEventList().toString()
                                + "\nActual items:\n" + events.values().toString() + "\n"
                        , event, events.get(event.getTime().toInstant()));
        });
    }

    @Test
    public void test04RemoveAll() {
        repository.removeAll();
        assertEquals(0, repository.findAll().size());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void test10FindOneUnsupported() {
        repository.find(ZonedDateTime.now().toInstant());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void test11RemoveUnsupported() {
        repository.remove(ZonedDateTime.now().toInstant());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void test12GetSizeUnsupported() {
        repository.size();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void test13GetLengthUnsupported() {
        repository.length();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void test14GetCountUnsupported() {
        repository.count();
    }

    @Test
    public void test15InvalidSecret() {
        MS_RemoteLoggingRepository repository = new MS_RemoteLoggingRepository(PRODUCT_OWNER, PRODUCT_NAME,
                new LoggingRemoteServerProperties()
                        .withHost(HOSTNAME)
                        .withSecret("Invalid secret"));

        boolean requestPassed = true;
        MS_LoggingEvent event = new MS_LoggingEvent().withTime(ZonedDateTime.now()).withType(LoggingEventTypeEnum.INFO)
                .withMessage("This message will not reach repository");
        try {
            repository.add(event.getTime().toInstant(), event);
        } catch (MS_RepositoryDataExchangeException e) {
            requestPassed = false;
        }
        assertFalse(requestPassed);
        requestPassed = true;

        try {
            repository.findAll();
        } catch (MS_RepositoryDataExchangeException e) {
            requestPassed = false;
        }
        assertFalse(requestPassed);
        requestPassed = true;

        try {
            repository.removeAll();
        } catch (MS_RepositoryDataExchangeException e) {
            requestPassed = false;
        }
        assertFalse(requestPassed);
    }

    @Test(expected = MS_RepositoryDataExchangeException.class)
    public void test16TooLongSecret() {
        new MS_RemoteLoggingRepository(PRODUCT_OWNER, PRODUCT_NAME,
                new LoggingRemoteServerProperties()
                        .withHost(TestData.HTTP_PREFIX + TestData.TESTING_SERVER_HOSTAME)
                        .withSecret(RandomStringUtils.random(MAX_SECRET_LENGTH + 1)))
                .removeAll();
    }
}