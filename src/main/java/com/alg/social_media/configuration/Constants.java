package com.alg.social_media.configuration;

public final class Constants {

  private Constants() {}

  // keywords
  public static final String USERNAME = "username";
  public static final String ROLE = "role";
  public static final String AUTHORIZATION = "Authorization";
  public static final String BEARER = "Bearer";

  // security
  public static final String PASSWORD_SALT = "saltandpepper";
  public static final String JWT_SECRET_KEY = "7e8a003c2bf1348846222330d3458712ad9b1de6a1ec8bcf9d41e6ecc66bea2f";
  public static final long JWT_EXPIRATION_TIME = 86400000; // 24 hours
}
