package services;

import org.hibernate.service.spi.ServiceException;
import rx.Observable;

import java.util.List;
import java.util.Map;

/**
 * Created by alec on 10/20/16.
 */
public interface MessageService {

    Observable<List<Map<String, Object>>> getMessages() throws Exception;

    Observable<Map<String, Object>> subscribe(String topic);

    void sendMessage(String topic, String action, Map<String, Object> state, Map<String, Object> changes) throws Exception;

    void stop() throws ServiceException;
}
