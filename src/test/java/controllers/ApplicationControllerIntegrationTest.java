/**
 * Copyright (C) 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers;


import com.google.inject.Injector;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import dao.MealDao;
import dao.TagDao;
import models.Meal;
import models.Tag;
import ninja.NinjaTest;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import services.MessageService;
import utils.UnirestObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ApplicationControllerIntegrationTest extends NinjaTest {

    ObjectMapper objectMapper;
    MealDao mealDao;
    TagDao tagDao;
    String apiUrl;
    String mealsUrl;
    String tagsUrl;

    @Before
    public void beforeEach() {
        Injector injector = getInjector();
        objectMapper = new ObjectMapper();
        mealDao = injector.getInstance(MealDao.class);
        tagDao = injector.getInstance(TagDao.class);
        apiUrl = getServerAddress() + "/api/v1";
        mealsUrl = apiUrl + "/meals";
        tagsUrl = apiUrl + "/tags";

        Unirest.setObjectMapper(new UnirestObjectMapper());
    }

    @After
    public void afterEach() {
        Injector injector = getInjector();
        injector.getInstance(MessageService.class).stop();
    }

    @Test
    public void testRoot() {
        try {
            HttpResponse<JsonNode> response = Unirest.get(getServerAddress())
                    .asJson();

            assertEquals(200, response.getStatus());
            assertEquals("meal-microservice-ninja", response.getBody().getObject().get("name"));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testHealthcheck() {
        try {
            HttpResponse<JsonNode> response = Unirest.get(getServerAddress() + "/healthcheck")
                    .asJson();

            assertEquals(200, response.getStatus());
            assertEquals("running", response.getBody().getObject().get("status"));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testInitialize() {
        try {
            HttpResponse<JsonNode> response = Unirest.post(getServerAddress() + "/initialize")
                    .asJson();

            assertEquals(200, response.getStatus());
            assertEquals("initialized", response.getBody().getObject().get("status"));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testListMealsSomeExist() {
        List<Meal> newMeals = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Meal newMeal = mealDao.create(new Meal("Banana"));
            newMeals.add(newMeal);
        }
        try {
            HttpResponse<JsonNode> response = Unirest.get(mealsUrl)
                    .asJson();
            List<Meal> allMeals = objectMapper.readValue(response.getBody().toString(), new TypeReference<List<Meal>>(){});

            assertEquals(200, response.getStatus());
            assertEquals(newMeals.size(), allMeals.size());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testListMealsByTagOneExists() {
        Tag fruitTag = new Tag("Fruit");
        Meal createdFirstMeal = mealDao.create(new Meal("Banana").addTag(fruitTag));
        mealDao.create(new Meal("Steak"));
        try {
            HttpResponse<JsonNode> response = Unirest.get(mealsUrl + "?tagId=" + createdFirstMeal.getTags().get(0).getId())
                    .asJson();
            List<Meal> mealsWithTag = objectMapper.readValue(response.getBody().toString(), new TypeReference<List<Meal>>(){});

            assertEquals(200, response.getStatus());
            assertEquals(1, mealsWithTag.size());
            assertEquals(fruitTag.getTagName(), mealsWithTag.get(0).getTags().get(0).getTagName());
            assertEquals(1, tagDao.findAll().size());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testListMealsByTagNoneExist() {
        Tag tag = tagDao.create(new Tag("Meat"));
        mealDao.create(new Meal("Banana"));
        try {
            HttpResponse<JsonNode> response = Unirest.get(mealsUrl + "?tagId=" + tag.getId())
                    .asJson();
            List<Meal> mealsWithTag = objectMapper.readValue(response.getBody().toString(), new TypeReference<List<Meal>>(){});

            assertEquals(200 ,response.getStatus());
            assertEquals(0, mealsWithTag.size());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testListMealsByChefIdNoneExist() {
        Long chefId = new Long(76);
        mealDao.create(new Meal("Bananas"));
        try {
            HttpResponse<JsonNode> response = Unirest.get(mealsUrl + "?chefId=" + chefId)
                    .asJson();
            List<Meal> mealsWithChefId = objectMapper.readValue(response.getBody().toString(), new TypeReference<List<Meal>>(){});

            assertEquals(200, response.getStatus());
            assertEquals(0, mealsWithChefId.size());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testListMealsByChefIdSomeExist() {
        Long chefId = new Long(77);
        mealDao.create(new Meal("Bananas", chefId));
        mealDao.create(new Meal("Steak", chefId));
        mealDao.create(new Meal("Raisins"));
        try {
            HttpResponse<JsonNode> response = Unirest.get(mealsUrl + "?chefId=" + chefId)
                    .asJson();
            List<Meal> mealsWithChefId = objectMapper.readValue(response.getBody().toString(), new TypeReference<List<Meal>>(){});

            assertEquals(200, response.getStatus());
            assertEquals(2, mealsWithChefId.size());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testCreateMealValid() {
        Meal meal = new Meal("Banana", new Long(1), new ArrayList<>());
        try {
            HttpResponse<JsonNode> response = Unirest.post(mealsUrl)
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .body(meal)
                    .asJson();
            Meal createdMeal = objectMapper.readValue(response.getBody().toString(), Meal.class);

            assertEquals(201, response.getStatus());
            assertEquals(meal.getMealName(), createdMeal.getMealName());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testCreateMealInvalid() {
        try {
            HttpResponse<JsonNode> response = Unirest.post(mealsUrl)
                    .asJson();

            assertEquals(400, response.getStatus());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testRetrieveMealDoesNotExist() {
        int id = 32;
        try {
            HttpResponse<JsonNode> response = Unirest.get(mealsUrl + "/" + id)
                    .asJson();
            Map<String, Object> responseBody = objectMapper.readValue(response.getBody().toString(), new TypeReference<Map>(){});

            assertEquals(404, response.getStatus());
            assertTrue(responseBody.isEmpty());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testRetrieveMealExists() {
        Meal meal = mealDao.create(new Meal("Bananas", new Long(21)));
        try {
            HttpResponse<JsonNode> response = Unirest.get(mealsUrl + "/" + meal.getId())
                    .asJson();
            Meal responseMeal = objectMapper.readValue(response.getBody().toString(), Meal.class);

            assertEquals(200, response.getStatus());
            assertEquals(meal.getId(), responseMeal.getId());
            assertEquals(meal.getMealName(), responseMeal.getMealName());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testUpdateMealExists() {
        Meal meal = mealDao.create(new Meal("Bananas", new Long(21)));
        String newMealName = "Plantains";
        meal.setMealName(newMealName);
        try {
            HttpResponse<JsonNode> response = Unirest.put(mealsUrl + "/" + meal.getId())
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .body(meal)
                    .asJson();
            Meal responseMeal = objectMapper.readValue(response.getBody().toString(), Meal.class);

            assertEquals(200, response.getStatus());
            assertEquals(meal.getId(), responseMeal.getId());
            assertEquals(newMealName, responseMeal.getMealName());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testUpdateMealDoesNotExist() {
        Meal meal = new Meal("Steak", new Long(81));
        String newMealName = "Plantains";
        meal.setMealName(newMealName);
        try {
            HttpResponse<JsonNode> response = Unirest.put(mealsUrl + "/" + meal.getId())
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .body(meal)
                    .asJson();
            Map<String, Object> responseBody = objectMapper.readValue(response.getBody().toString(), new TypeReference<Map>(){});

            assertEquals(400, response.getStatus());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testDestroyMealDoesNotExist() {
        Meal meal = new Meal("Steak", new Long(32), null, new Long(2));
        try {
            HttpResponse<JsonNode> response = Unirest.delete(mealsUrl + "/" + meal.getId())
                    .asJson();

            assertEquals(404, response.getStatus());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testDestroyMealExists() {
        Meal meal = new Meal("Bananas", new Long(43));
        mealDao.create(meal);
        try {
            HttpResponse<JsonNode> response = Unirest.delete(mealsUrl + "/" + meal.getId())
                    .asJson();

            assertEquals(204, response.getStatus());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testPurchaseMealDoesNotExist() {
        Long mealId = new Long(23);
        try {
            HttpResponse<JsonNode> response = Unirest.post(mealsUrl + "/" + mealId + "/purchase")
                    .asJson();

            assertEquals(404, response.getStatus());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testPurchaseMealExists() {
        Meal meal = new Meal("Steak");
        mealDao.create(meal);
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("userId", new Long(29));
        try {
            HttpResponse<JsonNode> response = Unirest.post(mealsUrl + "/" + meal.getId() + "/purchase")
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .body(requestBody)
                    .asJson();
            Map<String, Object> responseBody = objectMapper.readValue(response.getBody().toString(), new TypeReference<Map>(){});

            assertEquals(200, response.getStatus());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
