package com.alg.social_media.controllers;

import static com.alg.social_media.account.adapter.domain.constants.Paths.AUTHENTICATION_URI;
import static com.alg.social_media.account.adapter.domain.constants.Paths.REGISTRATION_URI;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.alg.social_media.account.adapter.application.port.in.AccountService;
import com.alg.social_media.account.adapter.application.port.out.AccountRepository;
import com.alg.social_media.account.adapter.domain.dto.AccountRegistrationDto;
import com.alg.social_media.account.adapter.domain.enums.GenericError;
import com.alg.social_media.configuration.BaseIntegrationTest;
import com.alg.social_media.configuration.converters.AccountConverter;
import com.alg.social_media.utils.AccountDtoFactory;
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
class AccountControllerIT extends BaseIntegrationTest {
  private final AccountConverter accountConverter;
  private final AccountService accountService;
  private final AccountRepository accountRepository;

  public AccountControllerIT() {
    accountRepository = appComponent.buildAccountRepository();
    accountService = appComponent.buildAccountService();
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
    // reset rest assured
    resetRestAssured();

    // clear repos
    accountRepository.deleteAll();
  }

  @Test
  @SneakyThrows
  void accountRegistrationTest() {
    AccountRegistrationDto registrationDto = AccountDtoFactory.getFreeAccountRegistrationDto();

    given()
        .body(objectMapper.writeValueAsString(registrationDto))
        .when()
        .post(REGISTRATION_URI)
        .then()
        .statusCode(200)
        .extract();

    var entity = accountRepository.findByUsername(registrationDto.getUsername());
    assertEquals(registrationDto.getEmail(), entity.getEmail());
  }

  @Test
  @SneakyThrows
  void accountLoginTest() {
    // create a new account
    var accountRegistrationDto = AccountDtoFactory.getFreeAccountRegistrationDto();

    var account = accountConverter.toAccount(accountRegistrationDto);
    accountRepository.save(account);

    var account1 = AccountDtoFactory.getAccountLoginDto(accountRegistrationDto);

    var response = given()
        .body(objectMapper.writeValueAsString(account1))
        .when()
        .post(AUTHENTICATION_URI)
        .then()
        .statusCode(200)
        .extract();

    assertTrue(response.body().asString().startsWith("Token: "));
  }

  @Test
  @SneakyThrows
  void accountLoginWithWrongPasswordReturnsNoToken() {
    // create a new account
    var accountRegistrationDto = AccountDtoFactory.getFreeAccountRegistrationDto();

    var account = accountConverter.toAccount(accountRegistrationDto);
    accountRepository.save(account);

    var loginDto = AccountDtoFactory.getAccountLoginDto(accountRegistrationDto);
    loginDto.setPassword("pass123");

    var response = given()
        .body(objectMapper.writeValueAsString(loginDto))
        .when()
        .post(AUTHENTICATION_URI)
        .then()
        .statusCode(HttpStatus.UNAUTHORIZED.getCode())
        .extract();

    assertTrue(response.body().asString().startsWith(GenericError.USER_PROVIDED_WRONG_PASSWORD.getDescription()));
  }
}