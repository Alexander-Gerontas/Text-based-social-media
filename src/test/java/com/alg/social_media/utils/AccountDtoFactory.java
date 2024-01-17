package com.alg.social_media.utils;

import com.alg.social_media.dto.account.AccountLoginDto;
import com.alg.social_media.dto.account.AccountRegistrationDto;
import com.alg.social_media.enums.AccountType;

public final class AccountDtoFactory {
  private static AccountRegistrationDto getFreeAccountRegistrationDto(
      String username,
      String email,
      String password) {

    return getAccountRegistrationDto(
        username,
        email,
        password,
        AccountType.FREE
    );
  }

  private static AccountRegistrationDto getAccountRegistrationDto(
      String username,
      String email,
      String password, AccountType accountType) {
    return new AccountRegistrationDto(
        username,
        email,
        password,
        accountType
    );
  }


  public static AccountRegistrationDto getFreeAccountRegistrationDto(String username) {
    var email = username + "@mail.com";
    var password = username + "pass";

    return getFreeAccountRegistrationDto(
        username,
        email,
        password
    );
  }

  public static AccountRegistrationDto getFreeAccountRegistrationDto() {
    return getFreeAccountRegistrationDto("freeuser");
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
