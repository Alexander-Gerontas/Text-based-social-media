package com.alg.jdbc_exercises.utils;

import com.alg.jdbc_exercises.config.database.JpaEntityManagerFactory;
import jakarta.persistence.EntityManager;

import java.sql.SQLException;

public class DbUtils {
    private final JpaEntityManagerFactory jpaEntityManagerFactory;
    private final ThreadLocal<EntityManager> threadLocalConnection = new ThreadLocal<>();

    public DbUtils(JpaEntityManagerFactory jpaEntityManagerFactory) {
        this.jpaEntityManagerFactory = jpaEntityManagerFactory;
    }

    public void executeInTransaction(DbTransactionOperation operation) {
        var entityManager = jpaEntityManagerFactory.getEntityManager();

        var transaction = entityManager.getTransaction();
        transaction.begin();

        try {
            operation.execute(entityManager);
            transaction.commit();
        } catch (SQLException e) {
            transaction.rollback();
            throw new RuntimeException(e);
        } finally {
            entityManager.close();
        }
    }

    public <T> T executeWithResultInTransaction(DbTransactionResultOperation<T> operation) {
        EntityManager entityManager = jpaEntityManagerFactory.getEntityManager();

        var transaction = entityManager.getTransaction();
        transaction.begin();

        try {
            T result = operation.execute(entityManager);
            transaction.commit();

            return result;
        } catch (SQLException e) {
            transaction.rollback();
            throw new RuntimeException(e);
        } finally {
            entityManager.close();
        }
    }

    public void executeWithTransactionPropagation(DbTransactionOperation operation) {

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