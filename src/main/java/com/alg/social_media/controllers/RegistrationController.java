package com.alg.social_media.controllers;

import com.alg.social_media.configuration.security.JwtUtil;
import com.alg.social_media.dto.AccountLoginDto;
import com.alg.social_media.dto.AccountRegistrationDto;
import com.alg.social_media.exceptions.GenericError;
import com.alg.social_media.exceptions.WrongPasswordException;
import com.alg.social_media.service.AccountService;
import com.alg.social_media.utils.PasswordEncoder;
import io.javalin.Javalin;
import io.javalin.http.Handler;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RegistrationController {
    private final Javalin app;
    private AccountService accountService;
    private PasswordEncoder passwordEncoder;

    @Inject
    public RegistrationController(Javalin app, AccountService accountService, PasswordEncoder passwordEncoder) {
        this.app = app;
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;

        configureRoutes();
    }

    private void configureRoutes() {
        app.post("/api/v1/account/registration", registrationHandler);
        app.post("/api/v1/account/login", loginHandler);

        // fixme remove
        app.get("/api/v1/secure/secure-endpoint", context -> context.json("hi"));
    }

    private final Handler registrationHandler = ctx -> {
        AccountRegistrationDto accountRegistrationDto = ctx.bodyAsClass(AccountRegistrationDto.class);

        // Log the registration
        log.info("Registering account with username: " + accountRegistrationDto.getUsername());

        // Perform account registration
        var account = accountService.accountRegistration(accountRegistrationDto);

        // Send the response
        ctx.json("account with username: " + account.getUsername() +
            " and role: " + account.getRole() + " created");

        ctx.status(200);
    };

    private final Handler loginHandler = ctx -> {
        var accountLoginDto = ctx.bodyAsClass(AccountLoginDto.class);

        // Log the registration
        log.info("Generating token for user: " + accountLoginDto.getUsername());

        var account = accountService.findByUsername(accountLoginDto.getUsername());
        var decryptedPassword = passwordEncoder.decryptPassword(account.getPassword());

        if (!accountLoginDto.getPassword().equals(decryptedPassword)) {
            throw new WrongPasswordException(GenericError.USER_PROVIDED_WRONG_PASSWORD,
                accountLoginDto.getUsername());
        }

        // If authentication is successful, generate a token
        String token = JwtUtil.generateToken(accountLoginDto.getUsername());

        // Send the response
        ctx.json("Token: " + token);
        ctx.status(200);
    };
}
