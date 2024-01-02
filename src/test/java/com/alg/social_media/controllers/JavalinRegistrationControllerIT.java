package com.alg.social_media.controllers;

import com.alg.social_media.configuration.BaseIntegrationTest;
import com.alg.social_media.configuration.GuiceModule;
import com.alg.social_media.converters.AccountConverter;
import com.alg.social_media.repository.AccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JavalinRegistrationControllerIT extends BaseIntegrationTest {
     private Javalin app;
     private AccountRepository accountRepository;
     private AccountConverter accountConverter;
     private ObjectMapper objectMapper;

    public JavalinRegistrationControllerIT() {

        Injector injector = Guice.createInjector(new GuiceModule());

        app = injector.getInstance(Javalin.class);

        objectMapper = injector.getInstance(ObjectMapper.class);

        accountRepository = injector.getInstance(AccountRepository.class);
        accountConverter = injector.getInstance(AccountConverter.class);
    }

    @BeforeAll
    public void init() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
        // todo enable
        // clear repos
//        accountRepository.deleteAll();
//        accountRepository.flush();
    }

    @Test
    @SneakyThrows
    void accountRegistrationTest() {

        var account1 = AccountDtoFactory.getFreeAccountDto();

        JavalinTest.test(app, (server, client) -> {
                Assertions.assertEquals(client.post("/api/v1/account/registration", objectMapper.writeValueAsString(account1)).code(), 200);
        });
    }
}