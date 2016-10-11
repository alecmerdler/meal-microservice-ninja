package services;

import com.google.inject.Inject;
import models.Meal;
import org.hibernate.service.spi.ServiceException;

import java.util.List;
import java.util.Optional;

/**
 * Created by alec on 10/11/16.
 */
public class MealServiceImpl implements MealService {

    @Inject
    public MealServiceImpl() {

    }

    public List<Meal> listAllMeals() {
        return null;
    }

    public Optional<Meal> listMealsByTagName(String tagName) throws ServiceException {
        return null;
    }
}
