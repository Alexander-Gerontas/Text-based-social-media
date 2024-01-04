package com.alg.social_media.controllers;

import static org.junit.Assert.assertEquals;

import com.alg.social_media.configuration.BaseIntegrationTest;
import com.alg.social_media.configuration.GuiceModule;
import com.alg.social_media.converters.AccountConverter;
import com.alg.social_media.repository.AccountRepository;
import com.alg.social_media.utils.AppInjector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import java.io.IOException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RegistrationControllerIT extends BaseIntegrationTest {
     private Javalin app;
     private AccountConverter accountConverter;
     private AccountRepository accountRepository;
     private ObjectMapper objectMapper;

    public RegistrationControllerIT() throws IOException {
//        Injector injector = Guice.createInjector(new GuiceModule(dbConnection));

        AppInjector.setConnection(dbConnection);
        Injector injector = AppInjector.getInjector();

        RegistrationController.setInjector(injector);

        app = injector.getInstance(Javalin.class);
        accountRepository = injector.getInstance(AccountRepository.class);

        // todo move to base int test
        objectMapper = injector.getInstance(ObjectMapper.class);
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
        // clear repos
        accountRepository.deleteAll();
    }

    @Test
    @SneakyThrows
    void accountRegistrationTest() {

        var account1 = AccountDtoFactory.getFreeAccountDto();

        JavalinTest.test(app, (server, client) -> {
                Assertions.assertEquals(client.post("/api/v1/account/registration", objectMapper.writeValueAsString(account1)).code(), 200);
        });

        var entity = accountRepository.findByUsername(account1.getUsername());
        assertEquals(account1.getEmail(), entity.getEmail());
    }
}