package com.alg.social_media.controllers;

import com.alg.social_media.configuration.BaseIntegrationTest;
import com.alg.social_media.converters.AccountConverter;
import com.alg.social_media.repository.AccountRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RegistrationControllerIT extends BaseIntegrationTest {

    private MockMvc mockMvc;
    @Autowired private WebApplicationContext webApplicationContext;
    @Autowired private AccountRepository accountRepository;
    @Autowired private AccountConverter accountConverter;

    @BeforeAll
    public void init() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
        // clear repos
        accountRepository.deleteAll();
        accountRepository.flush();
    }

    @Test
    @SneakyThrows
    public void accountRegistrationTest() {

        var account1 = AccountDtoFactory.getFreeAccountDto();

        mockMvc = webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(
                        post("/api/v1/account/registration")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        String.valueOf(objectMapper.writeValueAsString(account1))))
                .andExpect(status().isOk());
    }
}