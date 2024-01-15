package com.alg.social_media.controllers;

import static com.alg.social_media.constants.Keywords.AUTHORIZATION;
import static com.alg.social_media.constants.Keywords.BEARER;
import static com.alg.social_media.constants.Paths.COMMENT_URI;
import static com.alg.social_media.constants.Paths.POST_URI;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.alg.social_media.configuration.BaseIntegrationTest;
import com.alg.social_media.constants.Paths;
import com.alg.social_media.converters.AccountConverter;
import com.alg.social_media.dto.account.AccountLoginDto;
import com.alg.social_media.dto.post.CommentDto;
import com.alg.social_media.model.Post;
import com.alg.social_media.repository.AccountRepository;
import com.alg.social_media.repository.CommentRepository;
import com.alg.social_media.repository.PostRepository;
import com.alg.social_media.utils.AccountDtoFactory;
import com.alg.social_media.utils.PostDtoFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.javalin.http.HttpStatus;
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
class PostControllerIT extends BaseIntegrationTest {
  private final AccountRepository accountRepository;
  private final AccountConverter accountConverter;
  private final PostRepository postRepository;
  private final CommentRepository commentRepository;

  public PostControllerIT() {
    postRepository = appComponent.buildPostRepository();
    commentRepository = appComponent.buildCommentRepository();
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
    commentRepository.deleteAll();
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
        .header(AUTHORIZATION,  BEARER + " " + authToken)
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

  @Test
  @SneakyThrows
  void premiumUserCanCommentTest() {
    // create two users
    var registrationDto = AccountDtoFactory.getPremiumAccountRegistrationDto();
    var janeDoeRegistrationDto = AccountDtoFactory.getJaneDoePremiumAccountRegistrationDto();

    var premiumAccount1 = accountConverter.toAccount(registrationDto);
    var premiumAccount2 = accountConverter.toAccount(janeDoeRegistrationDto);

    accountRepository.save(premiumAccount1);
    accountRepository.save(premiumAccount2);

    var loginDto = AccountDtoFactory.getPremiumAccountLoginDto();
    var janeDoeLoginDto = AccountDtoFactory.getAccountLoginDto(janeDoeRegistrationDto);

    var authToken1 = getAuthTokenForUser(loginDto);
    var authToken2 = getAuthTokenForUser(janeDoeLoginDto);

    // create a new post dto
    var postDto = PostDtoFactory.getFreeUserPostDto();

    // create post with first user
    given()
        .body(objectMapper.writeValueAsString(postDto))
        .header(AUTHORIZATION, BEARER + " " + authToken1)
        .when()
        .post(POST_URI)
        .then()
        .statusCode(HttpStatus.OK.getCode())
        .extract();

    List<Post> savedPosts = postRepository.findAll();
    var post = savedPosts.get(0);

    assertEquals(registrationDto.getUsername(), post.getAuthor().getUsername());
    assertEquals(registrationDto.getRole(), post.getAuthor().getRole());
    assertEquals(1, savedPosts.size());

    assertEquals(1, postRepository.findAll().size());

    CommentDto commentDto = PostDtoFactory.getCommentDto();

    // second user can add a comment on first user's post
    commentOnPost(post.getId(), commentDto, authToken2, HttpStatus.OK);

    // only one record exists in db
    assertEquals(1, commentRepository.findAll().size());
  }

  @Test
  @SneakyThrows
  void freeUserCannotCommentMoreThan5TimesTest() {

    // create two users
    var freeAccountRegistrationDto = AccountDtoFactory.getFreeAccountRegistrationDto();
    var premiumAccountRegistrationDto = AccountDtoFactory.getPremiumAccountRegistrationDto();

    var freeAccount = accountConverter.toAccount(freeAccountRegistrationDto);
    var premiumAccount = accountConverter.toAccount(premiumAccountRegistrationDto);

    accountRepository.save(freeAccount);
    accountRepository.save(premiumAccount);

    var loginDto = AccountDtoFactory.getAccountLoginDto(freeAccountRegistrationDto);
    var premiumAccountLoginDto = AccountDtoFactory.getAccountLoginDto(premiumAccountRegistrationDto);

    var freeAccountAuthToken = getAuthTokenForUser(loginDto);
    var premiumAccountAuthToken = getAuthTokenForUser(premiumAccountLoginDto);

    // create a premium user post
    var postDto = PostDtoFactory.getPremiumUserPostDto();

    // create post with premium user
    given()
        .body(objectMapper.writeValueAsString(postDto))
        .header(AUTHORIZATION, BEARER + " " + premiumAccountAuthToken)
        .when()
        .post(POST_URI)
        .then()
        .statusCode(HttpStatus.OK.getCode())
        .extract();

    List<Post> savedPosts = postRepository.findAll();
    var post = savedPosts.get(0);

    assertEquals(premiumAccount.getUsername(), post.getAuthor().getUsername());
    assertEquals(premiumAccount.getRole(), post.getAuthor().getRole());
    assertEquals(1, savedPosts.size());

    // free user comments on premium user's post 5 times
    for (int i = 0; i < 5; i++) {
      CommentDto commentDto = PostDtoFactory.getCommentDto();
      commentOnPost(post.getId(), commentDto, freeAccountAuthToken, HttpStatus.OK);
    }

    // 5 comments should exist in db
    assertEquals(5, commentRepository.findAll().size());

    // on the second comment we should get an error
    var commentDto = PostDtoFactory.getCommentDto();
    commentOnPost(post.getId(), commentDto, freeAccountAuthToken, HttpStatus.BAD_REQUEST);

    // only 5 comments should exist in db
    assertEquals(5, commentRepository.findAll().size());
  }

  @Test
  @SneakyThrows
  void premiumUsersCanCommentUnlimitedTimesTest() {

    // create two users
    var freeAccountRegistrationDto = AccountDtoFactory.getFreeAccountRegistrationDto();
    var premiumAccountRegistrationDto = AccountDtoFactory.getPremiumAccountRegistrationDto();

    var freeAccount = accountConverter.toAccount(freeAccountRegistrationDto);
    var premiumAccount = accountConverter.toAccount(premiumAccountRegistrationDto);

    accountRepository.save(freeAccount);
    accountRepository.save(premiumAccount);

    var loginDto = AccountDtoFactory.getAccountLoginDto(freeAccountRegistrationDto);
    var premiumAccountLoginDto = AccountDtoFactory.getAccountLoginDto(premiumAccountRegistrationDto);

    var freeAccountAuthToken = getAuthTokenForUser(loginDto);
    var premiumAccountAuthToken = getAuthTokenForUser(premiumAccountLoginDto);

    // create a new post dto
    var postDto = PostDtoFactory.getFreeUserPostDto();

    // create post with free user
    given()
        .body(objectMapper.writeValueAsString(postDto))
        .header(AUTHORIZATION, BEARER + " " + freeAccountAuthToken)
        .when()
        .post(POST_URI)
        .then()
        .statusCode(HttpStatus.OK.getCode())
        .extract();

    List<Post> savedPosts = postRepository.findAll();
    var post = savedPosts.get(0);

    assertEquals(freeAccount.getUsername(), post.getAuthor().getUsername());
    assertEquals(freeAccount.getRole(), post.getAuthor().getRole());
    assertEquals(1, savedPosts.size());

    // premium user comments on free user's post 10 times
    for (int i = 0; i < 10; i++) {
      CommentDto commentDto = PostDtoFactory.getCommentDto();
      commentOnPost(post.getId(), commentDto, premiumAccountAuthToken, HttpStatus.OK);
    }

    // 10 comments should exist in db, and no error should be thrown
    assertEquals(10, commentRepository.findAll().size());
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

  private void commentOnPost(Long postId, CommentDto commentDto, String authToken, HttpStatus httpStatus) throws JsonProcessingException {
    given()
        .body(objectMapper.writeValueAsString(commentDto))
        .header(AUTHORIZATION, BEARER + " " + authToken)
        .when()
        .post(POST_URI + "/" + postId + COMMENT_URI)
        .then()
        .statusCode(httpStatus.getCode())
        .extract();
  }
}