package com.alg.social_media.exceptions;

import lombok.Getter;

/** Generic error. */
@Getter
public enum GenericError {
    GENERIC_ERROR(1000, "Something went wrong"),

    ACCOUNT_WITH_SAME_USERNAME_EXISTS(2000, "Account with username exists: "),

    PRDOUCT_DOES_NOT_EXIST(3000, "Product with id does not exist: ");

    private final int code;
    private final String description;
    GenericError(int code, String description) {
        this.code = code;
        this.description = description;
    }
}

