package services;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.Message;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.junit.Before;
import org.junit.Test;
import rx.Subscription;

import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by alec on 10/31/16.
 */
public class MessageServiceMQTTTest {

    MessageServiceMQTT messageServiceMQTT;

    // Mocks
    IMqttClient clientMock;

    ObjectMapper objectMapper;

    @Before
    public void beforeEach() {
        clientMock = mock(IMqttClient.class);
        messageServiceMQTT = new MessageServiceMQTT(clientMock);
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testGetMessagesNoneExist() {
        try {
            messageServiceMQTT.getMessages()
                    .subscribe((List<Message> messages) -> {
                        assertEquals(0, messages.size());
                    });
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testSubscribeInvalidTopic() {
        try {
            messageServiceMQTT.subscribe(null, true)
                    .subscribe((Message message) -> {
                        fail();
                    });
            fail("Should throw exception");
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof NullPointerException);
        }
    }

    @Test
    public void testSubscribeValidTopic() {
        String topic = "users";
        try {
            Subscription subscription = messageServiceMQTT.subscribe(topic, false)
                    .subscribe((Message message) -> {});

            assertFalse(subscription.isUnsubscribed());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testSubscribeNotToAll() {
        String topic = "users";
        try {
            doNothing().when(clientMock).subscribe(topic);
            Subscription subscription = messageServiceMQTT.subscribe(topic, false)
                    .subscribe((Message message) -> {});

            assertFalse(subscription.isUnsubscribed());
            verify(clientMock).subscribe(topic);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testSubscribeToAll() {
        String topic = "users";
        try {
            doNothing().when(clientMock).subscribe(topic.concat("/+"));
            Subscription subscription = messageServiceMQTT.subscribe(topic, true)
                    .subscribe((Message message) -> {});

            assertFalse(subscription.isUnsubscribed());
            verify(clientMock).subscribe(topic.concat("/+"));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testPublishInvalidMessage() {
        try {
            messageServiceMQTT.publish(null);
            fail("Should throw exception");
        } catch (Exception e) {
            assertTrue(e instanceof NullPointerException);
        }
    }

    @Test
    public void testPublishValidMessage() {
        Message message = new Message("users", new Long(3), "create");
        try {
            messageServiceMQTT.publish(message);

            verify(clientMock).publish(eq(message.getTopic()), any());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testPublishClientDisconnected() {
        reset(clientMock);
        Message message = new Message("users", new Long(3), "create");
        try {
            messageServiceMQTT.publish(message);

            verify(clientMock).isConnected();
            verify(clientMock).connect();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testPublishClientConnected() {
        reset(clientMock);
        doReturn(true).when(clientMock).isConnected();
        Message message = new Message("users", new Long(3), "create");
        try {
            messageServiceMQTT.publish(message);

            verify(clientMock).isConnected();
            verify(clientMock, never()).connect();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}