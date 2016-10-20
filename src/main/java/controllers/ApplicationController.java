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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.MealDao;
import dao.TagDao;
import models.Meal;
import ninja.Context;
import ninja.Result;
import ninja.exceptions.BadRequestException;
import ninja.params.Param;
import ninja.params.PathParam;
import rx.schedulers.Schedulers;
import services.MessageServiceMQTT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ninja.Results.json;


@Singleton
public class ApplicationController {

    private final String serviceUrl = "http://localhost:8080";
    private final ObjectMapper objectMapper;
    private final MessageServiceMQTT messageService;
    private final MealDao mealDao;
    private final TagDao tagDao;

    @Inject
    public ApplicationController(MealDao mealDao, TagDao tagDao) {
        this.objectMapper = new ObjectMapper();
        this.messageService = new MessageServiceMQTT();
        this.mealDao = mealDao;
        this.tagDao = tagDao;
    }

    public Result listMessages() {
        final List<Map<String, Object>> messages = new ArrayList<>();
        try {
            messageService.getMessages()
                    .subscribeOn(Schedulers.computation())
                    .subscribe((List<Map<String, Object>> newMessages) -> {
                        for (Map<String, Object> message : newMessages) {
                            messages.add(message);
                        }
                    });
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }

        return json().render(messages);
    }

    public Result sendMessage() {
        String topic = "test";
        Map<String, Object> message = new HashMap<>();
        message.put("from", "service-2");
        try {
            messageService.sendMessage(topic, message);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
        Map<String, Object> response = new HashMap<>();
        response.put("status", "message sent");

        return json().render(response);
    }

    public Result listMeals(@Param("tagId") Long id) {
        List<Meal> allMeals = new ArrayList<>();
        try {
            if (id != null) {
                allMeals = mealDao.findByTagId(id);
            }
            else {
                allMeals = mealDao.findAll();
            }
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }

        return json().render(allMeals);
    }

    public Result createMeal() {
        throw new BadRequestException("Route not implemented");
    }

    public Result retrieveMeal(@PathParam("id") Long id) {
        throw new BadRequestException("Route not implemented");
    }

    public Result updateMeal(@PathParam("id") Long id, Context context, Meal updatedMeal) {
        throw new BadRequestException("Route not implemented");
    }

    public Result destroyMeal(@PathParam("id") Long id) {
        throw new BadRequestException("Route not implemented");
    }
}
