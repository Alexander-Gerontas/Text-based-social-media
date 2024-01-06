package com.alg.social_media.configuration.dagger;

import static com.alg.social_media.handler.GlobalControllerExceptionHandler.exceptionHandler;
import static com.alg.social_media.handler.GlobalControllerExceptionHandler.handleAccountExists;
import static org.modelmapper.convention.MatchingStrategies.STRICT;

import com.alg.social_media.configuration.database.DBConnection;
import com.alg.social_media.configuration.database.JpaEntityManagerFactory;
import com.alg.social_media.configuration.database.LiquibaseConfiguration;
import com.alg.social_media.exceptions.AccountExistsException;
import com.alg.social_media.objects.Account;
import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import io.javalin.Javalin;
import javax.inject.Singleton;
import javax.sql.DataSource;
import org.modelmapper.ModelMapper;

@Module
public class AppModule {
	private DBConnection dbConnection;

	public AppModule() {}

	@Provides
	@Singleton
	public Javalin provideJavalin() {
		var app = Javalin.create(config -> {
					config.http.asyncTimeout = 0L;
				})
				.routes(() -> {});

		app.exception(Exception.class, exceptionHandler);
		app.exception(AccountExistsException.class, handleAccountExists);

		return app;
	}

	@Provides
	@Singleton
	public JpaEntityManagerFactory provideJpaEntityManagerFactory(DBConnection dbConnection) {
		return new JpaEntityManagerFactory(dbConnection, new Class[]{
				Account.class
		});
	}

	@Provides
	@Singleton
	public DBConnection provideDBConnection() {
		if (dbConnection == null) {
			this.dbConnection = new DBConnection();
		}

		return dbConnection;
	}

	@Provides
	@Singleton
	public LiquibaseConfiguration provideLiquibaseConfiguration(DataSource dataSource) {
		return new LiquibaseConfiguration(dataSource);
	}

	@Provides
	@Singleton
	public DataSource provideDataSource(DBConnection dbConnection) {
		return dbConnection.getHikariDataSource();
	}

	@Provides
	@Singleton
	public ObjectMapper provideObjectMapper() {
		return new ObjectMapper();
	}

	@Provides
	@Singleton
	/**
	 * Bean for model mapper with matching strategy to strict mode.
	 *
	 * @return the ModelMapper.
	 */
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(STRICT);
		return modelMapper;
	}
}
