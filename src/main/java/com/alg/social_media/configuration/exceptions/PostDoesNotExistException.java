package com.alg.social_media.configuration.exceptions;

import com.alg.social_media.domain.enums.GenericError;

/** Exception to be thrown if post does not exist. */
public class PostDoesNotExistException extends Exception {
    Long postId;

    /** Constructor. */
    public PostDoesNotExistException(GenericError error, Long postId) {
        super(error.getDescription() + postId);
        this.postId = postId;
    }
}
