package com.alg.social_media.controllers;

import com.alg.social_media.objects.AccountDto;
import com.alg.social_media.service.AccountService;
import io.javalin.Javalin;
import io.javalin.http.Handler;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RegistrationController {
    private Javalin app;
    private AccountService accountService;

    @Inject
    public RegistrationController(Javalin app, AccountService accountService) {
        this.app = app;
        this.accountService = accountService;

        configureRoutes();
    }

    private void configureRoutes() {
        app.post("/api/v1/account/registration", registrationHandler);
    }

    private final Handler registrationHandler = ctx -> {
        AccountDto accountDto = ctx.bodyAsClass(AccountDto.class);

        // Log the registration
        log.info("Registering account with username: " + accountDto.getUsername());

        // Perform account registration
        var account = accountService.accountRegistration(accountDto);

        // Send the response
        ctx.json("account with username: " + account.getUsername() +
            " and role: " + account.getRole() + " created");

        ctx.status(200);
    };
}
