package com.alg.social_media.configuration.dagger;

import com.alg.social_media.configuration.database.DBConnection;
import com.alg.social_media.configuration.database.LiquibaseConfiguration;
import com.alg.social_media.configuration.security.SecurityMiddleware;
import com.alg.social_media.controllers.RegistrationController;
import com.alg.social_media.converters.AccountConverter;
import com.alg.social_media.repository.AccountRepository;
import com.alg.social_media.service.AccountService;
import com.alg.social_media.utils.DBUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Component;
import io.javalin.Javalin;
import javax.inject.Singleton;
import org.modelmapper.ModelMapper;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
  Javalin buildJavalin();
  SecurityMiddleware buildSecurityMiddleware();
  AccountConverter buildAccountConverter();
  AccountRepository buildAccountRepository();
  AccountService buildAccountService();
  RegistrationController buildRegistrationController();
  DBConnection buildDBConnection();
  DBUtils buildDBUtils();
  LiquibaseConfiguration buildLiquibaseConfiguration();
  ObjectMapper buildObjectMapper();
  ModelMapper buildModelMapper();
}
