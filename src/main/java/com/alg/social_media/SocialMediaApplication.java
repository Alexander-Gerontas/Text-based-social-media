package com.alg.social_media;

import com.alg.social_media.configuration.dagger.AppComponent;
import com.alg.social_media.configuration.dagger.DaggerAppComponent;
import io.javalin.Javalin;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SocialMediaApplication {
	public static void main(String[] args) {
		// Initialize Dagger component
    AppComponent appComponent = DaggerAppComponent.create();

		Javalin app = appComponent.buildJavalin();
		app.start(8080);
	}
}
