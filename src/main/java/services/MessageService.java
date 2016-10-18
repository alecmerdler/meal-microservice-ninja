package services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import rx.Observable;
import rx.Subscriber;

import java.util.Map;
import java.util.UUID;

/**
 * Created by alec on 10/18/16.
 */
public class MessageService {

    private final String brokerUrl = "tcp://52.25.184.170:1884";
    private final String sensorDataTopic = "sensorData";
    private final String clientId = UUID.randomUUID().toString();
    private IMqttClient client;
    private MemoryPersistence persistence;

    @Inject
    public MessageService(IMqttClient client, MemoryPersistence persistence) {
        this.client = client;
        this.persistence = persistence;
    }

    public MessageService() {
        this.persistence = new MemoryPersistence();
        try {
            this.client = new MqttClient(brokerUrl, clientId, persistence);
        } catch (MqttException me) {
            me.printStackTrace();
        }
    }

    public Observable<Map<String, Object>> getMessages() {
        return Observable.create((subscriber) -> {
            try {
                client.setCallback(new SubscribeCallback(subscriber));
                client.connect();
                client.subscribe(sensorDataTopic);
            } catch (MqttException me) {
                me.printStackTrace();
            }
        });
    }

    private class SubscribeCallback implements MqttCallback {

        private Subscriber<? super Map<String, Object>> subscriber;
        private ObjectMapper objectMapper;

        SubscribeCallback(Subscriber<? super Map<String, Object>> subscriber) {
            this.subscriber = subscriber;
            this.objectMapper = new ObjectMapper();
        }

        @Override
        public void connectionLost(Throwable cause) {

        }

        @Override
        public void messageArrived(String topic, MqttMessage message) {
            try {
                byte[] bytes = message.getPayload();
                Map<String, Object> messageMap = objectMapper.readValue(message.getPayload(), new TypeReference<Map<String, Object>>(){});
                subscriber.onNext(messageMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    }


}
