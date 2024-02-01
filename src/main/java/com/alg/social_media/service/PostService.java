package com.alg.social_media.service;

import com.alg.social_media.dto.post.PostDto;
import com.alg.social_media.dto.post.PostResponseDto;
import com.alg.social_media.exceptions.PostException;
import com.alg.social_media.exceptions.SubscriptionException;
import com.alg.social_media.model.Post;
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
