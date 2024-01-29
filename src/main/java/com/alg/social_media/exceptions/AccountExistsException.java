package com.alg.social_media.exceptions;

import com.alg.social_media.enums.GenericError;

/** Exception to be thrown if account is already registered. */
public class AccountExistsException extends Exception {

    String username;

    /** Constructor. */
    public AccountExistsException(GenericError error, String username) {
        super(error.getDescription() + username);
        this.username = username;
    }
}
