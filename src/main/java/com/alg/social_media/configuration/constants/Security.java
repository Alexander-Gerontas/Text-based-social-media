package com.alg.social_media.configuration.constants;

import java.time.Duration;

public final class Security {

  private Security() {}

  public static final String JWT_SECRET_KEY = "7e8a003c2bf1348846222330d3458712ad9b1de6a1ec8bcf9d41e6ecc66bea2f";
  public static final long JWT_EXPIRATION_TIME = Duration.ofHours(2).toMillis();
}
