package com.alg.social_media.controllers;

import static com.alg.social_media.constants.Keywords.USERNAME;
import static com.alg.social_media.constants.Paths.FOLLOW_URI;

import com.alg.social_media.dto.account.FollowDto;
import com.alg.social_media.enums.AccountType;
import com.alg.social_media.service.FollowService;
import io.javalin.Javalin;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FollowController {

  private final Javalin app;
  private final FollowService followService;

  @Inject
  public FollowController(final Javalin app, final FollowService followService) {
    this.app = app;
    this.followService = followService;

    configureRoutes();
  }

  private void configureRoutes() {
    app.post(FOLLOW_URI, followerHandler(), AccountType.FREE, AccountType.PREMIUM);
  }

  private Handler followerHandler() {
    return ctx -> {
      String follower = ctx.attribute(USERNAME);
      String following = ctx.bodyAsClass(FollowDto.class).getFollowingUsername();

      // Log the request
      log.info("account with username: " + follower + " wants to follow: " + following);

      // Follow user
      followService.followUser(follower, following);

      // Send the response
      ctx.json("account with username: " + follower + " is now following: " + following);

      ctx.status(HttpStatus.OK);
    };
  }
}