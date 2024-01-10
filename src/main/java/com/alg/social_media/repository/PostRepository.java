package com.alg.social_media.repository;

import com.alg.social_media.model.Post;
import com.alg.social_media.utils.DBUtils;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;
import javax.inject.Inject;

public class PostRepository {

  private final DBUtils dbUtils;

  @Inject
  public PostRepository(DBUtils dbUtils) {
    this.dbUtils = dbUtils;
  }

  public Post save(Post newPost) {
    DBUtils.DbTransactionResultOperation<Post> operation = entityManager -> {
      entityManager.persist(newPost);
      entityManager.flush();
      return newPost;
    };

    return dbUtils.executeWithResultInTransaction(operation);
  }

  public List<Post> findAll() {
    DBUtils.DbTransactionResultOperation<List<Post>> operation = entityManager -> {

      CriteriaBuilder cb = entityManager.getCriteriaBuilder();
      CriteriaQuery<Post> query = cb.createQuery(Post.class);

      Root<Post> postRoot = query.from(Post.class);

      query.select(postRoot).distinct(true);

      TypedQuery<Post> typedQuery = entityManager.createQuery(query);

      return typedQuery.getResultList();
    };

    return dbUtils.executeWithResultInTransaction(operation);
  }

  public void deleteAll() {
    DBUtils.DbTransactionOperation operation = entityManager -> {
      Query deleteQuery = entityManager.createQuery("DELETE FROM Post");
      deleteQuery.executeUpdate();
    };

    dbUtils.executeInTransaction(operation);
  }
}
