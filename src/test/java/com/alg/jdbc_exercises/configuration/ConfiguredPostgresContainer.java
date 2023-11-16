package com.alg.jdbc_exercises.configuration;

import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Pre-configured Testcontainer for Postgres DB.
 */
public class ConfiguredPostgresContainer
        extends PostgreSQLContainer<ConfiguredPostgresContainer> {

  private static final String IMAGE_VERSION = "postgres:15.3-alpine";

  private static ConfiguredPostgresContainer configuredPostgresContainer;

  ConfiguredPostgresContainer() {
    super(IMAGE_VERSION);
  }

  /**
   * Get container instance.
   *
   * @return container instance.
   */
  public static ConfiguredPostgresContainer getInstance() {
    if (configuredPostgresContainer == null) {
      configuredPostgresContainer = new ConfiguredPostgresContainer();
    }

    return configuredPostgresContainer;
  }
}
