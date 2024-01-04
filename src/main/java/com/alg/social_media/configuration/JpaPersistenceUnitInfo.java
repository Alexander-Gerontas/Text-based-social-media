package com.alg.social_media.configuration;

import jakarta.persistence.SharedCacheMode;
import jakarta.persistence.ValidationMode;
import jakarta.persistence.spi.ClassTransformer;
import jakarta.persistence.spi.PersistenceUnitInfo;
import jakarta.persistence.spi.PersistenceUnitTransactionType;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import javax.sql.DataSource;
import org.hibernate.jpa.HibernatePersistenceProvider;

public class JpaPersistenceUnitInfo implements PersistenceUnitInfo {

	public static String JPA_VERSION = "2.1";
	private String persistenceUnitName;
	private PersistenceUnitTransactionType transactionType
			= PersistenceUnitTransactionType.RESOURCE_LOCAL;
	private List<String> managedClassNames;
	private List<String> mappingFileNames = new ArrayList<>();
	private Properties properties;
	private DataSource jtaDataSource;
	private DataSource nonjtaDataSource;
	private List<ClassTransformer> transformers = new ArrayList<>();

	public JpaPersistenceUnitInfo(String persistenceUnitName, List<String> managedClassNames, Properties properties) {
		this.persistenceUnitName = persistenceUnitName;
		this.managedClassNames = managedClassNames;
		this.properties = properties;
	}

	@Override
	public String getPersistenceUnitName() {
		return this.persistenceUnitName;
	}

	@Override
	public String getPersistenceProviderClassName() {
		return HibernatePersistenceProvider.class.getName();
	}

	@Override
	public PersistenceUnitTransactionType getTransactionType() {
		return this.transactionType;
	}

	@Override
	public DataSource getJtaDataSource() {
		return jtaDataSource;
	}

	@Override
	public DataSource getNonJtaDataSource() {
		return this.nonjtaDataSource;
	}

	@Override
	public List<String> getMappingFileNames() {
		return this.mappingFileNames;
	}

	@Override
	public List<URL> getJarFileUrls() {
		return Collections.emptyList();
	}

	@Override
	public URL getPersistenceUnitRootUrl() {
		return null;
	}

	@Override
	public List<String> getManagedClassNames() {
		return this.managedClassNames;
	}

	@Override
	public boolean excludeUnlistedClasses() {
		return false;
	}

	@Override
	public SharedCacheMode getSharedCacheMode() {
		return SharedCacheMode.UNSPECIFIED;
	}

	@Override
	public ValidationMode getValidationMode() {
		return ValidationMode.AUTO;
	}

	@Override
	public Properties getProperties() {
		return this.properties;
	}

	@Override
	public String getPersistenceXMLSchemaVersion() {
		return JPA_VERSION;
	}

	@Override
	public ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	@Override
	public void addTransformer(ClassTransformer transformer) {
		this.transformers.add(transformer);
	}

	@Override
	public ClassLoader getNewTempClassLoader() {
		return null;
	}
}
