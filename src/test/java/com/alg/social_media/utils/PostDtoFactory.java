package com.alg.social_media.utils;

import com.alg.social_media.account.adapter.domain.dto.CommentDto;
import com.alg.social_media.account.adapter.domain.dto.PostDto;
import org.apache.commons.lang3.RandomStringUtils;

public final class PostDtoFactory {

  private static PostDto getPostDto(int length) {
    var content = RandomStringUtils.randomAlphabetic(length);
    return new PostDto(content);
  }

  private static CommentDto getCommentDto(int length) {
    var content = RandomStringUtils.randomAlphabetic(length);
    return new CommentDto(content);
  }

  public static PostDto getFreeUserPostDto() {
    return getPostDto(1000);
  }

  public static PostDto getPremiumUserPostDto() {
    return getPostDto(3000);
  }

  public static PostDto getAboveMaxLengthPostDto() {
    return getPostDto(5000);
  }

  public static CommentDto getCommentDto() {
    return getCommentDto(1000);
  }
}
