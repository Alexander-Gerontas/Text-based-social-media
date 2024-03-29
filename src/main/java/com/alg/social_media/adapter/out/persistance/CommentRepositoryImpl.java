package com.alg.social_media.adapter.out.persistance;

import com.alg.social_media.domain.model.Account;
import com.alg.social_media.application.port.out.CommentRepository;
import com.alg.social_media.domain.model.Comment;
import com.alg.social_media.domain.model.Post;
import com.alg.social_media.utils.DBUtils;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import javax.inject.Inject;

public class CommentRepositoryImpl extends BaseRepositoryImpl<Comment, Long> implements
    CommentRepository {
  @Inject
  public CommentRepositoryImpl() {
    super(Comment.class);
  }

  @Override
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

    return DBUtils.executeWithTransactionResultPropagation(operation);
  }

  @Override
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

    var resultInTransaction = DBUtils.executeWithTransactionResultPropagation(operation);
    return Math.toIntExact(resultInTransaction);
  }

  @Override
  public List<Comment> findAccountPostCommentsReverseChronologically(Long accountId, int page,
      int commentLimit) {
    DBUtils.DbTransactionResultOperation<List<Comment>> operation = entityManager -> {
      CriteriaBuilder cb = entityManager.getCriteriaBuilder();
      CriteriaQuery<Comment> commentQuery = cb.createQuery(Comment.class);

      Root<Comment> commentRoot = commentQuery.from(Comment.class);

      // Set a condition to fetch comments by account id
      Predicate accountCondition = cb.equal(commentRoot.get("post").get("author").get("id"), accountId);

      commentQuery.select(commentRoot)
          .where(accountCondition)
          .orderBy(cb.desc(commentRoot.get("createDate")))
          .distinct(true);

      // Fetch the latest N comments according to page num
      TypedQuery<Comment> commentTypedQuery = entityManager.createQuery(commentQuery)
          .setFirstResult(page * commentLimit)
          .setMaxResults(commentLimit);

      return commentTypedQuery.getResultList();
    };

    return DBUtils.executeWithTransactionResultPropagation(operation);
  }

  @Override
  public List<Comment> findFollowersPostCommentsReverseChronologically(List<Long> accountIds, int page,
      int commentLimit) {
    DBUtils.DbTransactionResultOperation<List<Comment>> operation = entityManager -> {
      CriteriaBuilder cb = entityManager.getCriteriaBuilder();
      CriteriaQuery<Comment> commentQuery = cb.createQuery(Comment.class);

      Root<Comment> commentRoot = commentQuery.from(Comment.class);

      // Set a condition to fetch comments by account id
      Predicate accountCondition = commentRoot.get("post").get("author").get("id").in(accountIds);

      commentQuery.select(commentRoot)
          .where(accountCondition)
          .orderBy(cb.desc(commentRoot.get("createDate")))
          .distinct(true);

      // Fetch the latest N comments according to page num
      TypedQuery<Comment> commentTypedQuery = entityManager.createQuery(commentQuery)
          .setFirstResult(page * commentLimit)
          .setMaxResults(commentLimit);

      return commentTypedQuery.getResultList();
    };

    return DBUtils.executeWithTransactionResultPropagation(operation);
  }
}
