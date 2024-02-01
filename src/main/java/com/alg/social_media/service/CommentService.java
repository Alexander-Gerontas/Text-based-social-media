package com.alg.social_media.service;

import com.alg.social_media.dto.post.CommentDto;
import com.alg.social_media.dto.post.CommentResponseDto;
import com.alg.social_media.exceptions.PostDoesNotExistException;
import com.alg.social_media.exceptions.SubscriptionException;
import com.alg.social_media.model.Comment;
import java.util.List;

public interface CommentService {
  Comment createComment(CommentDto commentDto, Long postId, String username)
      throws SubscriptionException, PostDoesNotExistException;

  List<CommentResponseDto> getAccountPostComments(String username, int page, int pageSize);

  List<CommentResponseDto> getFollowerPostComments(String username, int page, int pageSize);
}
