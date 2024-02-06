package com.alg.social_media.configuration.exceptions;

import com.alg.social_media.account.adapter.domain.enums.GenericError;

/** Exception to be thrown when no token is provided. */
public class NoTokenProvidedException extends Exception {
  /** Constructor. */
  public NoTokenProvidedException(GenericError error) {
    super(error.getDescription());
  }
}
