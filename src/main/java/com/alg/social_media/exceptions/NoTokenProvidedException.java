package com.alg.social_media.exceptions;

import com.alg.social_media.enums.GenericError;

/** Exception to be thrown when no token is provided. */
public class NoTokenProvidedException extends Exception {
  /** Constructor. */
  public NoTokenProvidedException(GenericError error) {
    super(error.getDescription());
  }
}
