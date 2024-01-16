package com.alg.social_media.controllers;

import static com.alg.social_media.constants.Keywords.AUTHORIZATION;
import static com.alg.social_media.constants.Keywords.BEARER;
import static com.alg.social_media.constants.Paths.FOLLOW_URI;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.alg.social_media.configuration.BaseIntegrationTest;
import com.alg.social_media.converters.AccountConverter;
import com.alg.social_media.dto.account.FollowDto;
import com.alg.social_media.repository.AccountRepository;
import com.alg.social_media.repository.FollowRepository;
import com.alg.social_media.utils.AccountDtoFactory;
import com.alg.social_media.utils.AuthenticationUtil;
import io.javalin.http.HttpStatus;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
class FollowControllerIT extends BaseIntegrationTest {
  private final AccountConverter accountConverter;
  private final AccountRepository accountRepository;
  private final FollowRepository followRepository;

  public FollowControllerIT() {
    accountRepository = appComponent.buildAccountRepository();
    followRepository = appComponent.buildFollowRepository();
    accountConverter = appComponent.buildAccountConverter();
  }

  @BeforeAll
  public void init() {
  }

  @BeforeEach
  public void setUp() {
  }

  @AfterAll
  public void tearDown() {
  }

  @AfterEach
  public void cleanUp() {
    // clear repos
    followRepository.deleteAll();
    accountRepository.deleteAll();
  }

  @Test
  @SneakyThrows
  void followAccountTest() {
    // create two new accounts
    var accountRegistrationDto = AccountDtoFactory.getFreeAccountRegistrationDto();
    var premiumAccountRegistrationDto = AccountDtoFactory.getPremiumAccountRegistrationDto();

    var freeAccount = accountConverter.toAccount(accountRegistrationDto);
    accountRepository.save(freeAccount);

    var premiumAccount = accountConverter.toAccount(premiumAccountRegistrationDto);
    accountRepository.save(premiumAccount);

    var account1 = AccountDtoFactory.getAccountLoginDto(accountRegistrationDto);
    var account2 = AccountDtoFactory.getAccountLoginDto(premiumAccountRegistrationDto);

    var freeAccountToken = AuthenticationUtil.getAuthTokenForUser(account1);
    var premiumAccountToken = AuthenticationUtil.getAuthTokenForUser(account2);

    given()
        .body(objectMapper.writeValueAsString(new FollowDto(premiumAccount.getUsername())))
        .header(AUTHORIZATION, BEARER + " " + freeAccountToken)
        .when()
        .post(FOLLOW_URI)
        .then()
        .statusCode(HttpStatus.OK.getCode())
        .extract();

    assertEquals(1, followRepository.findAll().size());
  }

  @Test
  @SneakyThrows
  void followYourselfTest() {
    // create a new account
    var accountRegistrationDto = AccountDtoFactory.getFreeAccountRegistrationDto();

    var freeAccount = accountConverter.toAccount(accountRegistrationDto);
    accountRepository.save(freeAccount);

    var account = AccountDtoFactory.getAccountLoginDto(accountRegistrationDto);

    var token = AuthenticationUtil.getAuthTokenForUser(account);

    // send a request to follow yourself
    given()
        .body(objectMapper.writeValueAsString(new FollowDto(freeAccount.getUsername())))
        .header(AUTHORIZATION, BEARER + " " + token)
        .when()
        .post(FOLLOW_URI)
        .then()
        .statusCode(HttpStatus.BAD_REQUEST.getCode())
        .extract();

    assertEquals(0, followRepository.findAll().size());
  }
}