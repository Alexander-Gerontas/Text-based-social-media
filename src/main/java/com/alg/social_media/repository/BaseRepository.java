package com.alg.social_media.repository;

import com.alg.social_media.utils.DBUtils;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;

/**
 * The Base repository.
 *
 * @param <E> the entity
 * @param <P> the primary key of the entity
 */
public abstract class BaseRepository<E, P> {
  protected final DBUtils dbUtils;
  private final Class<E> entityName;

  protected BaseRepository(DBUtils dbUtils, Class<E> entityName) {
    this.dbUtils = dbUtils;
    this.entityName = entityName;
  }

  public E save(E entity) {
    DBUtils.DbTransactionResultOperation<E> operation = entityManager -> {
      entityManager.persist(entity);
      entityManager.flush();
      return entity;
    };

    return dbUtils.executeWithTransactionResultPropagation(operation);
  }

  public void update(E entity) {
    DBUtils.DbTransactionOperation operation = entityManager -> {
      entityManager.merge(entity);
      entityManager.flush();
    };

    dbUtils.executeWithTransactionPropagation(operation);
  }

  public E findById(P id) {
    DBUtils.DbTransactionResultOperation<E> operation = entityManager ->
        entityManager.find(entityName, id);

    return dbUtils.executeWithTransactionResultPropagation(operation);
  }

  public List<E> findAll() {
    DBUtils.DbTransactionResultOperation<List<E>> operation = entityManager -> {

      CriteriaBuilder cb = entityManager.getCriteriaBuilder();
      CriteriaQuery<E> query = cb.createQuery(entityName);

      Root<E> postRoot = query.from(entityName);

      query.select(postRoot).distinct(true);

      TypedQuery<E> typedQuery = entityManager.createQuery(query);

      return typedQuery.getResultList();
    };

    return dbUtils.executeWithTransactionResultPropagation(operation);
  }

  private void delete(P entityId) {
    DBUtils.DbTransactionOperation operation = entityManager -> {
      E entity = entityManager.find(entityName, entityId);
      entityManager.remove(entity);
    };

    dbUtils.executeWithTransactionPropagation(operation);
  }

  public void deleteAll() {
    DBUtils.DbTransactionOperation operation = entityManager -> {
      CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
      CriteriaDelete<E> criteriaDelete = criteriaBuilder.createCriteriaDelete(entityName);
      Root<E> root = criteriaDelete.from(entityName);

      entityManager.createQuery(criteriaDelete).executeUpdate();
    };

    dbUtils.executeWithTransactionPropagation(operation);
  }
}
