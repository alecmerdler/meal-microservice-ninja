package integration;

import com.google.inject.Injector;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import dao.MealDao;
import dao.TagDao;
import models.Meal;
import models.Message;
import ninja.NinjaTest;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import services.MessageService;
import utils.UnirestObjectMapper;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by alec on 10/25/16.
 */
public class MealIntegrationTest extends NinjaTest {

    ObjectMapper objectMapper;
    MessageService messageService;
    MealDao mealDao;
    TagDao tagDao;
    String apiUrl;
    String mealsUrl;
    String tagsUrl;
    String initializeUrl;

    @Before
    public void beforeEach() {
        Injector injector = getInjector();
        objectMapper = new ObjectMapper();
        messageService = injector.getInstance(MessageService.class);
        mealDao = injector.getInstance(MealDao.class);
        tagDao = injector.getInstance(TagDao.class);
        apiUrl = getServerAddress() + "api/v1";
        mealsUrl = apiUrl + "/meals";
        tagsUrl = apiUrl + "/tags";
        initializeUrl = getServerAddress() + "/initialize";

        Unirest.setObjectMapper(new UnirestObjectMapper());
    }

    @After
    public void afterEach() {
        messageService = getInjector().getInstance(MessageService.class);
    }

    @Test
    public void testUserDestroyedMessageDestroysAssociatedMeals() {
        Long chefId = new Long(32);
        mealDao.create(new Meal("Bananas", chefId));
        mealDao.create(new Meal("Steak", chefId));
        mealDao.create(new Meal("Ice Cream", new Long(76)));
        try {
            Unirest.post(initializeUrl).asJson();
            messageService.publish(new Message("users", chefId, "destroy"));
            Thread.sleep(2000);
            HttpResponse<JsonNode> response = Unirest.get(mealsUrl)
                    .asJson();
            List<Meal> meals = objectMapper.readValue(response.getBody().toString(), new TypeReference<List<Meal>>(){});

            assertEquals(200, response.getStatus());
            assertEquals(1, mealDao.findAll().size());
            assertEquals(1, meals.size());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testUserDestroyedMessageNoAssociatedMeals() {
        Long chefId = new Long(2);
        mealDao.create(new Meal("Bananas", new Long(72)));
        mealDao.create(new Meal("Steak", new Long(71)));
        mealDao.create(new Meal("Ice Cream", new Long(76)));
        try {
            Unirest.post(initializeUrl).asJson();
            messageService.publish(new Message("users", chefId, "destroy"));
            Thread.sleep(2000);
            HttpResponse<JsonNode> response = Unirest.get(mealsUrl)
                    .asJson();
            List<Meal> meals = objectMapper.readValue(response.getBody().toString(), new TypeReference<List<Meal>>(){});

            assertEquals(200, response.getStatus());
            assertEquals(3, mealDao.findAll().size());
            assertEquals(3, meals.size());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
