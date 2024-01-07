package com.alg.social_media.configuration.database;

import com.alg.social_media.SocialMediaApplication;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import java.util.Properties;
import javax.sql.DataSource;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;

public class DBConnection {

	public static Properties properties;
	private final String jdbcDriver, jdbcUrl, jdbcUsername, jdbcPassword;
	private final HikariConfig config;
	private final String jdbcFilename = "jdbc.properties";

	public DBConnection() {
		if (properties == null) {
			readProperties();
		}

		// initialize postgress db
		this.jdbcDriver = properties.getProperty("jdbc.driver");
		this.jdbcUrl = properties.getProperty("jdbc.url");
		this.jdbcUsername = properties.getProperty("jdbc.username");
		this.jdbcPassword = properties.getProperty("jdbc.password");

		// setup a hikari datasource using jdbc properties
		this.config = new HikariConfig();

		config.setJdbcUrl(jdbcUrl);
		config.setUsername(jdbcUsername);
		config.setPassword(jdbcPassword);
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

		config.setAutoCommit(false);
	}

	public DataSource getHikariDataSource() {
		// get hikari datasource
		var hikariDataSource = new HikariDataSource(config);

		// setup proxy using hikari datasource
		return ProxyDataSourceBuilder.create(hikariDataSource)
				.logQueryBySlf4j(SLF4JLogLevel.INFO)
				.countQuery()
				.listener(QueryExecutionListener.DEFAULT)
				.build();
	}

	private void readProperties() {
		var inputStream = SocialMediaApplication.class.getClassLoader()
				.getResourceAsStream(jdbcFilename);

		if (inputStream == null) {
			throw new RuntimeException("Unable to find database config file");
		}

		this.properties = new Properties();

		try {
			properties.load(inputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
