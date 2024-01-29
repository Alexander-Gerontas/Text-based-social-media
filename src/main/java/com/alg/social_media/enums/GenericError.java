package com.alg.social_media.enums;

import lombok.Getter;

/** Generic error. */
@Getter
public enum GenericError {
    GENERIC_ERROR(1000, "Something went wrong"),

    // Authentication errors
    ACCOUNT_WITH_SAME_USERNAME_EXISTS(2000, "Account with username exists: "),
    ACCOUNT_DOES_NOT_EXIST(2001, "Account with username or email does not exist: "),
    USER_PROVIDED_WRONG_PASSWORD(2002, "User provided wrong password: "),
    NO_TOKEN_PROVIDED(2003, "No token provided: "),
    INVALID_TOKEN_PROVIDED(2004, "Invalid token provided: "),

    // subscription errors
    SUBSCRIPTION_ERROR(3000, "User does not have a premium subscription: "),
    POST_CHARACTER_LIMIT(3001, "You cannot post text above 3000 character"),
    COMMENT_CHARACTER_LIMIT(3002, "Free users can comment up to 5 times. User is not a premium: "),

    //
    POST_DOES_NOT_EXIST(4000, "Post does not exist or deleted: ");

    private final int code;
    private final String description;
    GenericError(int code, String description) {
        this.code = code;
        this.description = description;
    }
}

