package com.alg.jdbc_exercises;

import com.alg.jdbc_exercises.config.database.DBConnection;

import java.io.IOException;

public class SocialMediaApplication {
	public static void main(String[] args) throws IOException {

		var dbConnection = new DBConnection();
		dbConnection.getDataSource();
	}
}
