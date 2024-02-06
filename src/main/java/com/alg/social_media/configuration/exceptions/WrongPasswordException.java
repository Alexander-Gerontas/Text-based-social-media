package com.alg.social_media.configuration.exceptions;

import com.alg.social_media.account.adapter.domain.enums.GenericError;

/** Exception to be thrown if user provided wrong password. */
public class WrongPasswordException extends Exception {

    private final String username;

    /** Constructor. */
    public WrongPasswordException(GenericError error, String username) {
        super(error.getDescription() + username);
        this.username = username;
    }
}
