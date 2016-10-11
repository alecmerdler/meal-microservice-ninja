package dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import models.Tag;
import ninja.jpa.UnitOfWork;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by alec on 10/11/16.
 */
public class TagDaoImpl extends BaseDao<Tag> implements TagDao {

    @Inject
    public TagDaoImpl(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider, Tag.class.getSimpleName(), Tag.class);
    }

    @Override
    @UnitOfWork
    public List<Tag> findById(Long id) {
        return super.findByProperty("id", id.longValue());
    }

    public List<Tag> findByMealId(Long mealId) {
        EntityManager entityManager = entityManagerProvider.get();

        return entityManager.createQuery("select tag from Tag as tag join tag.meals as meals where meals.id = :mealId")
                .setParameter("mealId", mealId)
                .getResultList();
    }
}
