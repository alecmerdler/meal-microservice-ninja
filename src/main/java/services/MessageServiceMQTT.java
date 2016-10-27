package services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.hibernate.service.spi.ServiceException;
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
    private Map<String, List<Subscriber>> subscribers = new HashMap<>();

    public MessageServiceMQTT() {
        this.persistence = new MemoryPersistence();
        try {
            this.client = new MqttClient(brokerUrl, clientId, persistence);
            this.client.setCallback(new SubscribeCallback());
            this.client.connect();
        } catch (MqttException me) {
            me.printStackTrace();
        }
    }

    @Override
    public Observable<List<Map<String, Object>>> getMessages() throws Exception {
        return Observable.just(messages);
    }

    @Override
    public Observable<Map<String, Object>> subscribe(String topic) {
        return Observable.create((subscriber) -> {
            try {
                client.subscribe(topic);
                addSubscriberToTopic(topic, subscriber);
            } catch (MqttException me) {
                me.printStackTrace();
            }
        });
    }

    @Override
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

    @Override
    public void shutdown() throws ServiceException {
        try {
            client.close();
        } catch (MqttException me) {
            throw new ServiceException(me.getMessage());
        }
    }

    private String formatMessage(Map<String, Object> message) throws JsonProcessingException {
        return objectMapper.writeValueAsString(message);
    }

    private void addSubscriberToTopic(String topic, Subscriber subscriber) {
        if (subscribers.containsKey(topic)) {
            subscribers.get(topic).add(subscriber);
        }
        else {
            List<Subscriber> newSubscriberList = new ArrayList<>();
            newSubscriberList.add(subscriber);
            subscribers.put(topic, newSubscriberList);
        }
    }

    private class SubscribeCallback implements MqttCallback {

        private final ObjectMapper objectMapper;

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
                for (Subscriber subscriber : subscribers.get(topic)) {
                    subscriber.onNext(messageMap);
                }
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
