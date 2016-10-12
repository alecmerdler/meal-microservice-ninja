package dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import models.Model;
import ninja.jpa.UnitOfWork;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by alec on 10/5/16.
 */
public class BaseDao<T extends Model> implements Dao<T> {

    protected Provider<EntityManager> entityManagerProvider;
    protected String modelName;
    protected Class entityType;

    @Inject
    public BaseDao(Provider<EntityManager> entityManagerProvider, String modelName, Class entityType) {
        this.entityManagerProvider = entityManagerProvider;
        this.modelName = modelName;
        this.entityType = entityType;
    }

    @Override
    @UnitOfWork
    public List<T> findAll() {
        EntityManager entityManager = entityManagerProvider.get();

        return entityManager.createQuery("select t from " + modelName + " as t")
                .getResultList();
    }

    @Override
    @UnitOfWork
    public List<T> findByProperty(String property, Object value) throws PersistenceException {
        EntityManager entityManager = entityManagerProvider.get();

        Query query = entityManager.createQuery("select t from " + modelName + " as t where t." + property + " = :value")
                .setParameter("value", value);
        return query.getResultList();
    }

    @Override
    @Transactional
    public T create(T model) throws PersistenceException {
        if (model == null) {
            throw new PersistenceException("Model should not be null");
        }
        EntityManager entityManager = entityManagerProvider.get();
        entityManager.persist(model);

        return model;
    }

    @Override
    @Transactional
    public T update(T model) throws PersistenceException {
        if (model == null) {
            throw new PersistenceException("Model should not be null");
        }
        EntityManager entityManager = entityManagerProvider.get();
        T updatedModel = entityManager.merge(model);
        entityManager.flush();

        return updatedModel;
    }

    @Override
    @Transactional
    public boolean destroy(T model) throws PersistenceException {
        if (model == null) {
            throw new PersistenceException("Model should not be null");
        }
        EntityManager entityManager = entityManagerProvider.get();
        T entity = (T) entityManager.find(entityType, new Long(model.getId()));
        entityManager.remove(entity);
        entityManager.flush();

        return true;
    }
}
