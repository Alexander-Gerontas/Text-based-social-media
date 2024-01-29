package com.alg.social_media.exceptions;

import com.alg.social_media.enums.GenericError;

/** Exception to be thrown if post does not exist. */
public class PostDoesNotExistException extends Exception {
    Long postId;

    /** Constructor. */
    public PostDoesNotExistException(GenericError error, Long postId) {
        super(error.getDescription() + postId);
        this.postId = postId;
    }
}
