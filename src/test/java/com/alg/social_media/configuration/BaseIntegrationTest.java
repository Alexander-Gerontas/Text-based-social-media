package com.alg.social_media.configuration;

import java.util.Properties;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class BaseIntegrationTest {

//  @Autowired public ObjectMapper objectMapper;
  public static final ConfiguredPostgresContainer postgres;
  public static final DBConnection dbConnection;

  static {
    postgres = ConfiguredPostgresContainer.getInstance();
    postgres.start();

    var jdbcDriver = postgres.getDriverClassName();
    var jdbcUrl = postgres.getJdbcUrl();
    var jdbcUsername = postgres.getUsername();
    var jdbcPassword = postgres.getPassword();

    var properties = new Properties();

    properties.setProperty("jdbc.driver", jdbcDriver);
    properties.setProperty("jdbc.url", jdbcUrl);
    properties.setProperty("jdbc.username", jdbcUsername);
    properties.setProperty("jdbc.password", jdbcPassword);

    dbConnection = new DBConnection(properties);
  }
}
