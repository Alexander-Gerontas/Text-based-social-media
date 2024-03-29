package com.alg.social_media.application.service;

import com.alg.social_media.application.port.in.AccountService;
import com.alg.social_media.application.port.in.FollowService;
import com.alg.social_media.application.port.out.FollowRepository;
import com.alg.social_media.configuration.converters.AccountConverter;
import com.alg.social_media.configuration.exceptions.AccountDoesNotExistException;
import com.alg.social_media.domain.dto.AccountResponseDto;
import com.alg.social_media.domain.enums.GenericError;
import com.alg.social_media.domain.model.Account;
import com.alg.social_media.domain.model.Follow;
import jakarta.transaction.Transactional;
import java.util.List;
import javax.inject.Inject;

@Transactional
public class FollowServiceImpl implements FollowService {
  private final FollowRepository followRepository;
  private final AccountService accountService;
  private final AccountConverter accountConverter;

  @Inject
  public FollowServiceImpl(final FollowRepository followRepository,
      final AccountService accountService, final AccountConverter accountConverter) {
    this.followRepository = followRepository;
    this.accountService = accountService;
    this.accountConverter = accountConverter;
  }

  public void followUser(String followerUsername, String followingUsername)
      throws AccountDoesNotExistException {

    // search for account with the same username
    var followerAccount = accountService.findByUsername(followerUsername);
    var followingAccount = accountService.findByUsername(followingUsername);

    // if the following username does not match an existing account throw exception
    if (followingAccount == null) {
      throw new AccountDoesNotExistException(GenericError.ACCOUNT_DOES_NOT_EXIST,
          followingUsername);
    } else if (followerAccount.getId().equals(followingAccount.getId())) {
      throw new RuntimeException("you cannot follow yourself");
    }

    // check if follow already exists
    if (followerAccount.getFollowers()
        .stream()
        .map(Follow::getFollowing)
        .anyMatch(account -> account.equals(followingAccount))) {
      return;
    }

    // save the new follow
    var follow = Follow.builder()
        .follower(followerAccount)
        .following(followingAccount)
        .build();

    followRepository.save(follow);
  }

  public List<AccountResponseDto> getAccountFollowers(String username) {
    Account account = accountService.findByUsername(username);

    // get account followers
    var followers = account.getFollowers().stream().map(Follow::getFollower).toList();

    // convert to response dtos
    return accountConverter.toResponseDtos(followers);
  }

  public List<AccountResponseDto> getAccountFollowing(String username) {
    Account account = accountService.findByUsername(username);

    // get account following
    var followers = account.getFollowing().stream().map(Follow::getFollowing).toList();

    // convert to response dtos
    return accountConverter.toResponseDtos(followers);
  }
}
