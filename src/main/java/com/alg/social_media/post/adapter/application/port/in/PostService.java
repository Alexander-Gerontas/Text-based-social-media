package com.alg.social_media.post.adapter.application.port.in;

import com.alg.social_media.configuration.exceptions.PostException;
import com.alg.social_media.configuration.exceptions.SubscriptionException;
import com.alg.social_media.post.adapter.domain.dto.PostDto;
import com.alg.social_media.post.adapter.domain.dto.PostResponseDto;
import com.alg.social_media.post.adapter.domain.model.Post;
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
