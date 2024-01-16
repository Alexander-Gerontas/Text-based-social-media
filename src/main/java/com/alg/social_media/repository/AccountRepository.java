package com.alg.social_media.repository;

import com.alg.social_media.model.Account;
import com.alg.social_media.model.Follow;
import com.alg.social_media.utils.DBUtils;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import javax.inject.Inject;

public class AccountRepository extends BaseRepository<Account, Long> {
    @Inject
    public AccountRepository(DBUtils dbUtils) {
        super(dbUtils, Account.class);
    }

    public Account findByUsername(String username) {
        DBUtils.DbTransactionResultOperation<Account> operation = entityManager -> {

            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Account> query = cb.createQuery(Account.class);

            Root<Account> accountRoot = query.from(Account.class);
            Fetch<Account, Follow> followFetch = accountRoot.fetch("followers", JoinType.LEFT);

            query.select(accountRoot)
                .where(cb.equal(accountRoot.get("username"), username))
                .distinct(true);

            TypedQuery<Account> typedQuery = entityManager.createQuery(query);

            return typedQuery.getSingleResult();
        };

        return dbUtils.executeWithResultInTransaction(operation);
    }
}