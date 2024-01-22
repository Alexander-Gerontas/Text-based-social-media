package com.alg.social_media.controllers;

import static com.alg.social_media.constants.ControllerArgs.COMMENT_LIMIT;
import static com.alg.social_media.constants.ControllerArgs.PAGE;
import static com.alg.social_media.constants.ControllerArgs.PAGE_SIZE;
import static com.alg.social_media.constants.Keywords.AUTHORIZATION;
import static com.alg.social_media.constants.Keywords.BEARER;
import static com.alg.social_media.constants.Paths.COMMENT_URI;
import static com.alg.social_media.constants.Paths.FOLLOWER_POSTS_URI;
import static com.alg.social_media.constants.Paths.MY_POSTS_URI;
import static com.alg.social_media.constants.Paths.POST_URI;
import static com.alg.social_media.utils.CrudUtils.getAuthTokenForUser;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.alg.social_media.configuration.BaseIntegrationTest;
import com.alg.social_media.converters.AccountConverter;
import com.alg.social_media.dto.account.AccountLoginDto;
import com.alg.social_media.dto.account.AccountRegistrationDto;
import com.alg.social_media.dto.post.CommentDto;
import com.alg.social_media.dto.post.CommentResponseDto;
import com.alg.social_media.dto.post.PostDto;
import com.alg.social_media.dto.post.PostResponseDto;
import com.alg.social_media.model.Account;
import com.alg.social_media.model.Post;
import com.alg.social_media.repository.AccountRepository;
import com.alg.social_media.repository.CommentRepository;
import com.alg.social_media.repository.FollowRepository;
import com.alg.social_media.repository.PostRepository;
import com.alg.social_media.utils.AccountDtoFactory;
import com.alg.social_media.utils.CrudUtils;
import com.alg.social_media.utils.DateUtil;
import com.alg.social_media.utils.PostDtoFactory;
import io.javalin.http.HttpStatus;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
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
  private final FollowRepository followRepository;
  private final AccountRepository accountRepository;
  private final AccountConverter accountConverter;
  private final PostRepository postRepository;
  private final CommentRepository commentRepository;

  public PostControllerIT() {
    postRepository = appComponent.buildPostRepository();
    commentRepository = appComponent.buildCommentRepository();
    followRepository = appComponent.buildFollowRepository();
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
    followRepository.deleteAll();
    accountRepository.deleteAll();
  }

  @Test
  @SneakyThrows
  void createFreeUserPostTest() {
    // create a free user
    var registrationDto = AccountDtoFactory.getFreeAccountRegistrationDto();
    var loginDto = AccountDtoFactory.getAccountLoginDto(registrationDto);

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
    var registrationDto = AccountDtoFactory.getPremiumAccountRegistrationDto();
    AccountLoginDto loginDto = AccountDtoFactory.getAccountLoginDto(registrationDto);

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
    var registrationDto = AccountDtoFactory.getFreeAccountRegistrationDto();
    var loginDto = AccountDtoFactory.getAccountLoginDto(registrationDto);

    var account = accountConverter.toAccount(registrationDto);
    accountRepository.save(account);

    var authToken = getAuthTokenForUser(loginDto);

    // create premium user post
    var postDto = PostDtoFactory.getPremiumUserPostDto();

    given()
        .body(objectMapper.writeValueAsString(postDto))
        .header(AUTHORIZATION, BEARER + " " + authToken)
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
    var registrationDto = AccountDtoFactory.getPremiumAccountRegistrationDto();
    var loginDto = AccountDtoFactory.getAccountLoginDto(registrationDto);

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

    var loginDto = AccountDtoFactory.getAccountLoginDto(registrationDto);
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

  @Test
  @SneakyThrows
  void getFollowerPostInReverseChronologicalOrderTest() {
    // create multiple users
    var registrationDto1 = AccountDtoFactory.getFreeAccountRegistrationDto("user1");
    var registrationDto2 = AccountDtoFactory.getFreeAccountRegistrationDto("user2");
    var registrationDto3 = AccountDtoFactory.getFreeAccountRegistrationDto("user3");
    var registrationDto4 = AccountDtoFactory.getFreeAccountRegistrationDto("user4");
    var registrationDto5 = AccountDtoFactory.getFreeAccountRegistrationDto("user5");

    List<AccountRegistrationDto> registrationDtos = List.of(registrationDto1, registrationDto2,
        registrationDto3, registrationDto4, registrationDto5);

    List<Account> accounts = registrationDtos.stream()
        .map(accountConverter::toAccount)
        .toList();

    accounts.forEach(accountRepository::save);

    List<String> authTokens = registrationDtos.stream()
        .map(AccountDtoFactory::getAccountLoginDto)
        .map(CrudUtils::getAuthTokenForUser)
        .toList();

    // first account follows every other user
    accounts.forEach(account -> {
      if (!account.getUsername().equals(registrationDto1.getUsername())) {
        CrudUtils.followUser(authTokens.get(0), account.getUsername());
      }
    });

    // create two posts per user
    authTokens.forEach(authToken -> {
      var postDto1 = PostDtoFactory.getFreeUserPostDto();
      var postDto2 = PostDtoFactory.getFreeUserPostDto();

      createNewPost(postDto1, authToken, HttpStatus.OK);
      createNewPost(postDto2, authToken, HttpStatus.OK);
    });

    // set random dates in posts
    postRepository.findAll().forEach(post -> {
      var date = DateUtil.getRandomLocalDate();

      post.setCreateDate(date);
      postRepository.update(post);
    });

    var response = given()
        .queryParam(PAGE, 0)
        .queryParam(PAGE_SIZE, 10)
        .header(AUTHORIZATION, BEARER + " " + authTokens.get(0))
        .when()
        .get(FOLLOWER_POSTS_URI)
        .then()
        .statusCode(HttpStatus.OK.getCode())
        .extract();

    List<PostResponseDto> followerPosts = Arrays.stream(objectMapper
        .readValue(response.body().asString(), PostResponseDto[].class))
        .toList();

    List<String> expectedUsernames = registrationDtos.stream()
        .map(AccountRegistrationDto::getUsername)
        .collect(Collectors.toList());

    expectedUsernames.remove(registrationDto1.getUsername());

    var actualUsernames = followerPosts.stream()
        .map(PostResponseDto::getAuthor)
        .toList();

    var followerPostsContainExpectedUsernames = expectedUsernames.containsAll(actualUsernames);

    assertTrue(followerPostsContainExpectedUsernames);

    List<LocalDate> postDates = followerPosts.stream()
        .map(PostResponseDto::getCreateDate)
        .toList();

    var datesInReverseChronologicalOrder = DateUtil.areDatesInReverseChronologicalOrder(postDates);
    assertTrue(datesInReverseChronologicalOrder);
  }

  @Test
  @SneakyThrows
  void getMyPostsWithLatestCommentsInChronologicalOrderTest() {
    // create multiple users
    List<AccountRegistrationDto> registrationDtos = List.of(
        AccountDtoFactory.getPremiumAccountRegistrationDto("user1"),
        AccountDtoFactory.getPremiumAccountRegistrationDto("user2"),
        AccountDtoFactory.getPremiumAccountRegistrationDto("user3")
    );

    List<Account> accounts = registrationDtos.stream()
        .map(accountConverter::toAccount)
        .toList();

    accounts.forEach(accountRepository::save);

    List<String> authTokens = registrationDtos.stream()
        .map(AccountDtoFactory::getAccountLoginDto)
        .map(CrudUtils::getAuthTokenForUser)
        .collect(Collectors.toList());

    var firstUserAuthToken = authTokens.get(0);
    authTokens.remove(firstUserAuthToken);

    var userPosts = 4;
    var commentPerUser = 4;

    var totalCommentsInDb = userPosts * commentPerUser * authTokens.size();

    var requestedPosts = userPosts / 2;
    var requestedCommentsPerPost = totalCommentsInDb / 2;

    // first user creates N posts
    for (int i = 0; i < userPosts; i++) {
      var postDto = PostDtoFactory.getFreeUserPostDto();
      createNewPost(postDto, firstUserAuthToken, HttpStatus.OK);
    }

    // assert posts are saved
    List<Post> postList = postRepository.findAll();
    assertEquals(userPosts, postList.size());

    // every other user comments on first user post
    postList.forEach(post -> {

      var postId = post.getId();

      authTokens.forEach(authToken -> {
        for (int i = 0; i < commentPerUser; i++) {
          CommentDto commentDto = PostDtoFactory.getCommentDto();
          commentOnPost(postId, commentDto, authToken, HttpStatus.OK);
        }
      });
    });

    // assert comments are saved
    var comments = commentRepository.findAll();
    assertEquals(totalCommentsInDb, comments.size());

    // set random dates in posts and comments
    postList.forEach(post -> {
      var date = DateUtil.getRandomLocalDate();
      post.setCreateDate(date);

      postRepository.update(post);
    });

    commentRepository.findAll().forEach(comment -> {
      var date = DateUtil.getRandomLocalDate();
      comment.setCreateDate(date);

      commentRepository.update(comment);
    });

    // fetch user posts and latest comments
    var response = given()
        .queryParam(PAGE, 0)
        .queryParam(PAGE_SIZE, requestedPosts)
        .queryParam(COMMENT_LIMIT, requestedCommentsPerPost)
        .header(AUTHORIZATION, BEARER + " " + firstUserAuthToken)
        .when()
        .get(MY_POSTS_URI)
        .then()
        .statusCode(HttpStatus.OK.getCode())
        .extract();

    List<PostResponseDto> postResponseDtos = Arrays.stream(objectMapper
        .readValue(response.body().asString(), PostResponseDto[].class))
        .toList();

    // check that all posts belong to first user
    var actualUsernames = postResponseDtos.stream()
        .map(PostResponseDto::getAuthor)
        .toList();

    var postsContainExpectedUsernames = actualUsernames.contains(registrationDtos.get(0).getUsername());
    assertTrue(postsContainExpectedUsernames);

    int actualCommentNum = postResponseDtos.stream().map(PostResponseDto::getComments).mapToInt(
        Collection::size).sum();
    assertEquals(requestedPosts, postResponseDtos.size());
    assertEquals(requestedCommentsPerPost, actualCommentNum);

    // assert post and comment dates are in order
    List<LocalDate> postDates = postResponseDtos.stream()
        .map(PostResponseDto::getCreateDate)
        .toList();

    var datesInReverseChronologicalOrder = DateUtil.areDatesInReverseChronologicalOrder(postDates);
    assertTrue(datesInReverseChronologicalOrder);

    postResponseDtos.forEach(postResponseDto -> {
          var commentDates = postResponseDto.getComments().stream()
              .map(CommentResponseDto::getCreateDate).toList();

          var commentDatesInReverseChronologicalOrder = DateUtil.areDatesInReverseChronologicalOrder(
              commentDates);

          assertTrue(commentDatesInReverseChronologicalOrder);
        }
    );
  }

  @SneakyThrows
  private void createNewPost(PostDto postDto, String authToken, HttpStatus httpStatus) {
    given()
        .body(objectMapper.writeValueAsString(postDto))
        .header(AUTHORIZATION, BEARER + " " + authToken)
        .when()
        .post(POST_URI)
        .then()
        .statusCode(httpStatus.getCode())
        .extract();
  }

  @SneakyThrows
  private void commentOnPost(Long postId, CommentDto commentDto, String authToken, HttpStatus httpStatus) {
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