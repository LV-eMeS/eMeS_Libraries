package lv.emes.libraries.tools.logging;

import lv.emes.libraries.tools.lists.MS_List;

/**
 * Logging repository mostly for example and testing purposes.
 * It holds all the information about events in in-memory lists.
 * <p>Public methods:
 * <ul>
 * <li>logEvent</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 */
public class MS_InMemoryLoggingRepository implements MS_LoggingRepository {

    private MS_List<MS_LoggingEvent> eventList = new MS_List<>();

    @Override
    public void logEvent(MS_LoggingEvent event) {
        eventList.add(event);
    }

    public MS_List<MS_LoggingEvent> getEventList() {
        return eventList;
    }
}
