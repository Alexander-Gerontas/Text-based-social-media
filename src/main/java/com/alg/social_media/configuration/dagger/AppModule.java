package com.alg.social_media.configuration.dagger;

import static com.alg.social_media.handler.GlobalControllerExceptionHandler.exceptionHandler;
import static com.alg.social_media.handler.GlobalControllerExceptionHandler.runtimeExceptionHandler;
import static org.modelmapper.convention.MatchingStrategies.STRICT;

import com.alg.social_media.configuration.database.DBConnection;
import com.alg.social_media.configuration.database.JpaEntityManagerFactory;
import com.alg.social_media.configuration.database.LiquibaseConfiguration;
import com.alg.social_media.configuration.security.CustomAccessManager;
import com.alg.social_media.model.Comment;
import com.alg.social_media.model.Follow;
import com.alg.social_media.model.Post;
import com.alg.social_media.model.Account;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dagger.Module;
import dagger.Provides;
import io.javalin.Javalin;
import javax.inject.Singleton;
import javax.sql.DataSource;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.modelmapper.ModelMapper;

@Module
public class AppModule {
	private DBConnection dbConnection;

	public AppModule() {}

	@Provides
	@Singleton
	public Javalin provideJavalin(CustomAccessManager accessManager) {
		var app = Javalin.create(config -> {
			config.accessManager(accessManager);
			config.http.asyncTimeout = 0L;
		});

		app.exception(Exception.class, exceptionHandler);
		app.exception(RuntimeException.class, runtimeExceptionHandler);

		return app;
	}

	@Provides
	@Singleton
	public CustomAccessManager provideCustomAccessManager() {
		return new CustomAccessManager();
	}

	@Provides
	@Singleton
	public JpaEntityManagerFactory provideJpaEntityManagerFactory(DBConnection dbConnection) {
		return new JpaEntityManagerFactory(dbConnection, new Class[]{
				Account.class,
				Follow.class,
				Post.class,
				Comment.class,
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
		var objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		return objectMapper;
	}

	@Provides
	public StandardPBEStringEncryptor provideStandardPBEStringEncryptor() {
		return new StandardPBEStringEncryptor();
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
