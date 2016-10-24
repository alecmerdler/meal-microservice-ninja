package services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import rx.Observable;
import rx.Subscriber;

import java.util.*;

/**
 * Created by alec on 10/18/16.
 */
public class MessageServiceMQTT implements MessageService {

    // TODO: Move to configuration file
    private final String brokerUrl = "tcp://52.25.184.170:1884";
    private final String clientId = UUID.randomUUID().toString();
    private IMqttClient client;
    private final MemoryPersistence persistence;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<Map<String, Object>> messages = new ArrayList<>();

    public MessageServiceMQTT() {
        this.persistence = new MemoryPersistence();
        final String topic = "test";
        try {
            this.client = new MqttClient(brokerUrl, clientId, persistence);
            subscribeToTopic(topic);
        } catch (MqttException me) {
            me.printStackTrace();
        }
    }

    public Observable<List<Map<String, Object>>> getMessages() throws Exception {
        return Observable.just(messages);
    }

    public Observable<Map<String, Object>> subscribe(String topic) {
        // TODO: Subscribe to topic and call Rx Subscriber in callback
        return Observable.just(new HashMap<String, Object>());
    }

    public void sendMessage(String topic, Map<String, Object> message) throws Exception {
        try {
            if (!client.isConnected()) {
                client.connect();
            }
            client.publish(topic, new MqttMessage(formatMessage(message).getBytes()));
        } catch (MqttException me) {
            me.printStackTrace();
        }
    }

    private void subscribeToTopic(String topic) {
        try {
            if (!client.isConnected()) {
                client.setCallback(new SubscribeCallback());
                client.connect();
            }
            client.subscribe(topic);
        } catch (MqttException me) {
            me.printStackTrace();
        }
    }

    private String formatMessage(Map<String, Object> message) throws JsonProcessingException {
        return objectMapper.writeValueAsString(message);
    }

    private class SubscribeCallback implements MqttCallback {

        private ObjectMapper objectMapper;
        private Subscriber subscriber;

        SubscribeCallback() {
            this.objectMapper = new ObjectMapper();
        }

        SubscribeCallback(Subscriber subscriber) {
            this.objectMapper = new ObjectMapper();
            this.subscriber = subscriber;
        }

        @Override
        public void connectionLost(Throwable cause) {
            System.out.println("Connection to MQTT broker lost");
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) {
            try {
                Map<String, Object> messageMap = objectMapper.readValue(message.getPayload(), new TypeReference<Map<String, Object>>(){});
                messages.add(messageMap);
                System.out.println(messages);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    }


}
