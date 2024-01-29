package com.alg.social_media.controllers;

import static com.alg.social_media.configuration.security.SecurityMiddleware.authenticationHandler;
import static com.alg.social_media.constants.Keywords.EMAIL;
import static com.alg.social_media.constants.Keywords.TOKEN;
import static com.alg.social_media.constants.Keywords.USERNAME;
import static com.alg.social_media.constants.Paths.ACCOUNT_SEARCH_URI;
import static com.alg.social_media.constants.Paths.ACCOUNT_URI;
import static com.alg.social_media.constants.Paths.AUTHENTICATION_URI;
import static com.alg.social_media.constants.Paths.REGISTRATION_URI;
import static com.alg.social_media.enums.GenericError.ACCOUNT_DOES_NOT_EXIST;
import static com.alg.social_media.enums.GenericError.USER_PROVIDED_WRONG_PASSWORD;
import static com.alg.social_media.handler.GlobalControllerExceptionHandler.handleAccountDoesNotExist;
import static com.alg.social_media.handler.GlobalControllerExceptionHandler.handleAccountExists;
import static com.alg.social_media.handler.GlobalControllerExceptionHandler.handleWrongPassword;

import com.alg.social_media.configuration.security.JwtUtil;
import com.alg.social_media.dto.account.AccountLoginDto;
import com.alg.social_media.dto.account.AccountRegistrationDto;
import com.alg.social_media.dto.account.AccountResponseDto;
import com.alg.social_media.enums.AccountType;
import com.alg.social_media.exceptions.AccountDoesNotExistException;
import com.alg.social_media.exceptions.AccountExistsException;
import com.alg.social_media.exceptions.WrongPasswordException;
import com.alg.social_media.model.Account;
import com.alg.social_media.service.AccountService;
import io.javalin.Javalin;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Slf4j
public class AccountController {
    private final Javalin app;
    private final AccountService accountService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Inject
    public AccountController(final Javalin app, final AccountService accountService,
        final BCryptPasswordEncoder passwordEncoder) {
        this.app = app;
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;

        configureRoutes();
    }

    private void configureRoutes() {
        // set authentication
        app.before(ACCOUNT_URI + "*", authenticationHandler());
        app.before(ACCOUNT_URI + "/*", authenticationHandler());

        app.before(REGISTRATION_URI + "*", ctx -> {});
        app.before(REGISTRATION_URI + "/*", ctx -> {});

        app.before(AUTHENTICATION_URI + "*", ctx -> {});
        app.before(AUTHENTICATION_URI + "/*", ctx -> {});

        // handle exceptions
        app.exception(AccountDoesNotExistException.class, handleAccountDoesNotExist);
        app.exception(WrongPasswordException.class, handleWrongPassword);

        // register new account
        app.post(REGISTRATION_URI, registrationHandler(), AccountType.ANYONE);

        // authenticate user
        app.post(AUTHENTICATION_URI, generateAuthenticationToken(), AccountType.ANYONE);

        // search account by username
        app.get(ACCOUNT_SEARCH_URI, searchHandler(), AccountType.FREE, AccountType.PREMIUM);

        app.exception(AccountExistsException.class, handleAccountExists);
    }

    private Handler generateAuthenticationToken() {
        return ctx -> {
            var loginDto = ctx.bodyAsClass(AccountLoginDto.class);

            // Log the authorization
            log.info("Generating token for user: " + loginDto.getUsername());

            Account account = accountService.findByUsername(loginDto.getUsername());

            // if an account does not exist throw exception
            if (account == null) {
                throw new AccountDoesNotExistException(ACCOUNT_DOES_NOT_EXIST,
                    loginDto.getUsername());
            }

            if (!passwordEncoder.matches(loginDto.getPassword(), account.getPassword())) {
                throw new WrongPasswordException(USER_PROVIDED_WRONG_PASSWORD,
                    loginDto.getUsername());
            }

            // If authentication is successful, generate a token
            String token = JwtUtil.generateToken(account.getUsername(), account.getRole());

            // Send the response
            ctx.json(TOKEN + ": " + token);
            ctx.status(HttpStatus.OK);
        };
    }

    private Handler registrationHandler() {
        return ctx -> {
            AccountRegistrationDto accountRegistrationDto = ctx.bodyAsClass(
                AccountRegistrationDto.class);

            // Log the registration
            log.info("Registering account with username: " + accountRegistrationDto.getUsername());

            // Perform account registration
            var account = accountService.accountRegistration(accountRegistrationDto);

            // Send the response
            ctx.json("account with username: " + account.getUsername() +
                " and role: " + account.getRole() + " created");

            ctx.status(HttpStatus.OK);
        };
    }

    private Handler searchHandler() {
        return ctx -> {
            String username = ctx.queryParam(USERNAME);
            String email = ctx.queryParam(EMAIL);

            var searchParam = (username != null) ? username : email;

            AccountResponseDto responseDto;

            if (username != null && !username.isEmpty() && !username.isBlank()) {
                log.info("Searching for account with username: " + username);
                responseDto = accountService.getAccountDtoByUsername(searchParam);
            } else if (email != null && !email.isEmpty() && !email.isBlank()) {
                log.info("Searching for account with email: " + email);
                responseDto = accountService.getAccountDtoByEmail(searchParam);
            } else {
                throw new RuntimeException("Please provide a username or an email to search for");
            }

            if (responseDto == null) {
                throw new AccountExistsException(ACCOUNT_DOES_NOT_EXIST, searchParam);
            }

            // Send the response
            ctx.json(responseDto);
            ctx.status(HttpStatus.OK);
        };
    }
}
