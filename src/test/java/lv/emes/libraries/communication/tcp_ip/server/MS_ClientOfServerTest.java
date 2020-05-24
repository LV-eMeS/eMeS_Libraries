package lv.emes.libraries.communication.tcp_ip.server;

import org.assertj.core.data.Offset;
import org.junit.Before;
import org.junit.Test;
import org.threeten.bp.ZonedDateTime;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MS_ClientOfServerTest {

    private Socket socketMock;
    private InputStream isMock;
    private OutputStream osMock;

    @Before
    public void setUp() throws IOException {
        socketMock = mock(Socket.class);
        isMock = mock(InputStream.class);
        osMock = mock(OutputStream.class);
        when(socketMock.getInputStream()).thenReturn(isMock);
        when(socketMock.getOutputStream()).thenReturn(osMock);
    }

    @Test
    public void testSetClientTimeDiffAheadFromTheServer() {
        MS_ClientOfServer client = new MS_ClientOfServer(1, socketMock);
        // First set it ahead from server
        client.setClientTime(ZonedDateTime.now().plusSeconds(2));
        assertThat(client.getTimeDiffFromServer()).isLessThan(0).isCloseTo(-2000000000L, Offset.offset(100000000L));
    }

    @Test
    public void testSetClientTimeDiffBehindFromTheServer() {
        MS_ClientOfServer client = new MS_ClientOfServer(1, socketMock);
        // Then set it behind from server
        client.setClientTime(ZonedDateTime.now().minusSeconds(2));
        assertThat(client.getTimeDiffFromServer()).isGreaterThan(0).isCloseTo(2000000000L, Offset.offset(100000000L));
    }

    @Test
    public void testSetClientTimeTwiceDiffGreaterThan1SecFirstSetValueRemains() {
        MS_ClientOfServer client = new MS_ClientOfServer(1, socketMock);
        client.setClientTime(ZonedDateTime.now().plusSeconds(2));
        client.setClientTime(ZonedDateTime.now().plusSeconds(5)); // Second diff is even greater, ignore it!
        assertThat(client.getTimeDiffFromServer()).isLessThan(0).isCloseTo(-2_000_000_000L, Offset.offset(1000_000_000L));
    }

    @Test
    public void testSetClientTimeTwiceDiffGreaterThan1SecAndSecondDiffValueWasSmaller() {
        MS_ClientOfServer client = new MS_ClientOfServer(1, socketMock);
        client.setClientTime(ZonedDateTime.now().plusSeconds(2));
        client.setClientTime(ZonedDateTime.now().plusNanos(500_000_000L)); // 1,5 seconds is already smaller diff, then it's taken
        assertThat(client.getTimeDiffFromServer()).isLessThan(0).isCloseTo(-500_000_000L, Offset.offset(1000_000_000L));
    }

    @Test
    public void testSetClientTimeTwiceDiffLessThan1SecSoAvgValueIsTaken() {
        MS_ClientOfServer client = new MS_ClientOfServer(1, socketMock);
        client.setClientTime(ZonedDateTime.now().minusNanos(500_000_000L)); // minus half a second
        client.setClientTime(ZonedDateTime.now().minusNanos(700_000_000L)); // 1,5 seconds is already smaller diff, then it's taken
        assertThat(client.getTimeDiffFromServer()).isGreaterThan(0).isCloseTo(600_000_000L, Offset.offset(300_000_000L));
    }
}