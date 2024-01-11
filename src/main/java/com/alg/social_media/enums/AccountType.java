package com.alg.social_media.enums;

import io.javalin.security.RouteRole;
import lombok.Getter;

@Getter
public enum AccountType implements RouteRole {
    ANYONE("ANYONE"),
    FREE("FREE"),
    PREMIUM("PREMIUM");

    private final String literal;

    AccountType(String literal) {
        this.literal = literal;
    }

    public String getLiteral() {
        return literal;
    }
}
