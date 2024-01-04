package com.alg.social_media.controllers;

import com.alg.social_media.objects.AccountDto;
import com.alg.social_media.service.AccountService;
import com.google.inject.Injector;
import io.javalin.http.Handler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RegistrationController {
    private static AccountService staticAccountService;

    public static void setInjector(Injector injector) {

        // todo remove
        staticAccountService = injector.getInstance(AccountService.class);
    }

    public static final Handler registrationHandler = ctx -> {
        AccountDto accountDto = ctx.bodyAsClass(AccountDto.class);

        // Log the registration
        log.info("Registering account with username: " + accountDto.getUsername());

        // Perform account registration
        var account = staticAccountService.accountRegistration(accountDto);

        // Send the response
        ctx.json("account with username: " + account.getUsername() +
            " and role: " + account.getRole() + " created");

        ctx.status(200);
    };
}
