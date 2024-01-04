package com.alg.social_media;

import com.alg.social_media.configuration.DBConnection;
import com.alg.social_media.controllers.RegistrationController;
import com.alg.social_media.utils.AppInjector;
import io.javalin.Javalin;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SocialMediaApplication {
	public static void main(String[] args) throws IOException {

		var dbConn = new DBConnection(getProperties());
		AppInjector.setConnection(dbConn);

		// todo remove
		RegistrationController.setInjector(AppInjector.getInjector());

		Javalin app = AppInjector.getInjector().getInstance(Javalin.class);
		app.start(8080);
	}

	private static Properties getProperties() throws IOException {

		final String jdbcFilename = "jdbc.properties";

		var inputStream = SocialMediaApplication.class.getClassLoader()

				.getResourceAsStream(jdbcFilename);

		if (inputStream == null) {
			throw new FileNotFoundException("Unable to find database config file");
		}

		Properties properties = new Properties();
		properties.load(inputStream);

		return properties;
	}
}
