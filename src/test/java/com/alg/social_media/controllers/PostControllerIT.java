package com.alg.social_media.controllers;

import static com.alg.social_media.configuration.constants.Paths.POST_URI;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.alg.social_media.configuration.BaseIntegrationTest;
import com.alg.social_media.configuration.constants.Paths;
import com.alg.social_media.converters.AccountConverter;
import com.alg.social_media.dto.AccountLoginDto;
import com.alg.social_media.repository.AccountRepository;
import com.alg.social_media.repository.PostRepository;
import com.alg.social_media.utils.AccountDtoFactory;
import com.alg.social_media.utils.PostDtoFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
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
class PostControllerIT extends BaseIntegrationTest {
  private final AccountRepository accountRepository;
  private final AccountConverter accountConverter;
  private final PostRepository postRepository;

  public PostControllerIT() {
    postRepository = appComponent.buildPostRepository();
    accountRepository = appComponent.buildAccountRepository();

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
    postRepository.deleteAll();
    accountRepository.deleteAll();
  }

  @Test
  @SneakyThrows
  void createFreeUserPostTest() {

    // create a free user
    var loginDto = AccountDtoFactory.getFreeAccountLoginDto();
    var registrationDto = AccountDtoFactory.getFreeAccountRegistrationDto();

    var account = accountConverter.toAccount(registrationDto);
    accountRepository.save(account);

    var authToken = getAuthTokenForUser(loginDto);

    // create a free user post
    var postDto = PostDtoFactory.getFreeUserPostDto();

    given()
        .body(objectMapper.writeValueAsString(postDto))
        .header("Authorization", "Bearer " + authToken)
        .when()
        .post(POST_URI)
        .then()
        .statusCode(HttpStatus.OK.getCode())
        .extract();

    var postList = postRepository.findAll();
    var postEntity = postList.get(0);

    assertEquals(1, postList.size());

    assertEquals(postDto.getContent(), postEntity.getContent());
    assertEquals(registrationDto.getUsername(), postEntity.getAuthor().getUsername());
  }

  @Test
  @SneakyThrows
  void createPremiumUserPostTest() {

    // create a premium user
    AccountLoginDto loginDto = AccountDtoFactory.getPremiumAccountLoginDto();
    var registrationDto = AccountDtoFactory.getPremiumAccountRegistrationDto();

    var account = accountConverter.toAccount(registrationDto);
    accountRepository.save(account);

    var authToken = getAuthTokenForUser(loginDto);

    // create a free user post
    var postDto = PostDtoFactory.getPremiumUserPostDto();

    given()
        .body(objectMapper.writeValueAsString(postDto))
        .header("Authorization", "Bearer " + authToken)
        .when()
        .post(POST_URI)
        .then()
        .statusCode(HttpStatus.OK.getCode())
        .extract();

    var postList = postRepository.findAll();
    var postEntity = postList.get(0);

    assertEquals(1, postList.size());

    assertEquals(postDto.getContent(), postEntity.getContent());
    assertEquals(registrationDto.getUsername(), postEntity.getAuthor().getUsername());
  }

  @Test
  @SneakyThrows
  void freeUserCannotPostMoreThan1000WordsTest() {

    // create a free user
    var loginDto = AccountDtoFactory.getFreeAccountLoginDto();
    var registrationDto = AccountDtoFactory.getFreeAccountRegistrationDto();

    var account = accountConverter.toAccount(registrationDto);
    accountRepository.save(account);

    var authToken = getAuthTokenForUser(loginDto);

    // create premium user post
    var postDto = PostDtoFactory.getPremiumUserPostDto();

    given()
        .body(objectMapper.writeValueAsString(postDto))
        .header("Authorization", "Bearer " + authToken)
        .when()
        .post(POST_URI)
        .then()
        .statusCode(HttpStatus.BAD_REQUEST.getCode())
        .extract();

    assertEquals(0, postRepository.findAll().size());
  }

  @Test
  @SneakyThrows
  void premiumUserCannotPostMoreThan3000WordsTest() {
    // create a premium user
    var loginDto = AccountDtoFactory.getPremiumAccountLoginDto();
    var registrationDto = AccountDtoFactory.getPremiumAccountRegistrationDto();

    var account = accountConverter.toAccount(registrationDto);
    accountRepository.save(account);

    var authToken = getAuthTokenForUser(loginDto);

    // create a lengthy post
    var postDto = PostDtoFactory.getAboveMaxLengthPostDto();

    given()
        .body(objectMapper.writeValueAsString(postDto))
        .header("Authorization", "Bearer " + authToken)
        .when()
        .post(POST_URI)
        .then()
        .statusCode(HttpStatus.BAD_REQUEST.getCode())
        .extract();

    assertEquals(0, postRepository.findAll().size());
  }

  @Test
  @SneakyThrows
  void createPostWithoutAuthenticatingFailsTest() {

    // create a free user
    var registrationDto = AccountDtoFactory.getFreeAccountRegistrationDto();

    var account = accountConverter.toAccount(registrationDto);
    accountRepository.save(account);

    // create a free user post
    var postDto = PostDtoFactory.getFreeUserPostDto();
    var endpointUrl = POST_URI;

    // attempt to create post without token
    given()
        .body(objectMapper.writeValueAsString(postDto))
        .when()
        .post(endpointUrl)
        .then()
        .statusCode(HttpStatus.UNAUTHORIZED.getCode())
        .extract();

    assertEquals(0, postRepository.findAll().size());

    var authToken = "invalidToken";

    // attempt to create post with invalid token
    given()
        .body(objectMapper.writeValueAsString(postDto))
        .header("Authorization", "Bearer " + authToken)
        .when()
        .post(endpointUrl)
        .then()
        .statusCode(HttpStatus.UNAUTHORIZED.getCode())
        .extract();

    assertEquals(0, postRepository.findAll().size());
  }

  private String getAuthTokenForUser(AccountLoginDto loginDto) throws JsonProcessingException {
    // Define the endpoint URL
    String loginUrl = Paths.AUTHENTICATION_URI;

    var response = given()
        .body(objectMapper.writeValueAsString(loginDto))
        .when()
        .post(loginUrl)
        .then()
        .statusCode(200)
        .extract();

    String responseBody = response.body().asString();

    // get token from response
    return responseBody.substring(7);
  }
}