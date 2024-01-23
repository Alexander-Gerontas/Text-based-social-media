package com.alg.social_media.constants;

public final class Paths {

  private Paths() {}
  public static final String BASE_URI = "/api/v1";

  // registration + authentication
  public static final String REGISTRATION_URI = BASE_URI + "/account/registration";
  public static final String AUTHENTICATION_URI = BASE_URI + "/account/login";

  // posts
  public static final String POST_URI = BASE_URI + "/post";
  public static final String FOLLOWER_POSTS_URI = POST_URI + "/follower-posts";
  public static final String MY_POSTS_URI = POST_URI + "/my-posts";

  // comments
  public static final String COMMENT = "/comment";
  public static final String COMMENT_URI = BASE_URI + COMMENT;
  public static final String MY_POST_COMMENTS_URI = COMMENT_URI + "/my-posts";
  public static final String MY_FOLLOWERS_POST_COMMENTS_URI = COMMENT_URI + "/my-followers";

  // follows
  public static final String FOLLOW_URI = BASE_URI + "/follow";
  public static final String MY_FOLLOWERS_URI = FOLLOW_URI + "/my-followers";
  public static final String MY_FOLLOWING_URI = FOLLOW_URI + "/my-following";
}
