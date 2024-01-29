package com.alg.social_media.configuration.database;

import dagger.Module;
import javax.inject.Inject;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;

@Module
public class FlywayConfiguration {

  private final DataSource dataSource;

  @Inject
  public FlywayConfiguration(final DataSource dataSource) {
    this.dataSource = dataSource;
    runChangelog();
  }

  private void runChangelog() {
    // Create Flyway instance
    Flyway flyway = Flyway.configure()
        .dataSource(dataSource)
        .load();

    // Start the migration
    flyway.migrate();
  }
}
