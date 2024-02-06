package com.alg.social_media.account.adapter.in.web;

import static com.alg.social_media.account.adapter.domain.constants.Keywords.USERNAME;
import static com.alg.social_media.account.adapter.domain.constants.Paths.FOLLOW_URI;
import static com.alg.social_media.account.adapter.domain.constants.Paths.MY_FOLLOWERS_URI;
import static com.alg.social_media.account.adapter.domain.constants.Paths.MY_FOLLOWING_URI;
import static com.alg.social_media.configuration.security.SecurityMiddleware.authenticationHandler;

import com.alg.social_media.account.adapter.application.port.in.FollowService;
import com.alg.social_media.account.adapter.domain.dto.FollowDto;
import com.alg.social_media.account.adapter.domain.enums.AccountType;
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
    // set authentication
    app.before(FOLLOW_URI + "*", authenticationHandler());
    app.before(FOLLOW_URI + "/*", authenticationHandler());

    app.post(FOLLOW_URI, followerHandler(), AccountType.FREE, AccountType.PREMIUM);

    // get a list of my followers
    app.get(MY_FOLLOWERS_URI, getAccountFollowersHandler(), AccountType.FREE, AccountType.PREMIUM);

    // get a list of the accounts I am following
    app.get(MY_FOLLOWING_URI, getAccountFollowingHandler(), AccountType.FREE, AccountType.PREMIUM);
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

  private Handler getAccountFollowersHandler() {
    return ctx -> {
      String username = ctx.attribute(USERNAME);

      // Log the request
      log.info("account with username: " + username + " wants to see his followers");

      // fetch user's followers
      var accountFollowers = followService.getAccountFollowers(username);

      // Send the response
      ctx.json(accountFollowers);
      ctx.status(HttpStatus.OK);
    };
  }

  private Handler getAccountFollowingHandler() {
    return ctx -> {
      String username = ctx.attribute(USERNAME);

      // Log the request
      log.info("account with username: " + username + " wants to see who he is following");

      // fetch user's following users
      var accountFollowers = followService.getAccountFollowing(username);

      // Send the response
      ctx.json(accountFollowers);
      ctx.status(HttpStatus.OK);
    };
  }
}
