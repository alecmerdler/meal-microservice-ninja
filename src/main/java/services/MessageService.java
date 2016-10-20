package services;

import rx.Observable;

import java.util.List;
import java.util.Map;

/**
 * Created by alec on 10/20/16.
 */
public interface MessageService {

    Observable<List<Map<String, Object>>> getMessages() throws Exception;

    void sendMessage(String topic, Map<String, Object> message) throws Exception;
}