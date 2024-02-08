package com.alg.social_media;

import com.alg.social_media.configuration.dagger.AppComponent;
import com.alg.social_media.configuration.dagger.DaggerAppComponent;
import com.alg.social_media.configuration.database.DBConfiguration;
import com.alg.social_media.configuration.database.FlywayConfiguration;
import io.javalin.Javalin;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class SocialMediaApplication {
	private static DBConfiguration dbConfiguration;
	private static FlywayConfiguration flywayConfiguration;
	private static int port = 8080;

	public static void main(String[] args) {

		// set app port or default to 8080
		if (args[0] != null && StringUtils.isNumeric(args[0])) {
			port = Integer.parseInt(args[0]);
		}

		// Initialize Dagger component
    AppComponent appComponent = DaggerAppComponent.create();

		// secure app
		appComponent.buildSecurityMiddleware();

		// initialize database
		appComponent.buildDBUtils();
		dbConfiguration = appComponent.buildDBConnection();
		flywayConfiguration = appComponent.buildLiquibaseConfiguration();

		// initialize controllers
		appComponent.buildPostController();
		appComponent.buildCommentController();
		appComponent.buildRegistrationController();
		appComponent.buildFollowController();

		// start javalin server
		Javalin app = appComponent.buildJavalin();
		app.start(port);
	}
}
