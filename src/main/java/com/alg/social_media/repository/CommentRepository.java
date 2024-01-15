package com.alg.social_media.repository;

import com.alg.social_media.model.Account;
import com.alg.social_media.model.Comment;
import com.alg.social_media.model.Post;
import com.alg.social_media.utils.DBUtils;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import javax.inject.Inject;

public class CommentRepository {

  private final DBUtils dbUtils;

  @Inject
  public CommentRepository(DBUtils dbUtils) {
    this.dbUtils = dbUtils;
  }

  public Comment save(Comment newComment) {
    DBUtils.DbTransactionResultOperation<Comment> operation = entityManager -> {
      entityManager.persist(newComment);
      entityManager.flush();
      return newComment;
    };

    return dbUtils.executeWithResultInTransaction(operation);
  }

  public List<Comment> findAll() {
    DBUtils.DbTransactionResultOperation<List<Comment>> operation = entityManager -> {

      CriteriaBuilder cb = entityManager.getCriteriaBuilder();
      CriteriaQuery<Comment> query = cb.createQuery(Comment.class);

      Root<Comment> commentRoot = query.from(Comment.class);

      query.select(commentRoot).distinct(true);

      TypedQuery<Comment> typedQuery = entityManager.createQuery(query);

      return typedQuery.getResultList();
    };

    return dbUtils.executeWithResultInTransaction(operation);
  }

  public List<Comment> findByAuthorId(Long authorId, Long postId) {
    DBUtils.DbTransactionResultOperation<List<Comment>> operation = entityManager -> {

      CriteriaBuilder cb = entityManager.getCriteriaBuilder();
      CriteriaQuery<Comment> query = cb.createQuery(Comment.class);

      Root<Comment> commentRoot = query.from(Comment.class);
      Root<Account> accountRoot = query.from(Account.class);
      Root<Post> postRoot = query.from(Post.class);

      Predicate accountCondition = cb.equal(commentRoot.get("author").get("id"), authorId);
      Predicate postCondition = cb.equal(commentRoot.get("post").get("id"), postId);

      Predicate finalCondition = cb.and(accountCondition, postCondition);

      query.select(commentRoot).where(finalCondition).distinct(true);

      TypedQuery<Comment> typedQuery = entityManager.createQuery(query);

      return typedQuery.getResultList();
    };

    return dbUtils.executeWithResultInTransaction(operation);
  }

  public int countPostsByAuthorId(Long authorId, Long postId) {
    DBUtils.DbTransactionResultOperation<Long> operation = entityManager -> {

      CriteriaBuilder cb = entityManager.getCriteriaBuilder();
      CriteriaQuery<Long> query = cb.createQuery(Long.class);

      Root<Comment> commentRoot = query.from(Comment.class);

      Predicate accountCondition = cb.equal(commentRoot.get("author").get("id"), authorId);
      Predicate postCondition = cb.equal(commentRoot.get("post").get("id"), postId);

      Predicate finalCondition = cb.and(accountCondition, postCondition);

      query.select(cb.count(commentRoot)).where(finalCondition);

      TypedQuery<Long> typedQuery = entityManager.createQuery(query);

      return typedQuery.getSingleResult();
    };

    var resultInTransaction = dbUtils.executeWithResultInTransaction(operation);
    return Math.toIntExact(resultInTransaction);
  }

  public void deleteAll() {
    DBUtils.DbTransactionOperation operation = entityManager -> {
      Query deleteQuery = entityManager.createQuery("DELETE FROM Comment");
      deleteQuery.executeUpdate();
    };

    dbUtils.executeInTransaction(operation);
  }
}
