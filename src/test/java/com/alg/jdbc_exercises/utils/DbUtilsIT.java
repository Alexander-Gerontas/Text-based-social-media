package com.alg.jdbc_exercises.utils;

import com.alg.jdbc_exercises.config.database.JpaEntityManagerFactory;
import com.alg.jdbc_exercises.configuration.BaseIntegrationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.TestInstance;

import javax.sql.DataSource;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DbUtilsIT extends BaseIntegrationTest {

  private DbUtils dbUtils;
  private DataSource dataSource;
  private JpaEntityManagerFactory jpaEntityManagerFactory;
  private final String jdbcDriver, jdbcUrl, jdbcUsername, jdbcPassword;

  public DbUtilsIT() {
    this.jdbcDriver = postgres.getDriverClassName();
    this.jdbcUrl = postgres.getJdbcUrl();
    this.jdbcUsername = postgres.getUsername();
    this.jdbcPassword = postgres.getPassword();
  }
}