package com.alg.social_media.utils;

import com.alg.social_media.dto.AccountLoginDto;
import com.alg.social_media.dto.AccountRegistrationDto;
import com.alg.social_media.enums.AccountType;

public final class AccountDtoFactory {
    public static AccountRegistrationDto getFreeAccountDto() {

        var userDto = new AccountRegistrationDto(
                "simpleUser",
                "abc@mail.com",
                "userpass",
                AccountType.FREE
        );

        return userDto;
    }

    public static AccountRegistrationDto getPremiumAccountDto() {

        var accountDto = new AccountRegistrationDto(
                "adminUser",
                "efg@mail.com",
                "adminpass",
                AccountType.PREMIUM
        );

        return accountDto;
    }

    public static AccountLoginDto getFreeAccountLoginDto() {
        var loginDto = new AccountLoginDto(
            "simpleUser",
            "userpass"
        );

        return loginDto;
    }
}
