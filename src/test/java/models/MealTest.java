package models;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by alec on 11/1/16.
 */
public class MealTest {

    Meal meal;

    @Before
    public void beforeEach() {

    }

    @Test
    public void testMapProperties() {
        meal = new Meal("Bananas", new Long(32), new ArrayList<>(), new Long(89));
        try {
            Map<String, Object> properties = meal.mapProperties();

            assertEquals(meal.getMealName(), properties.get("mealName"));
            assertEquals(meal.getId(), properties.get("id"));
            assertEquals(meal.getChefId(), properties.get("chefId"));
            assertEquals(meal.getTags(), properties.get("tags"));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}