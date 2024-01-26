package com.alg.social_media.repository;

import static com.alg.social_media.constants.QueryParameters.EMAIL;
import static com.alg.social_media.constants.QueryParameters.FOLLOWERS;
import static com.alg.social_media.constants.QueryParameters.USERNAME;

import com.alg.social_media.model.Account;
import com.alg.social_media.utils.DBUtils;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import javax.inject.Inject;

public class AccountRepository extends BaseRepository<Account, Long> {
    @Inject
    public AccountRepository(DBUtils dbUtils) {
        super(dbUtils, Account.class);
    }

    private Account findByCondition(String paramName, String param) {
        DBUtils.DbTransactionResultOperation<Account> operation = entityManager -> {

            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Account> query = cb.createQuery(Account.class);

            Root<Account> accountRoot = query.from(Account.class);
            accountRoot.fetch(FOLLOWERS, JoinType.LEFT);
//            accountRoot.fetch(FOLLOWING, JoinType.LEFT); fixme enable

            query.select(accountRoot)
                .where(cb.equal(accountRoot.get(paramName), param))
                .distinct(true);

            TypedQuery<Account> typedQuery = entityManager.createQuery(query);

            return typedQuery.getSingleResult();
        };

        return dbUtils.executeWithResultInTransaction(operation);
    }

    public Account findByUsername(String username) {
        return findByCondition(USERNAME, username);
    }

    public Account findByEmail(String email) {
        return findByCondition(EMAIL, email);
    }
}