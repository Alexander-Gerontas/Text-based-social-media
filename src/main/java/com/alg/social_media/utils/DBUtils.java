package com.alg.social_media.utils;

import com.alg.social_media.configuration.database.JpaEntityManagerFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import javax.inject.Inject;

public final class DBUtils {

  private static JpaEntityManagerFactory jpaEntityManagerFactory;
  private static ThreadLocal<EntityManager> threadLocalConnection;

  @Inject
  public DBUtils(final JpaEntityManagerFactory jpaEntityManagerFactory) {
    DBUtils.jpaEntityManagerFactory = jpaEntityManagerFactory;
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

  public static void executeWithTransactionPropagation(DbTransactionOperation operation) {

    // check if transaction is inner or outer
    boolean outerTransaction = !isConnectionOpen();

    var entityManager = getLocalEntityManager();
    var transaction = entityManager.getTransaction();

    if (!transaction.isActive()) {
      transaction.begin();
    }

    try {
      operation.execute(entityManager);

      // commit records and close connection if transaction is outer
      if (outerTransaction) {
        transaction.commit();
        closeConnection();
      }
    } catch (Exception e) {
      transaction.rollback();
      closeConnection();
      throw new RuntimeException(e);
    }
  }

  public static <T> T executeWithTransactionResultPropagation(DbTransactionResultOperation<T> operation) {
    // check if transaction is inner or outer
    boolean outerTransaction = !isConnectionOpen();

    var entityManager = getLocalEntityManager();
    var transaction = entityManager.getTransaction();

    if (!transaction.isActive()) {
      transaction.begin();
    }

    try {
      T result = operation.execute(entityManager);

      // commit records if transaction is outer
      if (outerTransaction) {
        transaction.commit();
      }

      return result;
    } catch (NoResultException e) {
      return null;
    } catch (Exception e) {
      transaction.rollback();
      closeConnection();
      throw new RuntimeException(e);
    } finally {
      if (outerTransaction) {
        closeConnection();
      }
    }
  }

  private static EntityManager getLocalEntityManager() {

    EntityManager entityManager = getCurrentEntityManager();
    if (entityManager == null) {
      entityManager = jpaEntityManagerFactory.getEntityManager();
      setThreadLocalConnection(entityManager);
    }
    return entityManager;
  }

  private static void setThreadLocalConnection(EntityManager entityManager) {
    threadLocalConnection.set(entityManager);
  }

  private static EntityManager getCurrentEntityManager() {
    return threadLocalConnection.get();
  }

  private static boolean isConnectionOpen() {
    return threadLocalConnection.get() != null;
  }

  private static void closeConnection() {
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

    T execute(EntityManager entityManager)
        throws SQLException, InvocationTargetException, IllegalAccessException;
  }
}
