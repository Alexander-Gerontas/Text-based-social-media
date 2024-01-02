package com.alg.social_media;

import com.alg.social_media.configuration.GuiceModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.javalin.Javalin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor // todo remove
public class SocialMediaApplication {
	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new GuiceModule());
		Javalin app = injector.getInstance(Javalin.class);

		app.start(8080);
	}
}
