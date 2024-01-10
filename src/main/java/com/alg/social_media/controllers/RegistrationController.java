package com.alg.social_media.controllers;

import static com.alg.social_media.configuration.constants.Paths.REGISTRATION_URI;

import com.alg.social_media.dto.AccountRegistrationDto;
import com.alg.social_media.service.AccountService;
import io.javalin.Javalin;
import io.javalin.http.Handler;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RegistrationController {
    private final Javalin app;
    private final AccountService accountService;
    private final Handler registrationHandler;

    @Inject
    public RegistrationController(Javalin app, AccountService accountService) {
        this.app = app;
        this.accountService = accountService;

        registrationHandler = setupRegistrationHandler();

        configureRoutes();
    }

    private void configureRoutes() {
        app.post(REGISTRATION_URI, registrationHandler);
    }

    private Handler setupRegistrationHandler() {
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

            ctx.status(200);
        };
    }
}
