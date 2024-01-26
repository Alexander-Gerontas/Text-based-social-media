package com.alg.social_media.controllers;

import static com.alg.social_media.constants.Keywords.EMAIL;
import static com.alg.social_media.constants.Keywords.USERNAME;
import static com.alg.social_media.constants.Paths.ACCOUNT_SEARCH_URI;
import static com.alg.social_media.constants.Paths.REGISTRATION_URI;
import static com.alg.social_media.handler.GlobalControllerExceptionHandler.handleAccountExists;

import com.alg.social_media.dto.account.AccountRegistrationDto;
import com.alg.social_media.dto.account.AccountResponseDto;
import com.alg.social_media.enums.AccountType;
import com.alg.social_media.exceptions.AccountExistsException;
import com.alg.social_media.exceptions.GenericError;
import com.alg.social_media.service.AccountService;
import io.javalin.Javalin;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

// fixme rename controller
@Slf4j
public class RegistrationController {
    private final Javalin app;
    private final AccountService accountService;

    @Inject
    public RegistrationController(Javalin app, AccountService accountService) {
        this.app = app;
        this.accountService = accountService;

        configureRoutes();
    }

    private void configureRoutes() {
        // register new account
        app.post(REGISTRATION_URI, registrationHandler(), AccountType.ANYONE);

        // search account by username
        app.get(ACCOUNT_SEARCH_URI, searchHandler(), AccountType.FREE, AccountType.PREMIUM);

        app.exception(AccountExistsException.class, handleAccountExists);
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
                throw new AccountExistsException(GenericError.ACCOUNT_DOES_NOT_EXIST, searchParam);
            }

            // Send the response
            ctx.json(responseDto);
            ctx.status(HttpStatus.OK);
        };
    }
}
