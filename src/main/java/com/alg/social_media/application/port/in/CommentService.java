package com.alg.social_media.application.port.in;

import com.alg.social_media.domain.model.Comment;
import com.alg.social_media.domain.dto.CommentDto;
import com.alg.social_media.domain.dto.CommentResponseDto;
import com.alg.social_media.exceptions.PostDoesNotExistException;
import com.alg.social_media.exceptions.SubscriptionException;
import java.util.List;

public interface CommentService {
  Comment createComment(CommentDto commentDto, Long postId, String username)
      throws SubscriptionException, PostDoesNotExistException;

  List<CommentResponseDto> getAccountPostComments(String username, int page, int pageSize);

  List<CommentResponseDto> getFollowerPostComments(String username, int page, int pageSize);
}
