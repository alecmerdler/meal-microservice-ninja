package dao;

import models.Meal;

import javax.persistence.PersistenceException;
import java.util.List;

/**
 * Created by alec on 10/11/16.
 */
public interface MealDao extends Dao<Meal> {

    List<Meal> findById(Long id) throws PersistenceException;

    List<Meal> findByTagId(Long tagId) throws PersistenceException;

    List<Meal> findByChefId(Long chefId) throws PersistenceException;
}
