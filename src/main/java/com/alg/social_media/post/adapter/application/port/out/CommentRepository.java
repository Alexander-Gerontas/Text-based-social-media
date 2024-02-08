package com.alg.social_media.post.adapter.application.port.out;

import com.alg.social_media.post.adapter.domain.model.Comment;
import com.alg.social_media.utils.BaseRepository;
import java.util.List;

public interface CommentRepository extends BaseRepository<Comment, Long> {

  List<Comment> findByAuthorId(Long authorId, Long postId);

  int countPostsByAuthorId(Long authorId, Long postId);

  // query that fetches the latest comments on user's posts
  List<Comment> findAccountPostCommentsReverseChronologically(Long accountId, int page,
      int commentLimit);

  List<Comment> findFollowersPostCommentsReverseChronologically(List<Long> accountIds, int page,
      int commentLimit);
}
