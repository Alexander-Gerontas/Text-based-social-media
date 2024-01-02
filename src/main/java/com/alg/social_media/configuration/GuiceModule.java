package com.alg.social_media.configuration;

import static com.alg.social_media.controllers.JavalinRegistrationController.registrationHandler;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;

import com.alg.social_media.repository.AccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.javalin.Javalin;
import org.springframework.beans.factory.annotation.Autowired;

public class GuiceModule extends AbstractModule {

	@Autowired private AccountRepository accountRepository;

	@Provides
	public Javalin provideJavalin() {


		return Javalin.create()
				.routes(() -> {
					get("/hello", ctx -> ctx.html("Hello, Javalin!"));

					// registration
					post("/api/v1/account/registration", registrationHandler);
				});
	}

	@Provides
	public ObjectMapper provideObjectMapper() {
		return new ObjectMapper();
	}

	@Provides
	public AccountRepository provideAccountRepo() {
		return accountRepository;
	}

	// todo add maven
	/**
	 * Bean for model mapper with matching strategy to strict mode.
	 *
	 * @return the ModelMapper.
	 */
//    @Bean
//    public ModelMapper modelMapper() {
//        ModelMapper modelMapper = new ModelMapper();
//        modelMapper.getConfiguration().setMatchingStrategy(STRICT);
//        return modelMapper;
//    }

	@Override
	protected void configure() {
//		bind(Javalin.class).to(JavalinImpl.class);
	}
}
