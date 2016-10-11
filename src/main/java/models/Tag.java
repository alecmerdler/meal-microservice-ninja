package models;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Created by alec on 10/11/16.
 */
@Entity
public class Tag extends Model {

    @Column
    private String tagName;

    public Tag() {

    }

    public Tag(String tagName) {
        this.tagName = tagName;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
