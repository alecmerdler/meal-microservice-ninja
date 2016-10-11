package dao;

import models.Model;

import javax.persistence.PersistenceException;
import java.util.List;

/**
 * Created by alec on 10/5/16.
 */
public interface Dao<T extends Model> {

    List<T> findAll();

    List<T> findByProperty(String property, Object value) throws PersistenceException;

    T create(T model) throws PersistenceException;

    T update(T model) throws PersistenceException;

    boolean destroy(T model) throws PersistenceException;
}
