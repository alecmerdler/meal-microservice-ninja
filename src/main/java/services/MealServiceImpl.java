package services;

import com.google.inject.Inject;
import dao.MealDao;
import models.Meal;
import org.hibernate.service.spi.ServiceException;

import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

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

    public List<Meal> listMealsByChefId(Long chefId) throws ServiceException {
        List<Meal> mealsWithChefId;
        try {
            mealsWithChefId = mealDao.findByChefId(chefId);
        } catch (PersistenceException pe) {
            throw new ServiceException(pe.getMessage());
        }

        return mealsWithChefId;
    }

    public Optional<Meal> createMeal(Meal meal) throws ServiceException {
        final Meal createdMeal;
        if (meal == null) {
            throw new ServiceException("Meal should not be null");
        }
        try {
            createdMeal = mealDao.create(meal);
        } catch (PersistenceException pe) {
            throw new ServiceException(pe.getMessage());
        }

        return ofNullable(createdMeal);
    }

    public Optional<Meal> retrieveMealById(Long id) throws ServiceException {
        if (id == null) {
            throw new ServiceException("ID should not be null");
        }
        Meal meal = null;
        try {
            List<Meal> mealsWithId = mealDao.findById(id);
            if (mealsWithId.size() > 0) {
                meal = mealsWithId.get(0);
            }
        } catch (PersistenceException pe) {
            throw new PersistenceException(pe.getMessage());
        }

        return ofNullable(meal);
    }

    public Optional<Meal> updateMeal(Meal meal) throws ServiceException {
        final Meal updatedMeal;
        if (meal == null) {
            throw new ServiceException("Meal should not be null");
        }
        try {
            updatedMeal = mealDao.update(meal);
        } catch (PersistenceException pe) {
            throw new ServiceException(pe.getMessage());
        }

        return ofNullable(updatedMeal);
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
