package com.alg.social_media.repository;

import com.alg.social_media.model.Comment;
import com.alg.social_media.model.Post;
import com.alg.social_media.utils.DBUtils;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.inject.Inject;

public class PostRepository extends BaseRepository<Post, Long> {
  @Inject
  public PostRepository(DBUtils dbUtils) {
    super(dbUtils, Post.class);
  }

  public List<Post> findAccountPostAndCommentsReverseChronologically(Long accountId, int page,
      int postsLimit, int commentLimit) {
    DBUtils.DbTransactionResultOperation<List<Post>> operation = entityManager -> {
      CriteriaBuilder cb = entityManager.getCriteriaBuilder();
      CriteriaQuery<Post> postQuery = cb.createQuery(Post.class);

      Root<Post> postRoot = postQuery.from(Post.class);

      // Set a condition to fetch posts by account id
      Predicate accountCondition = cb.equal(postRoot.get("author").get("id"), accountId);

      postQuery.select(postRoot)
          .where(accountCondition)
          .orderBy(cb.desc(postRoot.get("createDate")))
          .distinct(true);

      // Fetch the latest N posts according to page num
      TypedQuery<Post> postTypedQuery = entityManager.createQuery(postQuery)
          .setFirstResult(page * postsLimit).setMaxResults(postsLimit);

      List<Post> latestPosts = postTypedQuery.getResultList();
      List<Long> postIds = latestPosts.stream().map(Post::getId).toList();

      // Subquery to get the latest 100 comments for each post
      CriteriaQuery<Comment> commentQuery = cb.createQuery(Comment.class);
      Root<Comment> commentRoot = commentQuery.from(Comment.class);

      // Set a condition to fetch comments by posts id
      Predicate postCondition = commentRoot.get("post").get("id").in(postIds);

      commentQuery.select(commentRoot)
          .where(postCondition)
          .orderBy(cb.desc(commentRoot.get("createDate")))
          .distinct(true);

      // Fetch the latest 100 comments for each post
      TypedQuery<Comment> commentTypedQuery = entityManager.createQuery(commentQuery)
          .setFirstResult(0)
          .setMaxResults(commentLimit);

      List<Comment> latestComments = commentTypedQuery.getResultList();

      // Associate comments with their respective posts
      Map<Long, List<Comment>> postCommentsMap = latestComments.stream()
          .collect(Collectors.groupingBy(comment -> comment.getPost().getId()));

      // Set comments for each post
      latestPosts.forEach(post -> post.setComments(postCommentsMap.getOrDefault(post.getId(), Collections.emptyList())));

      return latestPosts;
    };

    return dbUtils.executeWithTransactionResultPropagation(operation);
  }

  public List<Post> findAccountPostReverseChronologically(Set<Long> accountIds, int page, int size) {
    DBUtils.DbTransactionResultOperation<List<Post>> operation = entityManager ->
    {
      CriteriaBuilder cb = entityManager.getCriteriaBuilder();
      CriteriaQuery<Post> query = cb.createQuery(Post.class);

      Root<Post> postRoot = query.from(Post.class);
      postRoot.fetch("comments", JoinType.LEFT);

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

    return dbUtils.executeWithTransactionResultPropagation(operation);
  }

  public Post findPostByUuid(UUID postUuid, int commentLimit) {
    DBUtils.DbTransactionResultOperation<Post> operation = entityManager -> {
      CriteriaBuilder cb = entityManager.getCriteriaBuilder();
      CriteriaQuery<Post> postQuery = cb.createQuery(Post.class);

      Root<Post> postRoot = postQuery.from(Post.class);

      // Set a condition to fetch posts by uuid
      Predicate uuidMatches = cb.equal(postRoot.get("uuid"), postUuid);

      postQuery.select(postRoot)
          .where(uuidMatches)
          .distinct(true);

      // Fetch post that matches the uuid
      TypedQuery<Post> postTypedQuery = entityManager.createQuery(postQuery);

      Post post = postTypedQuery.getSingleResult();
      var postId = post.getId();

      // Subquery to get the latest 100 comments for post
      CriteriaQuery<Comment> commentQuery = cb.createQuery(Comment.class);
      Root<Comment> commentRoot = commentQuery.from(Comment.class);

      // Set a condition to fetch comments by post id
      Predicate postCondition = commentRoot.get("post").get("id").in(postId);

      commentQuery.select(commentRoot)
          .where(postCondition)
          .orderBy(cb.desc(commentRoot.get("createDate")))
          .distinct(true);

      // Fetch the latest 100 comments
      TypedQuery<Comment> commentTypedQuery = entityManager.createQuery(commentQuery)
          .setFirstResult(0)
          .setMaxResults(commentLimit);

      List<Comment> latestComments = commentTypedQuery.getResultList();

      // Set comments in post entity
      post.setComments(latestComments);

      return post;
    };

    return dbUtils.executeWithTransactionResultPropagation(operation);
  }
}
