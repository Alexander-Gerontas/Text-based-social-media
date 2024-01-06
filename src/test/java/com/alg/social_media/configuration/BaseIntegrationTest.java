package com.alg.social_media.configuration;

import com.alg.social_media.configuration.dagger.AppComponent;
import com.alg.social_media.configuration.dagger.DaggerAppComponent;
import com.alg.social_media.configuration.database.DBConnection;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Properties;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class BaseIntegrationTest {

  public static final ObjectMapper objectMapper;
  public static final ConfiguredPostgresContainer postgres;
  public static final AppComponent appComponent;
  public static final Properties properties;

  static {
    postgres = ConfiguredPostgresContainer.getInstance();
    postgres.start();

    var jdbcDriver = postgres.getDriverClassName();
    var jdbcUrl = postgres.getJdbcUrl();
    var jdbcUsername = postgres.getUsername();
    var jdbcPassword = postgres.getPassword();

    properties = new Properties();

    properties.setProperty("jdbc.driver", jdbcDriver);
    properties.setProperty("jdbc.url", jdbcUrl);
    properties.setProperty("jdbc.username", jdbcUsername);
    properties.setProperty("jdbc.password", jdbcPassword);

    DBConnection.properties = properties;

    appComponent = DaggerAppComponent.create();
    objectMapper = appComponent.buildObjectMapper();
  }
}
