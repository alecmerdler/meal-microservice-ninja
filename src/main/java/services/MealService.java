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

    public Optional<Meal> listMealsByTagName(String tagName) throws ServiceException;
}
