package com.alg.social_media.repository;

import com.alg.social_media.model.Post;
import com.alg.social_media.utils.DBUtils;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;

public class PostRepository extends BaseRepository<Post, Long> {
  @Inject
  public PostRepository(DBUtils dbUtils) {
    super(dbUtils, Post.class);
  }

  public List<Post> findAccountPostReverseChronologically(Set<Long> accountIds, int page, int size) {
    DBUtils.DbTransactionResultOperation<List<Post>> operation = entityManager ->
    {
      CriteriaBuilder cb = entityManager.getCriteriaBuilder();
      CriteriaQuery<Post> query = cb.createQuery(Post.class);

      Root<Post> postRoot = query.from(Post.class);
      Predicate accountCondition = postRoot.get("author").get("id").in(accountIds);

      query.select(postRoot)
          .where(accountCondition)
          .orderBy(cb.desc(postRoot.get("createDate")))
          .distinct(true);

      TypedQuery<Post> typedQuery = entityManager.createQuery(query)
          .setFirstResult(page * size)
          .setMaxResults(size);

      return typedQuery.getResultList();
    };

    return dbUtils.executeWithResultInTransaction(operation);
  }
}
