package models;

import java.util.Map;

/**
 * Created by alec on 10/26/16.
 */
public class Message {

    private String topic;
    private String action;
    private Map<String, Object> state;
    private Map<String, Object> changes;

    public Message(String topic, String action, Map<String, Object> state, Map<String, Object> changes) {
        this.topic = topic;
        this.action = action;
        this.state = state;
        this.changes = changes;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Map<String, Object> getState() {
        return state;
    }

    public void setState(Map<String, Object> state) {
        this.state = state;
    }

    public Map<String, Object> getChanges() {
        return changes;
    }

    public void setChanges(Map<String, Object> changes) {
        this.changes = changes;
    }
}
