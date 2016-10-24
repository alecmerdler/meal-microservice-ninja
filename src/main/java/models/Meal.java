package models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alec on 10/11/16.
 */
@Entity
public class Meal extends Model {

    @Column(nullable = false)
    private String mealName;

    @Column(nullable = false)
    private String chefName;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name="meal_tag", joinColumns = @JoinColumn(name = "meal_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags;

    public Meal() {
        this.mealName = "";
        this.chefName = "";
        this.tags = new ArrayList<>();
    }

    public Meal(String mealName) {
        this.mealName = mealName;
        this.chefName = "";
        this.tags = new ArrayList<>();
    }

    public Meal(String mealName, String chefName) {
        this.mealName = mealName;
        this.chefName = chefName;
        this.tags = new ArrayList<>();
    }

    public Meal(String mealName, String chefName, List<Tag> tags) {
        this.mealName = mealName;
        this.chefName = chefName;
        this.tags = tags;
    }

    public Meal(String mealName, String chefName, List<Tag> tags, Long id) {
        this.mealName = mealName;
        this.chefName = chefName;
        this.tags = tags;
        this.id = id;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public String getChefName() {
        return this.chefName;
    }

    public void setChefName(String chefName) {
        this.chefName = chefName;
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
