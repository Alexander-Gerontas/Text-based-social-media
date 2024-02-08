package com.alg.social_media.configuration;

import com.alg.social_media.SocialMediaApplication;
import com.alg.social_media.configuration.dagger.AppComponent;
import com.alg.social_media.configuration.dagger.DaggerAppComponent;
import com.alg.social_media.configuration.database.DBConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import java.util.Properties;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class BaseIntegrationTest {
  public static final ObjectMapper objectMapper;
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

    DBConfiguration.properties = properties;

    SocialMediaApplication.main(new String[] {String.valueOf(port)});

    // initialize dagger components
    appComponent = DaggerAppComponent.create();

    objectMapper = appComponent.buildObjectMapper();

    // Set up the base URI and port of your application
    RestAssured.baseURI = "http://localhost/";
    RestAssured.port = port; // Set your port
  }

  protected static void resetRestAssured() {
    RestAssured.reset();

    RestAssured.useRelaxedHTTPSValidation();

    // Set up the base URI and port of your application
    RestAssured.baseURI = "http://localhost/";
    RestAssured.port = port; // Set your port
  }
}
