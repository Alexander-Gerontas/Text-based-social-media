package com.alg.social_media.configuration.exceptions;

import com.alg.social_media.account.adapter.domain.enums.GenericError;

/** Exception to be thrown if account is already registered. */
public class AccountExistsException extends Exception {

    String username;

    /** Constructor. */
    public AccountExistsException(GenericError error, String username) {
        super(error.getDescription() + username);
        this.username = username;
    }
}
