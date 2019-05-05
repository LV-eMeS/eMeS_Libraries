package lv.emes.libraries.tools.logging;

import lv.emes.libraries.communication.http.MS_Polling;
import lv.emes.libraries.storage.MS_RepositoryDataExchangeException;
import lv.emes.libraries.testdata.TestData;
import lv.emes.libraries.utilities.MS_CodingUtils;
import lv.emes.libraries.utilities.MS_ExecutionFailureException;
import lv.emes.libraries.utilities.MS_TestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.threeten.bp.Instant;
import org.threeten.bp.ZonedDateTime;

import java.util.Map;

import static lv.emes.libraries.tools.logging.MS_RemoteLoggingRepository.MAX_SECRET_LENGTH;
import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MS_RemoteLoggingRepositoryTest {

    private static final String PRODUCT_OWNER = "eMeS";
    private static final String PRODUCT_NAME = "Testing";
    private static final String SECRET_KEY = "Testing eMeS remote logging repository 2018...";
    private static final String HOSTNAME  = TestData.HTTP_PREFIX + TestData.TESTING_SERVER_HOSTAME;
//    private static final String HOSTNAME  = TestData.HTTP_PREFIX + "localhost";

    private static MS_RemoteLoggingRepository repository;
    private static MS_InMemoryLoggingRepository loggedEvents;

    @BeforeClass
    public static void initialize() {
        repository = new MS_RemoteLoggingRepository(PRODUCT_OWNER, PRODUCT_NAME,
                new MS_LoggingRemoteServerProperties()
                        .withHost(HOSTNAME)
                        .withSecret(SECRET_KEY)
                        .withHttpConnectionTimeout(15000));
        loggedEvents = new MS_InMemoryLoggingRepository();
        MS_MultiLoggingSetup setup = new MS_MultiLoggingSetup().withRepository(loggedEvents).withRepository(repository);
        MS_MultiLogger logger = new MS_MultiLogger(setup);

        //do some initial logging to both remote and in-memory repositories
        logger.info("Info: Starting remote logging repository test");
        logger.warn("Warning: Those logging events will be stored to real logging server");
        logger.error("Error: Tests will fail in initialization part if server will be unreachable. ĀЮ漢");
        try {
            throw new MS_TestUtils.MS_UnCheckedException2("Those lines will be ignored there. 語");
        } catch (Exception e) {
            logger.error("Error: Lines as delimiters are not supported for remote logging repository", e);
        }
        logger.line();
        MS_CodingUtils.sleep(3500); //lets give some meaningful time for logging server to take requests
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
    public void test03FindAllEvents() throws MS_ExecutionFailureException {
        MS_CodingUtils.executeWithRetry(4, () -> {
            Map<Instant, MS_LoggingEvent> events = repository.findAll();
            assertEquals("There should be 5 events logged at test initialization step", 5, loggedEvents.getEventList().size());
            assertThatEventsMatchOnesWeAddedOnInitialization(events);
        });
    }

    @Test
    public void test04FindEventsInPages() throws MS_ExecutionFailureException {
        MS_Polling<Map<Instant, MS_LoggingEvent>> polling = new MS_Polling<Map<Instant, MS_LoggingEvent>>()
                .withSleepInterval(250)
                .withMaxPollingAttempts(16)
                .withAction(() -> repository.findPage(1, 2))
                .withCheck(events -> events.size() == 2)
                .withStatusMessageProducer((p) -> "Looking for page with 2 logged events");
        Map<Instant, MS_LoggingEvent> eventsIn1StPage = polling.poll();
        assertThatEventsMatchOnesWeAddedOnInitialization(eventsIn1StPage);

        // Second page with rest of elements
        Map<Instant, MS_LoggingEvent> eventsIn2ndPage = polling
                .withStatusMessage("Looking for last page with rest of logged events (still should be 2)")
                .withAction(() -> repository.findPage(2, 2))
                .poll();
        assertThatEventsMatchOnesWeAddedOnInitialization(eventsIn2ndPage);

        // Check that in first page there are newest event records
        Instant timeOfEvent1 = MS_CodingUtils.getMapElementKey(eventsIn1StPage, 0);
        Instant timeOfEvent2 = MS_CodingUtils.getMapElementKey(eventsIn1StPage, 1);
        Instant timeOfEvent3 = MS_CodingUtils.getMapElementKey(eventsIn2ndPage, 0);
        Instant timeOfEvent4 = MS_CodingUtils.getMapElementKey(eventsIn2ndPage, 1);
        assertTrue("In first page first element should be newer than second element, but it's older", timeOfEvent1.isAfter(timeOfEvent2));
        assertTrue("Elements in second page should be older than ones in first page, but 1st page 2nd element is before than 1st element in 2nd page", timeOfEvent2.isAfter(timeOfEvent3));
        assertTrue("In second page first element should be newer than second element, but it's older", timeOfEvent3.isAfter(timeOfEvent4));
    }

    @Test
    public void test05RemoveAll() {
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
                new MS_LoggingRemoteServerProperties()
                        .withHost(HOSTNAME)
                        .withSecret("Invalid secret"));

        boolean requestPassed = true;
        MS_LoggingEvent event = new MS_LoggingEvent().withTime(ZonedDateTime.now()).withType(MS_LoggingEventTypeEnum.INFO)
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
                new MS_LoggingRemoteServerProperties()
                        .withHost(TestData.HTTP_PREFIX + TestData.TESTING_SERVER_HOSTAME)
                        .withSecret(RandomStringUtils.random(MAX_SECRET_LENGTH + 1)))
                .removeAll();
    }

    private void assertThatEventsMatchOnesWeAddedOnInitialization(Map<Instant, MS_LoggingEvent> events) {
        events.forEach((time, event) -> Assertions.assertThat(loggedEvents.getEventList().contains(event))
                .withFailMessage("Received event that was not initially logged:" + event)
                .isTrue());
    }
}