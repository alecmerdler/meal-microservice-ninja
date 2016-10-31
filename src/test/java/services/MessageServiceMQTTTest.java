package services;

import models.Message;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.junit.Before;
import org.junit.Test;
import rx.observers.TestSubscriber;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Created by alec on 10/31/16.
 */
public class MessageServiceMQTTTest {

    MessageServiceMQTT messageServiceMQTT;

    // Mocks
    IMqttClient clientMock;

    @Before
    public void beforeEach() {
        clientMock = mock(IMqttClient.class);
        messageServiceMQTT = new MessageServiceMQTT(clientMock);
    }

    @Test
    public void testGetMessagesNoneExist() {
        TestSubscriber<List<Message>> testSubscriber = new TestSubscriber<>();
        try {
            messageServiceMQTT.getMessages()
                    .subscribe(testSubscriber);

            testSubscriber.assertNoErrors();
            assertEquals(0, testSubscriber.getOnNextEvents().get(0).size());
        } catch (Exception e) {

        }
    }

    @Test
    public void testGetMesssagesSomeExist() {

    }

    @Test
    public void testSubscribeInvalidTopic() {

    }

    @Test
    public void testSubscribeValidTopic() {

    }

    @Test
    public void testSubscribeNotToAll() {

    }

    @Test
    public void testSubscribeToAll() {

    }

    @Test
    public void testPublishInvalidTopic() {

    }

    @Test
    public void testPublishClientDisconnected() {

    }

    @Test
    public void testPublishClientConnected() {

    }
}