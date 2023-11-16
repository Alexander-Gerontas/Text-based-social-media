package com.alg.jdbc_exercises.config.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;

import java.util.*;
import java.util.stream.Collectors;

public class JpaEntityManagerFactory {
    private final Class[] entityClasses;

    private final DBConnection dbConnection;

    private final Properties properties;

    public JpaEntityManagerFactory(DBConnection dbConnection, Class[] entityClasses ) {
        this.dbConnection = dbConnection;
        this.entityClasses = entityClasses;

        this.properties = new Properties();
        properties.put("hibernate.connection.autocommit", false);
        properties.put("hibernate.connection.provider_disables_autocommit", true);
        properties.put("hibernate.id.new_generator_mappings", false);
        properties.put("hibernate.jdbc.batch_size", 5);
        properties.put("hibernate.connection.datasource", dbConnection.getHikariDataSourceWithLog());
    }

    public EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    private EntityManagerFactory getEntityManagerFactory() {

        var persistenceUnitInfo = new PersistenceUnitInfoImpl(getClass().getSimpleName(),
                this.getEntityClassNames(), this.properties);

        Map<String, Object> configuration = new HashMap<>();

        return new EntityManagerFactoryBuilderImpl(
                new PersistenceUnitInfoDescriptor(persistenceUnitInfo), configuration)
                .build();
    }

    private List<String> getEntityClassNames() {
        return Arrays.asList(entityClasses)
                .stream()
                .map(Class::getName)
                .collect(Collectors.toList());
    }
}
