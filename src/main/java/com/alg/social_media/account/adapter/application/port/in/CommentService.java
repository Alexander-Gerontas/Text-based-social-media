package com.alg.social_media.account.adapter.application.port.in;

import com.alg.social_media.account.adapter.domain.dto.CommentDto;
import com.alg.social_media.account.adapter.domain.dto.CommentResponseDto;
import com.alg.social_media.account.adapter.domain.model.Comment;
import com.alg.social_media.configuration.exceptions.PostDoesNotExistException;
import com.alg.social_media.configuration.exceptions.SubscriptionException;
import java.util.List;

public interface CommentService {
  Comment createComment(CommentDto commentDto, Long postId, String username)
      throws SubscriptionException, PostDoesNotExistException;

  List<CommentResponseDto> getAccountPostComments(String username, int page, int pageSize);

  List<CommentResponseDto> getFollowerPostComments(String username, int page, int pageSize);
}
