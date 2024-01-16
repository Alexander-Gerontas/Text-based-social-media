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
 * @param <E> the repository entity
 */
public abstract class BaseRepository<E> {
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

    return dbUtils.executeWithResultInTransaction(operation);
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

    return dbUtils.executeWithResultInTransaction(operation);
  }

  public void deleteAll() {
    DBUtils.DbTransactionOperation operation = entityManager -> {
      CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
      CriteriaDelete<E> criteriaDelete = criteriaBuilder.createCriteriaDelete(entityName);
      Root<E> root = criteriaDelete.from(entityName);

      entityManager.createQuery(criteriaDelete).executeUpdate();
    };

    dbUtils.executeInTransaction(operation);
  }
}
