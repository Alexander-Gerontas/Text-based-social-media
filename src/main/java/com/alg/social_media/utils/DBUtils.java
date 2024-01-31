package com.alg.social_media.utils;

import com.alg.social_media.configuration.database.JpaEntityManagerFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.sql.SQLException;
import javax.inject.Inject;

public class DBUtils {

  private final JpaEntityManagerFactory jpaEntityManagerFactory;
  private final ThreadLocal<EntityManager> threadLocalConnection;

  @Inject
  public DBUtils(final JpaEntityManagerFactory jpaEntityManagerFactory) {
    this.jpaEntityManagerFactory = jpaEntityManagerFactory;
    threadLocalConnection = new ThreadLocal<>();
  }

  public void executeInTransaction(DbTransactionOperation operation) {
    var entityManager = getLocalEntityManager();

    var transaction = entityManager.getTransaction();
    transaction.begin();

    try {
      operation.execute(entityManager);
      transaction.commit();
    } catch (SQLException e) {
      transaction.rollback();
      throw new RuntimeException(e);
    } finally {
      closeConnection();
    }
  }

  public <T> T executeWithResultInTransaction(DbTransactionResultOperation<T> operation) {
    EntityManager entityManager = getLocalEntityManager();

    var transaction = entityManager.getTransaction();
    transaction.begin();

    try {
      T result = operation.execute(entityManager);
      transaction.commit();

      return result;
    } catch (SQLException e) {
      transaction.rollback();
      throw new RuntimeException(e);
    } catch (NoResultException e) {
      return null;
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      closeConnection();
    }
  }

  private EntityManager getLocalEntityManager() {

    EntityManager entityManager = getCurrentEntityManager();
    if (entityManager == null) {
      entityManager = jpaEntityManagerFactory.getEntityManager();
      setThreadLocalConnection(entityManager);
    }
    return entityManager;
  }

  private void setThreadLocalConnection(EntityManager entityManager) {
    threadLocalConnection.set(entityManager);
  }

  private EntityManager getCurrentEntityManager() {
    return threadLocalConnection.get();
  }

  boolean isConnectionOpen() {
    return threadLocalConnection.get() != null;
  }

  private void closeConnection() {
    EntityManager entityManager = getCurrentEntityManager();

    if (entityManager != null) {
      entityManager.close();
      threadLocalConnection.remove();
    }
  }

  @FunctionalInterface
  public interface DbTransactionOperation {

    void execute(EntityManager entityManager) throws SQLException;
  }

  @FunctionalInterface
  public interface DbTransactionResultOperation<T> {

    T execute(EntityManager entityManager) throws SQLException;
  }
}
