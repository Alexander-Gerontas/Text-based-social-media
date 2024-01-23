package com.alg.social_media.controllers;

import static com.alg.social_media.constants.Keywords.AUTHORIZATION;
import static com.alg.social_media.constants.Keywords.BEARER;
import static com.alg.social_media.constants.Paths.FOLLOW_URI;
import static com.alg.social_media.constants.Paths.MY_FOLLOWERS_URI;
import static com.alg.social_media.constants.Paths.MY_FOLLOWING_URI;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.alg.social_media.configuration.BaseIntegrationTest;
import com.alg.social_media.converters.AccountConverter;
import com.alg.social_media.dto.account.AccountRegistrationDto;
import com.alg.social_media.dto.account.AccountResponseDto;
import com.alg.social_media.dto.account.FollowDto;
import com.alg.social_media.model.Account;
import com.alg.social_media.repository.AccountRepository;
import com.alg.social_media.repository.FollowRepository;
import com.alg.social_media.utils.AccountDtoFactory;
import com.alg.social_media.utils.CrudUtils;
import io.javalin.http.HttpStatus;
import java.util.Arrays;
import java.util.List;
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

    var freeAccountToken = CrudUtils.getAuthTokenForUser(account1);
    var premiumAccountToken = CrudUtils.getAuthTokenForUser(account2);

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

    var token = CrudUtils.getAuthTokenForUser(account);

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

  @Test
  @SneakyThrows
  void getAccountFollowersTest() {
    // create multiple users
    List<AccountRegistrationDto> registrationDtos = List.of(
        AccountDtoFactory.getPremiumAccountRegistrationDto("user1"),
        AccountDtoFactory.getPremiumAccountRegistrationDto("user2"),
        AccountDtoFactory.getPremiumAccountRegistrationDto("user3"),
        AccountDtoFactory.getPremiumAccountRegistrationDto("user4"),
        AccountDtoFactory.getPremiumAccountRegistrationDto("user5")
    );

    List<Account> accounts = registrationDtos.stream()
        .map(accountConverter::toAccount)
        .toList();

    accounts.forEach(accountRepository::save);

    List<String> authTokens = registrationDtos.stream()
        .map(AccountDtoFactory::getAccountLoginDto)
        .map(CrudUtils::getAuthTokenForUser)
        .toList();

    // first account follows every other user
    accounts.stream()
        .filter(account -> !account.getUsername().equals(registrationDtos.get(0).getUsername()))
        .forEach(account -> CrudUtils.followUser(authTokens.get(0), account.getUsername()));

    // assert follows exist in db
    assertEquals(registrationDtos.size() - 1, followRepository.findAll().size());

    // send a request to get first user's followers
    var response = given()
        .header(AUTHORIZATION, BEARER + " " + authTokens.get(0))
        .when()
        .get(MY_FOLLOWERS_URI)
        .then()
        .statusCode(HttpStatus.OK.getCode())
        .extract();

    List<AccountResponseDto> accountResponseDtos = Arrays.stream(objectMapper
            .readValue(response.body().asString(), AccountResponseDto[].class))
        .toList();

    var actualUsernames = accountResponseDtos.stream().map(AccountResponseDto::getUsername).toList();

    var expectedUsernames = registrationDtos.stream()
        .map(AccountRegistrationDto::getUsername)
        .filter(username -> !username.equals(registrationDtos.get(0).getUsername()))
        .toList();

    assertEquals(expectedUsernames.size(), accountResponseDtos.size());
    assertTrue(expectedUsernames.containsAll(actualUsernames));
  }

  @Test
  @SneakyThrows
  void getAccountFollowingTest() {
    // create multiple users
    List<AccountRegistrationDto> registrationDtos = List.of(
        AccountDtoFactory.getPremiumAccountRegistrationDto("user1"),
        AccountDtoFactory.getPremiumAccountRegistrationDto("user2"),
        AccountDtoFactory.getPremiumAccountRegistrationDto("user3"),
        AccountDtoFactory.getPremiumAccountRegistrationDto("user4"),
        AccountDtoFactory.getPremiumAccountRegistrationDto("user5")
    );

    List<Account> accounts = registrationDtos.stream()
        .map(accountConverter::toAccount)
        .toList();

    accounts.forEach(accountRepository::save);

    List<String> authTokens = registrationDtos.stream()
        .map(AccountDtoFactory::getAccountLoginDto)
        .map(CrudUtils::getAuthTokenForUser)
        .toList();

    // every other user follows first account
    authTokens.stream()
        .filter(token -> !authTokens.get(0).equals(token))
        .forEach(token -> CrudUtils.followUser(token, registrationDtos.get(0).getUsername()));

    // assert follows exist in db
    assertEquals(registrationDtos.size() - 1, followRepository.findAll().size());

    // send a request to get first user's followers
    var response = given()
        .header(AUTHORIZATION, BEARER + " " + authTokens.get(0))
        .when()
        .get(MY_FOLLOWING_URI)
        .then()
        .statusCode(HttpStatus.OK.getCode())
        .extract();

    List<AccountResponseDto> accountResponseDtos = Arrays.stream(objectMapper
            .readValue(response.body().asString(), AccountResponseDto[].class))
        .toList();

    var actualUsernames = accountResponseDtos.stream().map(AccountResponseDto::getUsername).toList();

    var expectedUsernames = registrationDtos.stream()
        .map(AccountRegistrationDto::getUsername)
        .filter(username -> !username.equals(registrationDtos.get(0).getUsername()))
        .toList();

    assertEquals(expectedUsernames.size(), accountResponseDtos.size());
    assertTrue(expectedUsernames.containsAll(actualUsernames));
  }
}