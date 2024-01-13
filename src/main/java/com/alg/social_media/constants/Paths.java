package com.alg.social_media.constants;

public final class Paths {

  private Paths() {}
  public static final String BASE_URI = "/api/v1";

  // registration + authentication
  public static final String REGISTRATION_URI = BASE_URI + "/account/registration";
  public static final String AUTHENTICATION_URI = BASE_URI + "/account/login";

  // posts
  public static final String POST_URI = BASE_URI + "/post";
}
