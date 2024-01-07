package com.alg.social_media.enums;

import lombok.Getter;

@Getter
public enum AccountType {
    FREE("USER"),
    PREMIUM("PREMIUM");

    private final String literal;

    AccountType(String literal) {
        this.literal = literal;
    }

    public String getLiteral() {
        return literal;
    }
}
