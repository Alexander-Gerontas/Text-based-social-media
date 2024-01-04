package com.alg.social_media.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.Properties;
import javax.sql.DataSource;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;

public class DBConnection {
	private String jdbcDriver, jdbcUrl, jdbcUsername, jdbcPassword;
	private final String jdbcFilename = "jdbc.properties";
	private HikariConfig config;

	// todo remove
	/*public DBConnection() throws IOException {

		var inputStream = DBConnection.class.getClassLoader()
				.getResourceAsStream("resources/" + jdbcFilename);

		if (inputStream == null) {
			throw new FileNotFoundException("Unable to find database config file");
		}

		Properties properties = new Properties();
		properties.load(inputStream);

		initDb(properties);
	}*/

	public DBConnection(Properties properties) {
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
}
