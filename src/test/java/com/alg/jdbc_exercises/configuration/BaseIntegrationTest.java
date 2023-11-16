package com.alg.jdbc_exercises.configuration;

import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class BaseIntegrationTest {
  public static final ConfiguredPostgresContainer postgres;

  static {
    postgres = ConfiguredPostgresContainer.getInstance();
    postgres.start();
  }
}
