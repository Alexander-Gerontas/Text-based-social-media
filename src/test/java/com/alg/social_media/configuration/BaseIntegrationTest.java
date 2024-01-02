package com.alg.social_media.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
//@ActiveProfiles("test")
//@SpringBootTest(webEnvironment = RANDOM_PORT)
public class BaseIntegrationTest {

//  @Autowired public ObjectMapper objectMapper;
  public static final ConfiguredPostgresContainer postgres;

  static {
    postgres = ConfiguredPostgresContainer.getInstance();
    postgres.start();
  }
}
