package com.alg.social_media.configuration.security;

import static com.alg.social_media.constants.Keywords.AUTHORIZATION;
import static com.alg.social_media.constants.Keywords.BEARER;
import static com.alg.social_media.constants.Keywords.ROLE;
import static com.alg.social_media.constants.Keywords.TOKEN;
import static com.alg.social_media.constants.Keywords.USERNAME;
import static com.alg.social_media.constants.Paths.AUTHENTICATION_URI;
import static com.alg.social_media.constants.Paths.POST_URI;
import static com.alg.social_media.exceptions.GenericError.INVALID_TOKEN_PROVIDED;
import static com.alg.social_media.exceptions.GenericError.NO_TOKEN_PROVIDED;
import static com.alg.social_media.handler.GlobalControllerExceptionHandler.handleAccountDoesNotExist;
import static com.alg.social_media.handler.GlobalControllerExceptionHandler.handleInvalidTokenProvided;
import static com.alg.social_media.handler.GlobalControllerExceptionHandler.handleNoTokenProvided;
import static com.alg.social_media.handler.GlobalControllerExceptionHandler.handleWrongPassword;

import com.alg.social_media.dto.account.AccountLoginDto;
import com.alg.social_media.enums.AccountType;
import com.alg.social_media.exceptions.AccountDoesNotExistException;
import com.alg.social_media.exceptions.GenericError;
import com.alg.social_media.exceptions.InvalidTokenException;
import com.alg.social_media.exceptions.NoTokenProvidedException;
import com.alg.social_media.exceptions.WrongPasswordException;
import com.alg.social_media.model.Account;
import com.alg.social_media.service.AccountService;
import com.alg.social_media.utils.PasswordEncoder;
import io.javalin.Javalin;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
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
    this.loginHandler = generateAuthenticationToken();

    configureRoutes();
  }

  private void configureRoutes() {
    app.before(POST_URI + "*", authHandler);
    app.before(POST_URI + "/*", authHandler);

    app.post(AUTHENTICATION_URI, loginHandler, AccountType.ANYONE);

    app.exception(NoTokenProvidedException.class, handleNoTokenProvided);
    app.exception(InvalidTokenException.class, handleInvalidTokenProvided);

    app.exception(AccountDoesNotExistException.class, handleAccountDoesNotExist);
    app.exception(WrongPasswordException.class, handleWrongPassword);
  }

  private Handler createAuthenticationHandler() {
    return context -> {
      String token = context.header(AUTHORIZATION);

      if (token == null || !token.startsWith(BEARER)) {
        throw new NoTokenProvidedException(NO_TOKEN_PROVIDED);
      }

      try {
        String username = JwtUtil.extractUsername(token.substring(7));
        AccountType accountType = JwtUtil.extractAccountType(token.substring(7));

        // Continue to the next handler
        context.attribute(USERNAME, username);
        context.attribute(ROLE, accountType);
      } catch (Exception e) {
        throw new InvalidTokenException(INVALID_TOKEN_PROVIDED, token);
      }
    };
  }

  private Handler generateAuthenticationToken() {
    return ctx -> {
      var loginDto = ctx.bodyAsClass(AccountLoginDto.class);

      // Log the authorization
      log.info("Generating token for user: " + loginDto.getUsername());

      Account account = accountService.findByUsername(loginDto.getUsername());

      // if an account does not exist throw exception
      if (account == null) {
        throw new AccountDoesNotExistException(GenericError.ACCOUNT_DOES_NOT_EXIST,
            loginDto.getUsername());
      }

      var decryptedPassword = passwordEncoder.decryptPassword(account.getPassword());

      if (!loginDto.getPassword().equals(decryptedPassword)) {
        throw new WrongPasswordException(GenericError.USER_PROVIDED_WRONG_PASSWORD,
            loginDto.getUsername());
      }

      // If authentication is successful, generate a token
      String token = JwtUtil.generateToken(account.getUsername(), account.getRole());

      // Send the response
      ctx.json(TOKEN + ": " + token);
      ctx.status(HttpStatus.OK);
    };
  }
}
