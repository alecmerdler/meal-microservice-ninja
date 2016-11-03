package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alec on 10/11/16.
 */
@Entity
public class Tag extends Model {

    @Column
    private String tagName;

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL,
                mappedBy = "tags")
    private List<Meal> meals;

    public Tag() {
        this.tagName = "";
        this.meals = new ArrayList<>();
    }

    public Tag(String tagName) {
        this.tagName = tagName;
        this.meals = new ArrayList<>();
    }

    public Tag(String tagName, List<Meal> meals) {
        this.tagName = tagName;
        this.meals = meals;
    }

    public Tag(String tagName, List<Meal> meals, Long id) {
        this.tagName = tagName;
        this.meals = meals;
        this.id = id;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public List<Meal> getMeals() {
        return meals;
    }

    public void setMeals(List<Meal> meals) {
        this.meals = meals;
    }

    public Tag addMeal(Meal meal) {
        meals.add(meal);

        return this;
    }
}
