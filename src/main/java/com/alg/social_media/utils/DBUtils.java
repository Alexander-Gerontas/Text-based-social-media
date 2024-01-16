package com.alg.social_media.utils;

import com.alg.social_media.configuration.database.JpaEntityManagerFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.sql.SQLException;
import javax.inject.Inject;

public class DBUtils {
	private final JpaEntityManagerFactory jpaEntityManagerFactory;

	@Inject
	public DBUtils(JpaEntityManagerFactory jpaEntityManagerFactory) {
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
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			entityManager.close();
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
