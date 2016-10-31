package services;

import models.Message;
import org.hibernate.service.spi.ServiceException;
import rx.Observable;

import java.util.List;

/**
 * Created by alec on 10/20/16.
 */
public interface MessageService {

    Observable<List<Message>> getMessages() throws Exception;

    Observable<Message> subscribe(String topic, boolean subscribeToAll);

    void sendMessage(Message message) throws Exception;

    void stop() throws ServiceException;
}
