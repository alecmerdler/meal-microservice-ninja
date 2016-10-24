package services;

import dao.MealDao;
import models.Meal;
import models.Tag;
import org.hibernate.service.spi.ServiceException;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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

    }

    @Test
    public void testCreateMealInvalid() {

    }

    @Test
    public void testRetrieveMealByIdExists() {

    }

    @Test
    public void testRetrieveMealByIdDoesNotExist() {

    }

    @Test
    public void testUpdateMealValid() {

    }

    @Test
    public void testUpdateMealInvalid() {

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