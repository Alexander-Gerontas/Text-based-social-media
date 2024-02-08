package com.alg.social_media.post.adapter.application.port.in;

import com.alg.social_media.configuration.exceptions.PostDoesNotExistException;
import com.alg.social_media.configuration.exceptions.SubscriptionException;
import com.alg.social_media.post.adapter.domain.dto.CommentDto;
import com.alg.social_media.post.adapter.domain.dto.CommentResponseDto;
import com.alg.social_media.post.adapter.domain.model.Comment;
import java.util.List;

public interface CommentService {
  Comment createComment(CommentDto commentDto, Long postId, String username)
      throws SubscriptionException, PostDoesNotExistException;

  List<CommentResponseDto> getAccountPostComments(String username, int page, int pageSize);

  List<CommentResponseDto> getFollowerPostComments(String username, int page, int pageSize);
}
