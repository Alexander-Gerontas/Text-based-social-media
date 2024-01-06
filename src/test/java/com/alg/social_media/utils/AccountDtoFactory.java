package com.alg.social_media.utils;

import com.alg.social_media.objects.AccountDto;
import com.alg.social_media.objects.AccountType;

public final class AccountDtoFactory {
    public static AccountDto getFreeAccountDto() {

        var userDto = new AccountDto(
                "simpleUser",
                "abc@mail.com",
                "userpass",
                AccountType.FREE
        );

        return userDto;
    }

    public static AccountDto getPremiumAccountDto() {

        var accountDto = new AccountDto(
                "adminUser",
                "efg@mail.com",
                "adminpass",
                AccountType.PREMIUM
        );

        return accountDto;
    }
}
