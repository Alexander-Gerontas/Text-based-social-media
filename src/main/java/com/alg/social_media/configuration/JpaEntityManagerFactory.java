package com.alg.social_media.configuration;

import com.alg.social_media.utils.AppInjector;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;

public class JpaEntityManagerFactory {

	private final Class[] entityClasses;

	private final DBConnection dbConnection;

	private final Properties properties;

//	private final LiquibaseConfiguration liquibaseConfiguration;

	public JpaEntityManagerFactory(DBConnection dbConnection, Class[] entityClasses ) {
		this.dbConnection = dbConnection;
		this.entityClasses = entityClasses;

//		liquibaseConfiguration = AppInjector.getInjector().getInstance(LiquibaseConfiguration.class);

		this.properties = new Properties();
		properties.put("hibernate.connection.autocommit", false);
		properties.put("hibernate.connection.provider_disables_autocommit", true);
		properties.put("hibernate.id.new_generator_mappings", false);
		properties.put("hibernate.jdbc.batch_size", 5);
		properties.put("hibernate.connection.datasource", dbConnection.getHikariDataSource());
	}

	public EntityManager getEntityManager() {
		return getEntityManagerFactory().createEntityManager();
	}

	private EntityManagerFactory getEntityManagerFactory() {

		var persistenceUnitInfo = new JpaPersistenceUnitInfo(getClass().getSimpleName(),
				this.getEntityClassNames(), this.properties);

		Map<String, Object> configuration = new HashMap<>();

		return new EntityManagerFactoryBuilderImpl(
				new PersistenceUnitInfoDescriptor(persistenceUnitInfo), configuration)
				.build();
	}

	private List<String> getEntityClassNames() {
		return Arrays.stream(entityClasses)
				.map(Class::getName)
				.toList();
	}
}
