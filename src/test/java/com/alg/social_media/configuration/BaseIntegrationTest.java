package com.alg.social_media.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@DirtiesContext
@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class BaseIntegrationTest {

  @Autowired public ObjectMapper objectMapper;
  public static final ConfiguredPostgresContainer postgres;

  static {
    postgres = ConfiguredPostgresContainer.getInstance();
    postgres.start();
  }
}
