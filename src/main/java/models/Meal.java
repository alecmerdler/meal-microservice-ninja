package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Set;

/**
 * Created by alec on 10/11/16.
 */
@Entity
public class Meal extends Model {

    @Column
    private String mealName;

    @OneToMany
    private Set<Tag> tags;

    public Meal() {

    }

    public Meal(String mealName, Set<Tag> tags) {
        this.mealName = mealName;
        this.tags = tags;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

}
