package dao;

import models.Tag;

import javax.persistence.PersistenceException;
import java.util.List;

/**
 * Created by alec on 10/11/16.
 */
public interface TagDao extends Dao<Tag> {

    List<Tag> findById(Long id) throws PersistenceException;

    List<Tag> findByMealId(Long mealId) throws PersistenceException;
}
