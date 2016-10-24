package dao;

import com.google.inject.Provider;
import models.Meal;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by alec on 10/12/16.
 */
public class MealDaoImplTest {
    MealDaoImpl mealDao;

    // Mocks
    Provider<EntityManager> providerMock;
    EntityManager entityManagerMock;
    Query queryMock;

    @Before
    public void beforeEach() {
        entityManagerMock = mock(EntityManager.class);
        providerMock = mock(Provider.class);
        queryMock = mock(Query.class);
        doReturn(entityManagerMock).when(providerMock).get();
        mealDao = new MealDaoImpl(providerMock);
    }

    @Test
    public void testFindAllNoMeals() {
        doReturn(queryMock).when(entityManagerMock).createQuery("select t from Meal as t");
        doReturn(new ArrayList<>()).when(queryMock).getResultList();

        assertEquals(0, mealDao.findAll().size());
    }

    @Test
    public void testFindAllSomeMeals() {
        List<Meal> meals = new ArrayList<>();
        meals.add(new Meal("Banana"));
        doReturn(queryMock).when(entityManagerMock).createQuery("select t from Meal as t");
        doReturn(meals).when(queryMock).getResultList();

        assertEquals(meals.size(), mealDao.findAll().size());
    }

    @Test
    public void testFindByIdOneMeal() {
        List<Meal> meals = new ArrayList<>();
        meals.add(new Meal("Banana", null, null, new Long(1)));
        doReturn(queryMock).when(entityManagerMock).createQuery("select t from Meal as t where t.id = :value");
        doReturn(queryMock).when(queryMock).setParameter("value", meals.get(0).getId());
        doReturn(meals).when(queryMock).getResultList();

        assertEquals(meals.size(), mealDao.findById(meals.get(0).getId()).size());

    }

    @Test
    public void testFindByTagIdNoMeals() {
        Long tagId = new Long(15);
        doReturn(queryMock).when(entityManagerMock).createQuery("select meal from Meal as meal join meal.tags as tag where tag.id = :tagId");
        doReturn(queryMock).when(queryMock).setParameter("tagId", tagId);
        doReturn(new ArrayList<>()).when(queryMock).getResultList();

        assertEquals(0, mealDao.findByTagId(tagId).size());
    }

    @Test
    public void testFindByTagIdSomeMeals() {
        Long tagId = new Long(83);
        List<Meal> meals = new ArrayList<>();
        meals.add(new Meal("Banana"));
        meals.add(new Meal("Apple"));
        doReturn(queryMock).when(entityManagerMock).createQuery("select meal from Meal as meal join meal.tags as tag where tag.id = :tagId");
        doReturn(queryMock).when(queryMock).setParameter("tagId", tagId);
        doReturn(meals).when(queryMock).getResultList();

        assertEquals(meals.size(), mealDao.findByTagId(tagId).size());
    }

    @Test
    public void testFindByChefIdNoMeals() {
        Long chefId = new Long(43);
        doReturn(queryMock).when(entityManagerMock).createQuery("select t from Meal as t where t.chefId = :value");
        doReturn(queryMock).when(queryMock).setParameter("value", chefId);
        doReturn(new ArrayList<>()).when(queryMock).getResultList();

        assertEquals(0, mealDao.findByChefId(chefId).size());
    }

    @Test
    public void testFindByChefIdOneMeal() {
        Long chefId = new Long(87);
        List<Meal> mealsWithChefId = new ArrayList<>();
        mealsWithChefId.add(new Meal("Banana"));
        doReturn(queryMock).when(entityManagerMock).createQuery("select t from Meal as t where t.chefId = :value");
        doReturn(queryMock).when(queryMock).setParameter("value", chefId);
        doReturn(mealsWithChefId).when(queryMock).getResultList();

        assertEquals(mealsWithChefId.size(), mealDao.findByChefId(chefId).size());
        assertEquals(1, mealDao.findByChefId(chefId).size());
    }

    @Test
    public void testFindByChefIdSomeMeals() {
        Long chefId = new Long(42);
        List<Meal> mealsWithChefId = new ArrayList<>();
        mealsWithChefId.add(new Meal("Steak"));
        mealsWithChefId.add(new Meal("Banana"));
        doReturn(queryMock).when(entityManagerMock).createQuery("select t from Meal as t where t.chefId = :value");
        doReturn(queryMock).when(queryMock).setParameter("value", chefId);
        doReturn(mealsWithChefId).when(queryMock).getResultList();

        assertEquals(mealsWithChefId.size(), mealDao.findByChefId(chefId).size());
        assertEquals(mealsWithChefId.size(), mealDao.findByChefId(chefId).size());
    }
}