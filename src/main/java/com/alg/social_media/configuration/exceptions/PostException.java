package com.alg.social_media.configuration.exceptions;

import com.alg.social_media.account.adapter.domain.enums.GenericError;

/** Exception to be thrown if user creates large post. */
public class PostException extends Exception {
    /** Constructor. */
    public PostException(GenericError error) {
        super(error.getDescription());
    }
}
