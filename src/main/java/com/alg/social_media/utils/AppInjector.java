package com.alg.social_media.utils;

import com.alg.social_media.configuration.DBConnection;
import com.alg.social_media.configuration.GuiceModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.io.IOException;

public class AppInjector {
	private static Injector injector;
	private static DBConnection dbConnection;

	public static void setConnection(DBConnection dbConnection) {
		AppInjector.dbConnection = dbConnection;
	}

	public static Injector getInjector() throws IOException {
		if (injector == null) {
			injector = Guice.createInjector(
					new GuiceModule(dbConnection)
			);
		}
		return injector;
	}
}
