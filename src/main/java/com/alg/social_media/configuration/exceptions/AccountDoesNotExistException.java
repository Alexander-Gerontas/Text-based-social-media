package com.alg.social_media.configuration.exceptions;

import com.alg.social_media.domain.enums.GenericError;

/** Exception to be thrown if account does not exist. */
public class AccountDoesNotExistException extends Exception {

    String username;

    /** Constructor. */
    public AccountDoesNotExistException(GenericError error, String username) {
        super(error.getDescription() + username);
        this.username = username;
    }
}
