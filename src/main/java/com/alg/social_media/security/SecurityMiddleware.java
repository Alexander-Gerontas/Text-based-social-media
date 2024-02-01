package com.alg.social_media.security;

import static com.alg.social_media.constants.Keywords.AUTHORIZATION;
import static com.alg.social_media.constants.Keywords.BEARER;
import static com.alg.social_media.constants.Keywords.ROLE;
import static com.alg.social_media.constants.Keywords.USERNAME;
import static com.alg.social_media.enums.GenericError.INVALID_TOKEN_PROVIDED;
import static com.alg.social_media.enums.GenericError.NO_TOKEN_PROVIDED;
import static com.alg.social_media.handler.GlobalControllerExceptionHandler.handleInvalidTokenProvided;
import static com.alg.social_media.handler.GlobalControllerExceptionHandler.handleNoTokenProvided;

import com.alg.social_media.enums.AccountType;
import com.alg.social_media.exceptions.InvalidTokenException;
import com.alg.social_media.exceptions.NoTokenProvidedException;
import com.alg.social_media.utils.JwtUtil;
import io.javalin.Javalin;
import io.javalin.http.Handler;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SecurityMiddleware {
  private final Javalin app;

  @Inject
  public SecurityMiddleware(final Javalin app) {
    this.app = app;
    configureRoutes();
  }

  private void configureRoutes() {
    // handle exceptions
    app.exception(NoTokenProvidedException.class, handleNoTokenProvided);
    app.exception(InvalidTokenException.class, handleInvalidTokenProvided);
  }

  public static Handler authenticationHandler() {
    return context -> {
      String token = context.header(AUTHORIZATION);

      if (token == null || !token.startsWith(BEARER)) {
        throw new NoTokenProvidedException(NO_TOKEN_PROVIDED);
      }

      try {
        String username = JwtUtil.extractUsername(token.substring(7));
        AccountType accountType = JwtUtil.extractAccountType(token.substring(7));

        // register username and role in handler context
        context.attribute(USERNAME, username);
        context.attribute(ROLE, accountType);
      } catch (Exception e) {
        throw new InvalidTokenException(INVALID_TOKEN_PROVIDED, token);
      }
    };
  }
}
