package com.alg.social_media.exceptions;

/** Exception to be thrown when no token is provided. */
public class NoTokenProvidedException extends Exception {
  /** Constructor. */
  public NoTokenProvidedException(GenericError error) {
    super(error.getDescription());
  }
}
