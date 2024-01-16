package com.alg.social_media.service;

import com.alg.social_media.exceptions.AccountDoesNotExistException;
import com.alg.social_media.exceptions.GenericError;
import com.alg.social_media.model.Follow;
import com.alg.social_media.repository.FollowRepository;
import jakarta.transaction.Transactional;
import javax.inject.Inject;

@Transactional
public class FollowService {
  private final FollowRepository followRepository;
  private final AccountService accountService;

  @Inject
  public FollowService(final FollowRepository followRepository,
      final AccountService accountService) {
    this.followRepository = followRepository;
    this.accountService = accountService;
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
}
