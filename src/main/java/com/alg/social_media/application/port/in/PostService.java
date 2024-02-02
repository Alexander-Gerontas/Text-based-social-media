package com.alg.social_media.application.port.in;

import com.alg.social_media.domain.dto.PostResponseDto;
import com.alg.social_media.exceptions.PostException;
import com.alg.social_media.exceptions.SubscriptionException;
import com.alg.social_media.domain.model.Post;
import com.alg.social_media.domain.dto.PostDto;
import java.util.List;
import java.util.UUID;

public interface PostService {

  Post createPost(PostDto postDto, String username)
      throws SubscriptionException, PostException;

  Post findById(Long id);

  List<PostResponseDto> getFollowerPosts(String username, int page, int pageSize);

  List<PostResponseDto> getAccountPosts(String username, int page, int pageSize, int commentLimit);

  PostResponseDto getPostByUuid(UUID postId, int commentLimit);
}
