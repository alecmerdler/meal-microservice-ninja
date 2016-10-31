package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by alec on 10/26/16.
 */
public class Message {

    private String resourceName;
    private Long resourceId;
    private String action;
    private Map<String, Object> state;
    private Map<String, Object> changes;

    public Message() {

    }

    public Message(String resourceName, Long resourceId, String action) {
        this.resourceName = resourceName;
        this.resourceId = resourceId;
        this.action = action;
        this.state = new HashMap<>();
        this.changes = new HashMap<>();
    }

    public Message(String resourceName, Long resourceId, String action, Map<String, Object> state,
                   Map<String, Object> changes) {
        this.resourceName = resourceName;
        this.resourceId = resourceId;
        this.action = action;
        this.state = state;
        this.changes = changes;
    }

    @JsonIgnore
    public String getTopic() {
        return resourceName + "/" + resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
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
