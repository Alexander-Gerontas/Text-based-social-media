package com.alg.social_media.account.adapter.application.port.in;

import com.alg.social_media.account.adapter.domain.dto.AccountResponseDto;
import com.alg.social_media.configuration.exceptions.AccountDoesNotExistException;
import java.util.List;

public interface FollowService {

  void followUser(String followerUsername, String followingUsername)
      throws AccountDoesNotExistException;

  List<AccountResponseDto> getAccountFollowers(String username);

  List<AccountResponseDto> getAccountFollowing(String username);
}
