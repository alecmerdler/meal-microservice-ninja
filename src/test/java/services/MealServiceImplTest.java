package services;

import dao.MealDao;
import models.Meal;
import models.Tag;
import org.hibernate.service.spi.ServiceException;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by alec on 10/12/16.
 */
public class MealServiceImplTest {

    MealServiceImpl mealService;

    // Mocks
    MealDao mealDaoMock;

    @Before
    public void beforeEach() {
        mealDaoMock = mock(MealDao.class);
    }

    @Test
    public void testListAllMealsNoMeals() {
        doReturn(new ArrayList<>()).when(mealDaoMock).findAll();
        mealService = new MealServiceImpl(mealDaoMock);

        assertEquals(0, mealService.listAllMeals().size());
        verify(mealDaoMock).findAll();
    }

    @Test
    public void testListAllMealsSomeMeals() {
        List<Meal> meals = new ArrayList<>();
        meals.add(new Meal("Banana"));
        meals.add(new Meal("Steak"));
        doReturn(meals).when(mealDaoMock).findAll();
        mealService = new MealServiceImpl(mealDaoMock);

        assertEquals(meals.size(), mealService.listAllMeals().size());
        verify(mealDaoMock).findAll();
    }

    @Test
    public void testListMealsByTagIdSomeMeals() {
        Tag tag = new Tag("Fruit", null, new Long(32));
        doReturn(new ArrayList<>()).when(mealDaoMock).findByTagId(tag.getId());
        mealService = new MealServiceImpl(mealDaoMock);

        assertEquals(0, mealService.listMealsByTagId(tag.getId()).size());
        verify(mealDaoMock).findByTagId(tag.getId());
    }

    @Test
    public void testListMealsByTagIdSomeExist() {
        Tag tag = new Tag("Fruit", null, new Long(43));
        List<Meal> mealsWithTag = new ArrayList<>();
        mealsWithTag.add(new Meal("Banana"));
        mealsWithTag.add(new Meal("Steak"));
        doReturn(mealsWithTag).when(mealDaoMock).findByTagId(tag.getId());
        mealService = new MealServiceImpl(mealDaoMock);

        assertEquals(mealsWithTag.size(), mealService.listMealsByTagId(tag.getId()).size());
        verify(mealDaoMock).findByTagId(tag.getId());
    }

    @Test
    public void testListMealsByChefIdNoMeals() {
        Long chefId = new Long(43);
        doReturn(new ArrayList<>()).when(mealDaoMock).findByChefId(chefId);
        mealService = new MealServiceImpl(mealDaoMock);

        assertEquals(0, mealService.listMealsByChefId(chefId).size());
        verify(mealDaoMock).findByChefId(chefId);
    }

    @Test
    public void testListMealsByChefIdSomeMeals() {
        Long chefId = new Long(73);
        List<Meal> mealsWithChefId = new ArrayList<>();
        mealsWithChefId.add(new Meal("Banana"));
        mealsWithChefId.add(new Meal("Steak"));
        mealsWithChefId.add(new Meal("Ice Cream"));
        doReturn(mealsWithChefId).when(mealDaoMock).findByChefId(chefId);
        mealService = new MealServiceImpl(mealDaoMock);

        assertEquals(mealsWithChefId.size(), mealService.listMealsByChefId(chefId).size());
        verify(mealDaoMock).findByChefId(chefId);
    }

    @Test
    public void testCreateMealValid() {
        Meal meal = new Meal("Banana");
        doReturn(meal).when(mealDaoMock).create(meal);
        mealService = new MealServiceImpl(mealDaoMock);
        Optional<Meal> mealOptional = mealService.createMeal(meal);

        assertTrue(mealOptional.isPresent());
        assertEquals(meal, mealOptional.get());
        verify(mealDaoMock).create(meal);
    }

    @Test
    public void testCreateMealInvalid() {
        mealService = new MealServiceImpl(mealDaoMock);
        try {
            mealService.createMeal(null);
            fail("Should throw exception");
        } catch (Exception e) {
            assertTrue(e instanceof ServiceException);
        }
    }

    @Test
    public void testRetrieveMealByIdExists() {
        List<Meal> mealsWithId = new ArrayList<>();
        Meal meal = new Meal("Banana", new Long(43), null, new Long(2));
        mealsWithId.add(meal);
        doReturn(mealsWithId).when(mealDaoMock).findById(meal.getId());
        mealService = new MealServiceImpl(mealDaoMock);
        Optional<Meal> mealOptional = mealService.retrieveMealById(meal.getId());

        assertTrue(mealOptional.isPresent());
        assertEquals(meal, mealOptional.get());
        verify(mealDaoMock).findById(meal.getId());
    }

    @Test
    public void testRetrieveMealByIdDoesNotExist() {
        Long id = new Long(54);
        doReturn(new ArrayList<>()).when(mealDaoMock).findById(id);
        mealService = new MealServiceImpl(mealDaoMock);
        Optional<Meal> mealOptional = mealService.retrieveMealById(id);

        assertFalse(mealOptional.isPresent());
        verify(mealDaoMock).findById(id);
    }

    @Test
    public void testUpdateMealInvalid() {
        mealService = new MealServiceImpl(mealDaoMock);
        try {
            mealService.updateMeal(null);
            fail("Should throw exception");
        } catch (Exception e) {
            assertTrue(e instanceof ServiceException);
        }
    }

    @Test
    public void testUpdateMealExists() {
        Meal meal = new Meal("Steak", new Long(43));
        doReturn(meal).when(mealDaoMock).update(meal);
        mealService = new MealServiceImpl(mealDaoMock);

        assertTrue(mealService.updateMeal(meal).isPresent());
        verify(mealDaoMock).update(meal);
    }

    @Test
    public void testUpdateMealDoesNotExist() {
        Meal meal = new Meal("Steak", new Long(43));
        doThrow(new PersistenceException("Meal with the given ID does not exist")).when(mealDaoMock).update(meal);
        mealService = new MealServiceImpl(mealDaoMock);

        try {
            Optional<Meal> mealOptional = mealService.updateMeal(meal);
            fail("Should throw exception");
        } catch (Exception e) {
            assertTrue(e instanceof ServiceException);
            verify(mealDaoMock).update(meal);
        }
    }

    @Test
    public void testDestroyMealExists() {
        Meal meal = new Meal("Banana");
        doReturn(true).when(mealDaoMock).destroy(meal);
        mealService = new MealServiceImpl(mealDaoMock);

        assertTrue(mealService.destroyMeal(meal));
        verify(mealDaoMock).destroy(meal);
    }

    @Test
    public void testDestroyMealDoesNotExist() {
        Meal meal = new Meal("Banana");
        doThrow(new ServiceException("")).when(mealDaoMock).destroy(meal);
        mealService = new MealServiceImpl(mealDaoMock);

        try {
            mealService.destroyMeal(meal);
            fail("Should throw exception");
        } catch (Exception e) {
            assertTrue(e instanceof ServiceException);
        }
    }
}