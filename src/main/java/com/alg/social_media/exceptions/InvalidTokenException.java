package com.alg.social_media.exceptions;

/** Exception to be thrown when an invalid token is provided. */
public class InvalidTokenException extends Exception {
  private final String token;

  /** Constructor. */
  public InvalidTokenException(GenericError error, String token) {
    super(error.getDescription() + token);
    this.token = token;
  }
}
