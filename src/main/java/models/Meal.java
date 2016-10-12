package models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alec on 10/11/16.
 */
@Entity
public class Meal extends Model {

    @Column
    private String mealName;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name="meal_tag", joinColumns = @JoinColumn(name = "meal_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags;

    public Meal() {
        this.mealName = "";
        this.tags = new ArrayList<>();
    }

    public Meal(String mealName) {
        this.mealName = mealName;
        this.tags = new ArrayList<>();
    }

    public Meal(String mealName, List<Tag> tags) {
        this.mealName = mealName;
        this.tags = tags;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public Meal addTag(Tag tag) {
        tags.add(tag);

        return this;
    }

}
