package com.alg.jdbc_exercises.config.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Setter;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

@Setter
public class DBConnection {
    private String jdbcDriver, jdbcUrl, jdbcUsername, jdbcPassword;
    private final String jdbcFilename = "jdbc.properties";

    private HikariConfig config;

    public DBConnection() throws IOException {

        var inputStream = DBConnection.class.getClassLoader()
                .getResourceAsStream("resources/" + jdbcFilename);

        if (inputStream == null) {
            throw new FileNotFoundException("Unable to find properties file");
        }

        Properties properties = new Properties();
        properties.load(inputStream);

        this.jdbcDriver = properties.getProperty("jdbc.driver");
        this.jdbcUrl = properties.getProperty("jdbc.url");
        this.jdbcUsername = properties.getProperty("jdbc.username");
        this.jdbcPassword = properties.getProperty("jdbc.password");
    }

    public DBConnection(String jdbcDriver, String jdbcUrl, String jdbcUsername, String jdbcPassword) {
        this.jdbcDriver = jdbcDriver;
        this.jdbcUrl = jdbcUrl;
        this.jdbcUsername = jdbcUsername;
        this.jdbcPassword = jdbcPassword;
    }

    private void createHikariConfig() {
        this.config = new HikariConfig();

        config.setJdbcUrl(jdbcUrl);
        config.setUsername(jdbcUsername);
        config.setPassword(jdbcPassword);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        config.setAutoCommit(false);
    }

    public DataSource getDataSource() {
        // Create a datasource using jdbc properties
        var dataSource = new PGSimpleDataSource();
        dataSource.setURL(jdbcUrl);
        dataSource.setUser(jdbcUsername);
        dataSource.setPassword(jdbcPassword);

        return dataSource;
    }

    private DataSource getHikariDataSource() {
        // Create a hikari datasource using jdbc properties
        if (this.config == null) {
            createHikariConfig();
        }

        return new HikariDataSource(config);
    }

    public DataSource getHikariDataSourceWithLog() {
        if (this.config == null) {
            createHikariConfig();
        }

        // get hikari datasource
        var hikariDataSource = getHikariDataSource();

        // setup proxy using hikari datasource
        return ProxyDataSourceBuilder.create(hikariDataSource)
                .logQueryBySlf4j(SLF4JLogLevel.INFO)
                .countQuery()
                .listener(QueryExecutionListener.DEFAULT)
                .build();
    }
}
