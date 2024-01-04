package com.alg.social_media.configuration;

import static com.alg.social_media.controllers.RegistrationController.registrationHandler;
import static com.alg.social_media.handler.GlobalControllerExceptionHandler.exceptionHandler;
import static com.alg.social_media.handler.GlobalControllerExceptionHandler.handleAccountExists;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;
import static org.modelmapper.convention.MatchingStrategies.STRICT;

import com.alg.social_media.exceptions.AccountExistsException;
import com.alg.social_media.objects.Account;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.javalin.Javalin;
import java.io.IOException;
import javax.sql.DataSource;
import org.modelmapper.ModelMapper;

public class GuiceModule extends AbstractModule {
	private final DBConnection dbConnection;
	private JpaEntityManagerFactory jpaEntityManagerFactory;

	// todo remove
	private LiquibaseConfiguration liquibaseConfiguration;

	public GuiceModule(DBConnection dbConnection) throws IOException {
		this.dbConnection = dbConnection;
		initDependencies();
	}

	public void initDependencies() {
		this.jpaEntityManagerFactory = new JpaEntityManagerFactory(dbConnection, new Class[]{
				Account.class
		});

		// todo enable
		this.liquibaseConfiguration = new LiquibaseConfiguration(dbConnection.getHikariDataSource());
	}

	@Override
	protected void configure() {
	}

	@Provides
	public Javalin provideJavalin() {
		var app = Javalin.create(config -> {
					config.http.asyncTimeout = 0L;
				})
				.routes(() -> {
					get("/hello", ctx -> ctx.html("Hello, Javalin!"));

					// registration
					post("/api/v1/account/registration", registrationHandler);
				});

		app.exception(Exception.class, exceptionHandler);
		app.exception(AccountExistsException.class, handleAccountExists);

		return app;
	}

	@Provides
	public DataSource provideDataSource() {
		return dbConnection.getHikariDataSource();
	}

	@Provides
	public JpaEntityManagerFactory provideJpaEntityManagerFactory() {
		return this.jpaEntityManagerFactory;
	}

	@Provides
	public ObjectMapper provideObjectMapper() {
		return new ObjectMapper();
	}

//	@Provides
//	public LiquibaseConfiguration provideLiquidbaseConfiguration() {
//		return liquibaseConfiguration;
//	}

	@Provides
	public LiquibaseConfiguration provideLiquidbaseConfiguration(DataSource dataSource) {
		return new LiquibaseConfiguration(dataSource);
	}

	/**
	 * Bean for model mapper with matching strategy to strict mode.
	 *
	 * @return the ModelMapper.
	 */
    @Provides
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(STRICT);
        return modelMapper;
    }
}
