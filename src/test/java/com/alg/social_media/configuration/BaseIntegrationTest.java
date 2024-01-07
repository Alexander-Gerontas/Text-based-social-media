package com.alg.social_media.configuration;

import com.alg.social_media.configuration.dagger.AppComponent;
import com.alg.social_media.configuration.dagger.DaggerAppComponent;
import com.alg.social_media.configuration.database.DBConnection;
import com.alg.social_media.configuration.database.LiquibaseConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import java.util.Properties;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class BaseIntegrationTest {
  public static final Javalin app;
  public static final ObjectMapper objectMapper;
  public static final DBConnection dbConnection;
  public static final LiquibaseConfiguration liquibaseConfiguration;
  public static final ConfiguredPostgresContainer postgres;
  public static final AppComponent appComponent;
  public static final Properties properties;
  public static final int port = 8080;

  static {
    // start container db
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

    // initialize dagger components
    appComponent = DaggerAppComponent.create();

    appComponent.buildSecurityMiddleware();
    app = appComponent.buildJavalin();
    objectMapper = appComponent.buildObjectMapper();

    dbConnection = appComponent.buildDBConnection();
    liquibaseConfiguration = appComponent.buildLiquibaseConfiguration();

    // Set up the base URI and port of your application
    RestAssured.baseURI = "http://localhost/api/v1";
    RestAssured.port = port; // Set your port

    // start javalin server
    app.start(port);
  }
}
