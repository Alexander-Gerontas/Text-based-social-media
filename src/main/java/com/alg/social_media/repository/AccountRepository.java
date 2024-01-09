package com.alg.social_media.repository;

import com.alg.social_media.objects.Account;
import com.alg.social_media.utils.DBUtils;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import javax.inject.Inject;

public class AccountRepository {
    private final DBUtils dbUtils;

    @Inject
    public AccountRepository(DBUtils dbUtils) {
        this.dbUtils = dbUtils;
    }

    public Account save(Account newAccount) {
        DBUtils.DbTransactionResultOperation<Account> operation = entityManager -> {
            entityManager.persist(newAccount);
            entityManager.flush();
            return newAccount;
        };

        return dbUtils.executeWithResultInTransaction(operation);
    }

    public Account findById(Long id) {
        DBUtils.DbTransactionResultOperation<Account> operation = entityManager ->
            entityManager.find(Account.class, id);

        return dbUtils.executeWithResultInTransaction(operation);
    }

    public Account findByUsername(String username) {
        DBUtils.DbTransactionResultOperation<Account> operation = entityManager -> {

            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Account> query = cb.createQuery(Account.class);

            Root<Account> accountRoot = query.from(Account.class);

            query.select(accountRoot)
                .where(cb.equal(accountRoot.get("username"), username))
                .distinct(true);

            TypedQuery<Account> typedQuery = entityManager.createQuery(query);

            return typedQuery.getSingleResult();
        };

        return dbUtils.executeWithResultInTransaction(operation);
    }

    private Long delete(Long entityId) {
        DBUtils.DbTransactionResultOperation<Long> operation = entityManager -> {
            var entity = entityManager.find(Account.class, entityId);
            entityManager.remove(entity);
            return entity.getId();
        };

        return dbUtils.executeWithResultInTransaction(operation);
    }

    public void deleteAll() {
        DBUtils.DbTransactionOperation operation = entityManager -> {
            Query deleteQuery = entityManager.createQuery("DELETE FROM Account");
            deleteQuery.executeUpdate();
        };

        dbUtils.executeInTransaction(operation);
    }
}