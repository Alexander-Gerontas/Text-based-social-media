package com.alg.social_media.controllers;

import static com.alg.social_media.constants.ControllerArgs.PAGE;
import static com.alg.social_media.constants.ControllerArgs.PAGE_SIZE;
import static com.alg.social_media.constants.Keywords.AUTHORIZATION;
import static com.alg.social_media.constants.Keywords.BEARER;
import static com.alg.social_media.constants.Paths.MY_FOLLOWERS_POST_COMMENTS_URI;
import static com.alg.social_media.constants.Paths.MY_POST_COMMENTS_URI;
import static com.alg.social_media.utils.CrudUtils.commentOnPost;
import static com.alg.social_media.utils.CrudUtils.createNewPost;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.alg.social_media.application.port.out.AccountRepository;
import com.alg.social_media.application.port.out.CommentRepository;
import com.alg.social_media.application.port.out.FollowRepository;
import com.alg.social_media.application.port.out.PostRepository;
import com.alg.social_media.configuration.BaseIntegrationTest;
import com.alg.social_media.converters.AccountConverter;
import com.alg.social_media.domain.dto.AccountRegistrationDto;
import com.alg.social_media.domain.dto.CommentDto;
import com.alg.social_media.domain.dto.CommentResponseDto;
import com.alg.social_media.domain.model.Account;
import com.alg.social_media.domain.model.Post;
import com.alg.social_media.utils.AccountDtoFactory;
import com.alg.social_media.utils.CrudUtils;
import com.alg.social_media.utils.DateUtil;
import com.alg.social_media.utils.JwtUtil;
import com.alg.social_media.utils.PostDtoFactory;
import io.javalin.http.HttpStatus;
import java.time.LocalDate;
import java.util.Arrays;
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
class CommentControllerIT extends BaseIntegrationTest {
  private final FollowRepository followRepository;
  private final AccountRepository accountRepository;
  private final AccountConverter accountConverter;
  private final PostRepository postRepository;
  private final CommentRepository commentRepository;

  public CommentControllerIT() {
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
    // reset rest assured
    resetRestAssured();

    // clear repos
    commentRepository.deleteAll();
    postRepository.deleteAll();
    followRepository.deleteAll();
    accountRepository.deleteAll();
  }

  @Test
  @SneakyThrows
  void getLatestCommentsInMyPostsInChronologicalOrderTest() {
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

    var userPosts = 2;
    var commentPerUser = 2;

    var totalCommentsInDb = userPosts * commentPerUser * authTokens.size();
    var requestedComments = totalCommentsInDb / 2;

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

    // fetch latest comments on user posts
    var response = given()
        .queryParam(PAGE, 0)
        .queryParam(PAGE_SIZE, requestedComments)
        .header(AUTHORIZATION, BEARER + " " + firstUserAuthToken)
        .when()
        .get(MY_POST_COMMENTS_URI)
        .then()
        .statusCode(HttpStatus.OK.getCode())
        .extract();

    List<CommentResponseDto> commentResponseDtos = Arrays.stream(objectMapper
        .readValue(response.body().asString(), CommentResponseDto[].class))
        .toList();

    assertEquals(requestedComments, commentResponseDtos.size());

    // assert comment dates are in order
    List<LocalDate> postDates = commentResponseDtos.stream()
        .map(CommentResponseDto::getCreateDate)
        .toList();

    var datesInReverseChronologicalOrder = DateUtil.areDatesInReverseChronologicalOrder(postDates);
    assertTrue(datesInReverseChronologicalOrder);
  }

  @Test
  @SneakyThrows
  void getLatestCommentsInMyFollowersPostsInChronologicalOrderTest() {
    // create multiple users
    List<AccountRegistrationDto> registrationDtos = List.of(
        AccountDtoFactory.getPremiumAccountRegistrationDto("user1"),
        AccountDtoFactory.getPremiumAccountRegistrationDto("user2"),
        AccountDtoFactory.getPremiumAccountRegistrationDto("user3"),
        AccountDtoFactory.getPremiumAccountRegistrationDto("user4")
    );

    List<Account> accounts = registrationDtos.stream()
        .map(accountConverter::toAccount)
        .toList();

    accounts.forEach(accountRepository::save);

    List<String> authTokens = registrationDtos.stream()
        .map(AccountDtoFactory::getAccountLoginDto)
        .map(CrudUtils::getAuthTokenForUser)
        .toList();

    // every other user follows first account except one
    authTokens.stream()
        .filter(token -> !authTokens.get(0).equals(token))
        .filter(token -> !authTokens.get(1).equals(token))
        .forEach(token -> CrudUtils.followUser(token, registrationDtos.get(0).getUsername()));

    var userPosts = 2;
    var commentPerUser = 2;

    var totalPostsInDb = userPosts * authTokens.size();
    var totalCommentsInDb = totalPostsInDb * (commentPerUser) * authTokens.size() - (authTokens.size() * userPosts * commentPerUser);
    var requestedComments = totalCommentsInDb / 2;

    // every user creates N posts
    authTokens.forEach(authToken -> {
      for (int i = 0; i < userPosts; i++) {
        var postDto = PostDtoFactory.getFreeUserPostDto();
        createNewPost(postDto, authToken, HttpStatus.OK);
      }
    });

    // assert posts are saved
    List<Post> postList = postRepository.findAll();
    assertEquals(userPosts * authTokens.size(), postList.size());

    // every user comments on other users posts
    postList.forEach(post -> {
      var postId = post.getId();

      authTokens.forEach(authToken -> {
        var tokenUser = JwtUtil.extractUsername(authToken);

        // a user should not comment on his own post
        if (!tokenUser.equals(post.getAuthor().getUsername())) {
          for (int i = 0; i < commentPerUser; i++) {
            CommentDto commentDto = PostDtoFactory.getCommentDto();
            commentOnPost(postId, commentDto, authToken, HttpStatus.OK);
          }
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

    // fetch latest comments on user posts
    var response = given()
        .queryParam(PAGE, 0)
        .queryParam(PAGE_SIZE, requestedComments)
        .header(AUTHORIZATION, BEARER + " " + authTokens.get(0))
        .when()
        .get(MY_FOLLOWERS_POST_COMMENTS_URI)
        .then()
        .statusCode(HttpStatus.OK.getCode())
        .extract();

    List<CommentResponseDto> commentResponseDtos = Arrays.stream(objectMapper
        .readValue(response.body().asString(), CommentResponseDto[].class))
        .toList();

    assertEquals(requestedComments, commentResponseDtos.size());

    // assert the comments are fetched from first user's posts and his follower's posts
    List<String> followerUsernames = accounts.stream()
        .map(Account::getUsername)
        .filter(username -> !username.equals(registrationDtos.get(1).getUsername()))
        .toList();

    List<String> postAuthors = commentResponseDtos.stream()
        .map(CommentResponseDto::getPostAuthor)
        .distinct()
        .toList();

    assertEquals(followerUsernames.size(), postAuthors.size());
    assertTrue(followerUsernames.containsAll(postAuthors));

    // assert comment dates are in order
    List<LocalDate> postDates = commentResponseDtos.stream()
        .map(CommentResponseDto::getCreateDate)
        .toList();

    var datesInReverseChronologicalOrder = DateUtil.areDatesInReverseChronologicalOrder(postDates);
    assertTrue(datesInReverseChronologicalOrder);
  }
}