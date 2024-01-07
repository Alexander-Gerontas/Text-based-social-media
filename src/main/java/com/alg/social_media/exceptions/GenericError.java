package com.alg.social_media.exceptions;

import lombok.Getter;

/** Generic error. */
@Getter
public enum GenericError {
    GENERIC_ERROR(1000, "Something went wrong"),

    ACCOUNT_WITH_SAME_USERNAME_EXISTS(2000, "Account with username exists: "),

    ACCOUNT_DOES_NOT_EXIST(2001, "Account with username does not exist: ");

    private final int code;
    private final String description;
    GenericError(int code, String description) {
        this.code = code;
        this.description = description;
    }
}

