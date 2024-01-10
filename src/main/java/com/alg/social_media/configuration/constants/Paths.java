package com.alg.social_media.configuration.constants;

public final class Paths {

  private Paths() {}

  public static final String BASE_URI_VERSION_0 = "/api/v0";
  public static final String BASE_URI_VERSION_1 = "/api/v1";

  // registration + authentication
  public static final String REGISTRATION_URI = BASE_URI_VERSION_0 + "/account/registration";
  public static final String AUTHENTICATION_URI = BASE_URI_VERSION_0 + "/account/login";

  // posts
  public static final String POST_URI = BASE_URI_VERSION_1 + "/post";
}
