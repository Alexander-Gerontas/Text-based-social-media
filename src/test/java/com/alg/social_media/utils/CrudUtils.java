package com.alg.social_media.utils;

import static com.alg.social_media.constants.Keywords.AUTHORIZATION;
import static com.alg.social_media.constants.Keywords.BEARER;
import static com.alg.social_media.constants.Paths.FOLLOW_URI;
import static io.restassured.RestAssured.given;

import com.alg.social_media.configuration.BaseIntegrationTest;
import com.alg.social_media.constants.Paths;
import com.alg.social_media.dto.account.AccountLoginDto;
import com.alg.social_media.dto.account.FollowDto;
import io.javalin.http.HttpStatus;
import lombok.SneakyThrows;

public final class CrudUtils extends BaseIntegrationTest {
  private CrudUtils() {}

  @SneakyThrows
  public static String getAuthTokenForUser(AccountLoginDto loginDto) {
    // Define the endpoint URL
    String loginUrl = Paths.AUTHENTICATION_URI;

    var response = given()
        .body(objectMapper.writeValueAsString(loginDto))
        .when()
        .post(loginUrl)
        .then()
        .statusCode(HttpStatus.OK.getCode())
        .extract();

    String responseBody = response.body().asString();

    // get token from response
    return responseBody.substring(7);
  }

  @SneakyThrows
  public static void followUser(String authToken, String username) {
    given()
        .body(objectMapper.writeValueAsString(new FollowDto(username)))
        .header(AUTHORIZATION, BEARER + " " + authToken)
        .when()
        .post(FOLLOW_URI)
        .then()
        .statusCode(HttpStatus.OK.getCode())
        .extract();
  }
}
