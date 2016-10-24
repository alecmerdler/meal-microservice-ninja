package services;

import com.google.inject.Inject;
import dao.MealDao;
import models.Meal;
import org.hibernate.service.spi.ServiceException;

import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Optional;

/**
 * Created by alec on 10/11/16.
 */
public class MealServiceImpl implements MealService {

    private final MealDao mealDao;

    @Inject
    public MealServiceImpl(MealDao mealDao) {
        this.mealDao = mealDao;
    }

    public List<Meal> listAllMeals() throws ServiceException {
        List<Meal> meals;
        try {
            meals = mealDao.findAll();
        } catch (PersistenceException pe) {
            throw new ServiceException(pe.getMessage());
        }

        return meals;
    }

    public List<Meal> listMealsByTagId(Long tagId) throws ServiceException {
        List<Meal> mealsWithTag;
        try {
            mealsWithTag = mealDao.findByTagId(tagId);
        } catch (PersistenceException pe) {
            throw new ServiceException(pe.getMessage());
        }

        return mealsWithTag;
    }

    public Optional<Meal> createMeal(Meal meal) throws ServiceException {
        return null;
    }

    public Optional<Meal> retrieveMealById(Long id) throws ServiceException {
        return null;
    }

    public Optional<Meal> updateMeal(Meal meal) throws ServiceException {
        return null;
    }

    public boolean destroyMeal(Meal meal) throws ServiceException {
        if (meal == null) {
            throw new ServiceException("Meal should not be null");
        }
        boolean status = false;
        try {
            status = mealDao.destroy(meal);
        } catch (PersistenceException pe) {
            throw new ServiceException(pe.getMessage());
        }

        return status;
    }
}
