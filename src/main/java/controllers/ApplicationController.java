package controllers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.MealDao;
import dao.TagDao;
import models.Meal;
import models.Message;
import ninja.Context;
import ninja.Result;
import ninja.exceptions.BadRequestException;
import ninja.params.Param;
import ninja.params.PathParam;
import org.hibernate.service.spi.ServiceException;
import rx.schedulers.Schedulers;
import services.MealService;
import services.MessageService;

import java.util.*;

import static ninja.Results.json;

@Singleton
public class ApplicationController {

    private final MessageService messageService;
    private final MealService mealService;
    private final MealDao mealDao;
    private final TagDao tagDao;

    @Inject
    public ApplicationController(MealDao mealDao, TagDao tagDao, MessageService messageService, MealService mealService) {
        this.messageService = messageService;
        this.mealService = mealService;
        this.mealDao = mealDao;
        this.tagDao = tagDao;
    }

    public Result initialize(Context context, Map<String, Object> options) {
        // TODO: Move to initialization service
        try {
            messageService.subscribe("users", true)
                    .subscribeOn(Schedulers.newThread())
                    .subscribe((Message message) -> {
                        switch (message.getAction()) {
                            case "update":
                                rx.Observable.from(mealService.listMealsByChefId(message.getResourceId()))
                                        .subscribe((Meal meal) -> {
                                            meal.setChefId((Long.valueOf((int) message.getChanges().get("id"))));
                                            mealService.updateMeal(meal);
                                        });
                            case "destroy":
                                rx.Observable.from(mealService.listMealsByChefId(message.getResourceId()))
                                        .subscribe(mealService::destroyMeal);
                        }
                    });
        } catch (ServiceException se) {
            System.out.println(se.getMessage());
        }
        Map<String, Object> response = new HashMap<>();
        response.put("status", "initialized");

        return json()
                .status(200)
                .render(response);
    }

    public Result listMessages() {
        final List<Message> messages = new ArrayList<>();
        try {
            messageService.getMessages()
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(messages::addAll);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }

        return json()
                .status(200)
                .render(messages);
    }

    public Result listMeals(@Param("tagId") Long tagId,
                            @Param("chefId") Long chefId,
                            @Param("recommendedFor") Long userId,
                            @Param("zipcode") String zipcode,
                            @Param("range") int range,
                            @Param("fields") String[] fields) {
        List<Meal> allMeals = new ArrayList<>();
        try {
            if (tagId != null) {
                allMeals = mealDao.findByTagId(tagId);
            }
            else if (chefId != null) {
                allMeals = mealDao.findByChefId(chefId);
            }
            else {
                allMeals = mealDao.findAll();
            }
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }

        return json()
                .status(200)
                .render(allMeals);
    }

    public Result createMeal(Context context, Meal meal) {
        Meal createdMeal = null;
        try {
            Optional<Meal> mealOptional = mealService.createMeal(meal);
            if (mealOptional.isPresent()) {
                createdMeal = mealOptional.get();
                messageService.publish(new Message("meals", createdMeal.getId(), "create"));
            }
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }

        return json()
                .status(201)
                .render(createdMeal);
    }

    public Result retrieveMeal(@PathParam("id") Long id,
                               @Param("fields") String[] fields) {
        Result response = json()
                .status(404)
                .render(new HashMap<>());
        try {
            Optional<Meal> mealOptional = mealService.retrieveMealById(id);
            if (mealOptional.isPresent()) {
                response = json()
                        .status(200)
                        .render(mealOptional.get());
            }
        } catch (ServiceException se) {
            throw new BadRequestException(se.getMessage());
        }

        return response;
    }

    public Result updateMeal(@PathParam("id") Long id, Context context, Meal meal) {
        Meal updatedMeal = null;
        try {
            Optional<Meal> mealOptional = mealService.updateMeal(meal);
            if (mealOptional.isPresent()) {
                updatedMeal = mealOptional.get();
                messageService.publish(new Message("meals", id, "update", meal.mapProperties(), meal.mapProperties()));
            }
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }

        return json()
                .status(200)
                .render(updatedMeal);
    }

    public Result destroyMeal(@PathParam("id") Long id) {
        Result response = json()
                .status(404)
                .render(new HashMap<>());
        try {
            Optional<Meal> mealOptional = mealService.retrieveMealById(id);
            if (mealOptional.isPresent()) {
                boolean status = mealService.destroyMeal(mealOptional.get());
                messageService.publish(new Message("meals", id, "destroy", new HashMap<>(), new HashMap<>()));
                response = json()
                        .status(204)
                        .render(new HashMap<>());
            }
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }

        return response;
    }

    // TODO: Define request body
    public Result purchaseMeal(@PathParam("id") Long id, Context context, Map<String, Object> requestBody) {
        Map<String, Object> responseBody = new HashMap<>();
        Result response = json()
                .status(404)
                .render(responseBody);
        try {
            Optional<Meal> mealOptional = mealService.retrieveMealById(id);
            if (mealOptional.isPresent()) {

                Map<String, Object> messageState = new HashMap<>();
                messageState.put("userId", requestBody.get("userId"));
                messageService.request("purchases", new Message("meals", id, "purchase", messageState, mealOptional.get().mapProperties()))
                        .subscribeOn(Schedulers.newThread())
                        .subscribe((Message message) -> {
                            if (message.getAction().equals("create") && Long.valueOf((int) message.getState().get("mealId")).equals(id)) {
                                responseBody.put("purchaseId", message.getResourceId());
                                responseBody.put("status", "purchase created");
                            }
                        });
                // FIXME: Wait until message response received to send HTTP response
                Thread.sleep(2000);
                response = json()
                        .status(200)
                        .render(responseBody);
            }
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }

        return response;
    }
}
