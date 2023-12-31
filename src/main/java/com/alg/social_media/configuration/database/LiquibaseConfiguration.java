package com.alg.social_media.configuration.database;

import dagger.Module;
import java.sql.Connection;
import java.sql.SQLException;
import javax.inject.Inject;
import javax.sql.DataSource;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

@Module
public class LiquibaseConfiguration {

  private final DataSource dataSource;

  @Inject
  public LiquibaseConfiguration(DataSource dataSource) {
    this.dataSource = dataSource;
    runChangelog();
  }

  private void runChangelog() {

    try (Connection connection = dataSource.getConnection()) {
      Database database = DatabaseFactory.getInstance()
          .findCorrectDatabaseImplementation(new JdbcConnection(connection));

      // Create Liquibase instance
      Liquibase liquibase = new Liquibase("/changelog/db.changelog-master.xml",
          new ClassLoaderResourceAccessor(), database);

      // Update the database
      liquibase.update("");
    } catch (SQLException | LiquibaseException e) {
      throw new RuntimeException(e);
    }
  }
}
