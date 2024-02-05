package com.alg.social_media.configuration.exceptions;

import com.alg.social_media.domain.enums.GenericError;

/** Exception to be thrown if free user attempts to access premium features. */
public class SubscriptionException extends Exception {

    private final String username;

    /** Constructor. */
    public SubscriptionException(GenericError error, String username) {
        super(error.getDescription() + username);
        this.username = username;
    }
}
