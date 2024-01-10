package com.alg.social_media.exceptions;

/** Exception to be thrown if user creates large post. */
public class PostException extends Exception {
    /** Constructor. */
    public PostException(GenericError error) {
        super(error.getDescription());
    }
}
