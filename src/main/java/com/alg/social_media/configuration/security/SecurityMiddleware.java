package com.alg.social_media.configuration.security;

import static com.alg.social_media.configuration.constants.Paths.AUTHENTICATION_URI;
import static com.alg.social_media.configuration.constants.Paths.BASE_URI_VERSION_1;
import static com.alg.social_media.exceptions.GenericError.INVALID_TOKEN_PROVIDED;
import static com.alg.social_media.exceptions.GenericError.NO_TOKEN_PROVIDED;
import static com.alg.social_media.handler.GlobalControllerExceptionHandler.handleAccountDoesNotExist;
import static com.alg.social_media.handler.GlobalControllerExceptionHandler.handleInvalidTokenProvided;
import static com.alg.social_media.handler.GlobalControllerExceptionHandler.handleNoTokenProvided;
import static com.alg.social_media.handler.GlobalControllerExceptionHandler.handleWrongPassword;

import com.alg.social_media.dto.AccountLoginDto;
import com.alg.social_media.exceptions.AccountDoesNotExistException;
import com.alg.social_media.exceptions.GenericError;
import com.alg.social_media.exceptions.InvalidTokenException;
import com.alg.social_media.exceptions.NoTokenProvidedException;
import com.alg.social_media.exceptions.WrongPasswordException;
import com.alg.social_media.objects.Account;
import com.alg.social_media.service.AccountService;
import com.alg.social_media.utils.PasswordEncoder;
import io.javalin.Javalin;
import io.javalin.http.Handler;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SecurityMiddleware {
  private final Javalin app;
  private final AccountService accountService;
  private final PasswordEncoder passwordEncoder;
  private final Handler authHandler;
  private final Handler loginHandler;

  @Inject
  public SecurityMiddleware(Javalin app, AccountService accountService,
      PasswordEncoder passwordEncoder) {
    this.app = app;

    this.accountService = accountService;
    this.passwordEncoder = passwordEncoder;

    this.authHandler = createAuthenticationHandler();
    this.loginHandler = createLoginHandler();

    configureRoutes();
  }

  private void configureRoutes() {
    app.before(BASE_URI_VERSION_1 + "/*", authHandler);

    app.post(AUTHENTICATION_URI, loginHandler);

    app.exception(NoTokenProvidedException.class, handleNoTokenProvided);
    app.exception(InvalidTokenException.class, handleInvalidTokenProvided);

    app.exception(AccountDoesNotExistException.class, handleAccountDoesNotExist);
    app.exception(WrongPasswordException.class, handleWrongPassword);
  }

  private Handler createAuthenticationHandler() {
    return context -> {
      String token = context.header("Authorization");

      if (token == null || !token.startsWith("Bearer ")) {
        throw new NoTokenProvidedException(NO_TOKEN_PROVIDED);
      }

      try {
        String username = JwtUtil.extractUsername(token.substring(7));

        // Continue to the next handler
        context.attribute("username", username);
      } catch (Exception e) {
        throw new InvalidTokenException(INVALID_TOKEN_PROVIDED, token);
      }
    };
  }

  private Handler createLoginHandler() {
    return ctx -> {
      var loginDto = ctx.bodyAsClass(AccountLoginDto.class);

      // Log the authorization
      log.info("Generating token for user: " + loginDto.getUsername());

      Account account = accountService.findByUsername(loginDto.getUsername());
      var decryptedPassword = passwordEncoder.decryptPassword(account.getPassword());

      // if an account does not exist throw exception
      if (account == null) {
        throw new AccountDoesNotExistException(GenericError.ACCOUNT_DOES_NOT_EXIST,
            loginDto.getUsername());
      }

      if (!loginDto.getPassword().equals(decryptedPassword)) {
        throw new WrongPasswordException(GenericError.USER_PROVIDED_WRONG_PASSWORD,
            loginDto.getUsername());
      }

      // If authentication is successful, generate a token
      String token = JwtUtil.generateToken(loginDto.getUsername());

      // Send the response
      ctx.json("Token: " + token);
      ctx.status(200);
    };
  }
}
