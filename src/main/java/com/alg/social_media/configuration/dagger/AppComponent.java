package com.alg.social_media.configuration.dagger;

import com.alg.social_media.account.adapter.application.port.in.AccountService;
import com.alg.social_media.account.adapter.application.port.out.AccountRepository;
import com.alg.social_media.account.adapter.application.port.out.CommentRepository;
import com.alg.social_media.account.adapter.application.port.out.FollowRepository;
import com.alg.social_media.account.adapter.application.port.out.PostRepository;
import com.alg.social_media.account.adapter.in.web.AccountController;
import com.alg.social_media.account.adapter.in.web.CommentController;
import com.alg.social_media.account.adapter.in.web.FollowController;
import com.alg.social_media.account.adapter.in.web.PostController;
import com.alg.social_media.configuration.converters.AccountConverter;
import com.alg.social_media.configuration.converters.PostConverter;
import com.alg.social_media.configuration.database.DBConfiguration;
import com.alg.social_media.configuration.database.FlywayConfiguration;
import com.alg.social_media.configuration.security.CustomAccessManager;
import com.alg.social_media.configuration.security.SecurityMiddleware;
import com.alg.social_media.utils.DBUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Component;
import io.javalin.Javalin;
import javax.inject.Singleton;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
  AccountController buildRegistrationController();
  PostController buildPostController();
  CommentController buildCommentController();
  FollowController buildFollowController();

  DBConfiguration buildDBConnection();
  DBUtils buildDBUtils();
  FlywayConfiguration buildLiquibaseConfiguration();
  ObjectMapper buildObjectMapper();
  ModelMapper buildModelMapper();
  BCryptPasswordEncoder buildBCryptPasswordEncoder();
}
