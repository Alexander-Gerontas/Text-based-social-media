package com.alg.social_media.utils;

import com.alg.social_media.dto.account.AccountLoginDto;
import com.alg.social_media.dto.account.AccountRegistrationDto;
import com.alg.social_media.enums.AccountType;

public final class AccountDtoFactory {
  private static AccountRegistrationDto getAccountRegistrationDto(
      String username,
      AccountType accountType) {

    var email = username + "@mail.com";
    var password = username + "pass";

    return new AccountRegistrationDto(
        username,
        email,
        password,
        accountType
    );
  }

  public static AccountRegistrationDto getFreeAccountRegistrationDto(String username) {
    return getAccountRegistrationDto(
        username,
        AccountType.FREE
    );
  }

  public static AccountRegistrationDto getPremiumAccountRegistrationDto(String username) {
    return getAccountRegistrationDto(
        username,
        AccountType.PREMIUM
    );
  }

  public static AccountRegistrationDto getFreeAccountRegistrationDto() {
    return getFreeAccountRegistrationDto("freeuser");
  }

  public static AccountRegistrationDto getPremiumAccountRegistrationDto() {
    return getPremiumAccountRegistrationDto("premiumuser");
  }

  public static AccountRegistrationDto getJaneDoePremiumAccountRegistrationDto() {
    return new AccountRegistrationDto(
        "janeDoe",
        "jane@doe.com",
        "janepass",
        AccountType.PREMIUM
    );
  }

  public static AccountLoginDto getAccountLoginDto(AccountRegistrationDto dto) {
    return new AccountLoginDto(
        dto.getUsername(),
        dto.getPassword()
    );
  }
}
