package com.alg.social_media.exceptions;

/** Exception to be thrown if free user attempts to access premium features. */
public class SubscriptionException extends Exception {

    private final String username;

    /** Constructor. */
    public SubscriptionException(GenericError error, String username) {
        super(error.getDescription() + username);
        this.username = username;
    }
}