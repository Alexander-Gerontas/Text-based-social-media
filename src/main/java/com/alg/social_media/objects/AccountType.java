package com.alg.social_media.objects;

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
