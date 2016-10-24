package services;

import models.Meal;
import org.hibernate.service.spi.ServiceException;

import java.util.List;
import java.util.Optional;

/**
 * Created by alec on 10/11/16.
 */
public interface MealService {

    List<Meal> listAllMeals() throws ServiceException;

    List<Meal> listMealsByTagId(Long tagId) throws ServiceException;

    List<Meal> listMealsByChefId(Long chefId) throws ServiceException;

    Optional<Meal> createMeal(Meal meal) throws ServiceException;

    Optional<Meal> retrieveMealById(Long id) throws ServiceException;

    Optional<Meal> updateMeal(Meal meal) throws ServiceException;

    boolean destroyMeal(Meal meal) throws ServiceException;
}
