package com.alg.social_media.utils;

import static io.restassured.RestAssured.given;

import com.alg.social_media.configuration.BaseIntegrationTest;
import com.alg.social_media.constants.Paths;
import com.alg.social_media.dto.account.AccountLoginDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.javalin.http.HttpStatus;

public final class AuthenticationUtil extends BaseIntegrationTest {

  private AuthenticationUtil() {}

  public static String getAuthTokenForUser(AccountLoginDto loginDto) throws JsonProcessingException {
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
}
