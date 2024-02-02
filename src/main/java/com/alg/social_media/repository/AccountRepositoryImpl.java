package com.alg.social_media.repository;

import static com.alg.social_media.constants.QueryParameters.EMAIL;
import static com.alg.social_media.constants.QueryParameters.FOLLOWERS;
import static com.alg.social_media.constants.QueryParameters.FOLLOWING;
import static com.alg.social_media.constants.QueryParameters.USERNAME;

import com.alg.social_media.model.Account;
import com.alg.social_media.utils.DBUtils;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import javax.inject.Inject;

public class AccountRepositoryImpl extends BaseRepositoryImpl<Account, Long> implements AccountRepository {
    @Inject
    public AccountRepositoryImpl() {
        super(Account.class);
    }

    private Account findByCondition(String paramName, String param) {
        DBUtils.DbTransactionResultOperation<Account> operation = entityManager -> {

            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Account> query = cb.createQuery(Account.class);

            // create a query that fetches the user and his followers
            Root<Account> accountRoot = query.from(Account.class);
            accountRoot.fetch(FOLLOWERS, JoinType.LEFT);

            query.select(accountRoot)
                .where(cb.equal(accountRoot.get(paramName), param))
                .distinct(true);

            TypedQuery<Account> typedQuery = entityManager.createQuery(query);
            Account account = typedQuery.getSingleResult();

            // create a separate query to fetch the users followed by user
            query = cb.createQuery(Account.class);

            accountRoot = query.from(Account.class);
            accountRoot.fetch(FOLLOWING, JoinType.LEFT);

            query.select(accountRoot)
                .where(cb.equal(accountRoot, account))
                .distinct(true);

            typedQuery = entityManager.createQuery(query);
            return typedQuery.getSingleResult();
        };

        return DBUtils.executeWithTransactionResultPropagation(operation);
    }

    @Override
    public Account findByUsername(String username) {
        return findByCondition(USERNAME, username);
    }

    @Override
    public Account findByEmail(String email) {
        return findByCondition(EMAIL, email);
    }
}