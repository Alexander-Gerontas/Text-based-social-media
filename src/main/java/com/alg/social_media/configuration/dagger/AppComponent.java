package com.alg.social_media.configuration.dagger;

import com.alg.social_media.configuration.database.DBConnection;
import com.alg.social_media.configuration.database.LiquibaseConfiguration;
import com.alg.social_media.configuration.security.CustomAccessManager;
import com.alg.social_media.configuration.security.SecurityMiddleware;
import com.alg.social_media.controllers.FollowController;
import com.alg.social_media.controllers.PostController;
import com.alg.social_media.controllers.RegistrationController;
import com.alg.social_media.converters.AccountConverter;
import com.alg.social_media.converters.PostConverter;
import com.alg.social_media.repository.AccountRepository;
import com.alg.social_media.repository.CommentRepository;
import com.alg.social_media.repository.FollowRepository;
import com.alg.social_media.repository.PostRepository;
import com.alg.social_media.service.AccountService;
import com.alg.social_media.utils.DBUtils;
import com.alg.social_media.utils.PasswordEncoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Component;
import io.javalin.Javalin;
import javax.inject.Singleton;
import org.modelmapper.ModelMapper;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
  Javalin buildJavalin();
  CustomAccessManager buildCustomAccessManager();
  SecurityMiddleware buildSecurityMiddleware();

  // converters
  AccountConverter buildAccountConverter();
  PostConverter buildPostConverter();

  // repositories
  PostRepository buildPostRepository();
  CommentRepository buildCommentRepository();
  AccountRepository buildAccountRepository();
  FollowRepository buildFollowRepository();

  // services
  AccountService buildAccountService();
  // controllers
  RegistrationController buildRegistrationController();
  PostController buildPostController();
  FollowController buildFollowController();

  DBConnection buildDBConnection();
  DBUtils buildDBUtils();
  LiquibaseConfiguration buildLiquibaseConfiguration();
  ObjectMapper buildObjectMapper();
  ModelMapper buildModelMapper();
  PasswordEncoder buildPasswordEncoder();
}
