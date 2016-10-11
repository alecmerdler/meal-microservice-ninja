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
import org.junit.Before;
import org.junit.Test;
import utils.UnirestObjectMapper;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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

    @Test
    public void testRoot() {
        try {
            HttpResponse<JsonNode> response = Unirest.get(getServerAddress())
                    .asJson();

            assertEquals(200, response.getStatus());
            assertEquals("service-2", response.getBody().getObject().get("name"));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testHealthcheck() {
        try {
            HttpResponse<JsonNode> response = Unirest.get(getServerAddress())
                    .asJson();

            assertEquals(200, response.getStatus());
            assertEquals("running", response.getBody().getObject().get("status"));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testListMealsSomeExist() {
        Tag tag = new Tag("Fruit");
        Meal firstMeal = new Meal("Banana");
        Meal secondMeal = new Meal("Meat");
        mealDao.create(firstMeal);
        mealDao.create(secondMeal);
        tag.addMeal(firstMeal);
        Tag persistedTag = tagDao.create(tag);

        try {
            HttpResponse<JsonNode> response = Unirest.get(mealsUrl + "?tagId=" + persistedTag.getId())
                    .asJson();
            List<Meal> mealsWithTag = objectMapper.readValue(response.getBody().toString(), new TypeReference<List<Meal>>(){});

            assertEquals(200, response.getStatus());
            assertEquals(1, mealsWithTag.size());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
