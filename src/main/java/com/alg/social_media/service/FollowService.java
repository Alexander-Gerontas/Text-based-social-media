package com.alg.social_media.service;

import com.alg.social_media.dto.account.AccountResponseDto;
import com.alg.social_media.exceptions.AccountDoesNotExistException;
import java.util.List;

public interface FollowService {

  void followUser(String followerUsername, String followingUsername)
      throws AccountDoesNotExistException;

  List<AccountResponseDto> getAccountFollowers(String username);

  List<AccountResponseDto> getAccountFollowing(String username);
}
