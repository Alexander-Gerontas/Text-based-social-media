package com.alg.social_media.utils;

import com.alg.social_media.dto.account.AccountLoginDto;
import com.alg.social_media.dto.account.AccountRegistrationDto;
import com.alg.social_media.enums.AccountType;

public final class AccountDtoFactory {
    public static AccountRegistrationDto getFreeAccountRegistrationDto() {
      return new AccountRegistrationDto(
                "simpleUser",
                "abc@mail.com",
                "userpass",
                AccountType.FREE
        );
    }

    public static AccountRegistrationDto getPremiumAccountRegistrationDto() {
      return new AccountRegistrationDto(
                "adminUser",
                "efg@mail.com",
                "adminpass",
                AccountType.PREMIUM
        );
    }

    public static AccountRegistrationDto getJaneDoePremiumAccountRegistrationDto() {
      return new AccountRegistrationDto(
                "janeDoe",
                "jane@doe.com",
                "janepass",
                AccountType.PREMIUM
        );
    }

    public static AccountLoginDto getFreeAccountLoginDto() {
      return new AccountLoginDto(
            "simpleUser",
            "userpass"
        );
    }

  public static AccountLoginDto getPremiumAccountLoginDto() {
    return new AccountLoginDto(
        "adminUser",
        "adminpass"
    );
  }

  public static AccountLoginDto getAccountLoginDto(AccountRegistrationDto dto) {
    return new AccountLoginDto(
        dto.getUsername(),
        dto.getPassword()
    );
  }
}
