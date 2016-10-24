package dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import models.Meal;
import ninja.jpa.UnitOfWork;
import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.List;

/**
 * Created by alec on 10/11/16.
 */
public class MealDaoImpl extends BaseDao<Meal> implements MealDao {

    @Inject
    public MealDaoImpl(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider, Meal.class.getSimpleName(), Meal.class);
    }

    @Override
    @UnitOfWork
    public List<Meal> findById(Long id) throws PersistenceException {
        return super.findByProperty("id", id.longValue());
    }

    @Override
    @UnitOfWork
    public List<Meal> findByTagId(Long tagId) throws PersistenceException {
        EntityManager entityManager = entityManagerProvider.get();

        return entityManager.createQuery("select meal from Meal as meal join meal.tags as tag where tag.id = :tagId")
                .setParameter("tagId", tagId)
                .getResultList();
    }

    @Override
    @UnitOfWork
    public List<Meal> findByChefId(Long chefId) throws PersistenceException {
        EntityManager entityManager = entityManagerProvider.get();

        return super.findByProperty("chefId", chefId);
    }
}
