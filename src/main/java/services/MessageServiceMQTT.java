package services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    @Inject
    public MessageServiceMQTT(IMqttClient client, MemoryPersistence persistence) {
        this.client = client;
        this.persistence = persistence;
    }

    public MessageServiceMQTT() {
        this.persistence = new MemoryPersistence();
        final String topic = "test";
        try {
            this.client = new MqttClient(brokerUrl, clientId, persistence);
            subscribe(topic);
        } catch (MqttException me) {
            me.printStackTrace();
        }
    }

    public Observable<List<Map<String, Object>>> getMessages() throws Exception {
        System.out.println(messages);

        return Observable.just(messages);
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

    private void subscribe(String topic) {
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

        SubscribeCallback() {
            this.objectMapper = new ObjectMapper();
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
