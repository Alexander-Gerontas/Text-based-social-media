package com.alg.social_media.controllers;

import static org.junit.Assert.assertEquals;

import com.alg.social_media.configuration.BaseIntegrationTest;
import com.alg.social_media.configuration.database.DBConnection;
import com.alg.social_media.configuration.database.LiquibaseConfiguration;
import com.alg.social_media.converters.AccountConverter;
import com.alg.social_media.repository.AccountRepository;
import com.alg.social_media.service.AccountService;
import com.alg.social_media.utils.AccountDtoFactory;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RegistrationControllerIT extends BaseIntegrationTest {
     private final Javalin app;
     private final RegistrationController registrationController;
     private final DBConnection dbConnection;
     private final LiquibaseConfiguration liquibaseConfiguration;
     private AccountConverter accountConverter;
     private final AccountService accountService;
     private final AccountRepository accountRepository;

    @Inject
    public RegistrationControllerIT() {
        app = appComponent.buildJavalin();
        registrationController = appComponent.buildRegistrationController();
        dbConnection = appComponent.buildDBConnection();
        liquibaseConfiguration = appComponent.buildLiquibaseConfiguration();
        accountRepository = appComponent.buildAccountRepository();
        accountService = appComponent.buildAccountService();
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