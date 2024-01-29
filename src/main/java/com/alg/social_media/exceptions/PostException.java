package com.alg.social_media.exceptions;

import com.alg.social_media.enums.GenericError;

/** Exception to be thrown if user creates large post. */
public class PostException extends Exception {
    /** Constructor. */
    public PostException(GenericError error) {
        super(error.getDescription());
    }
}
