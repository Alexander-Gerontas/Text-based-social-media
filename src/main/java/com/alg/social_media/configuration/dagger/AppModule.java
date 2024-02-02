package com.alg.social_media.configuration.dagger;

import static com.alg.social_media.handler.GlobalControllerExceptionHandler.exceptionHandler;
import static com.alg.social_media.handler.GlobalControllerExceptionHandler.runtimeExceptionHandler;
import static org.modelmapper.convention.MatchingStrategies.STRICT;

import com.alg.social_media.adapter.out.persistance.AccountRepositoryImpl;
import com.alg.social_media.application.port.in.AccountService;
import com.alg.social_media.application.port.out.AccountRepository;
import com.alg.social_media.application.service.AccountServiceImpl;
import com.alg.social_media.domain.model.Account;
import com.alg.social_media.adapter.out.persistance.CommentRepositoryImpl;
import com.alg.social_media.application.port.out.CommentRepository;
import com.alg.social_media.application.service.CommentServiceImpl;
import com.alg.social_media.domain.model.Comment;
import com.alg.social_media.application.port.in.CommentService;
import com.alg.social_media.application.port.in.FollowService;
import com.alg.social_media.application.service.FollowServiceImpl;
import com.alg.social_media.configuration.database.DBConfiguration;
import com.alg.social_media.configuration.database.FlywayConfiguration;
import com.alg.social_media.configuration.database.JpaEntityManagerFactory;
import com.alg.social_media.converters.AccountConverter;
import com.alg.social_media.converters.CommentConverter;
import com.alg.social_media.converters.PostConverter;
import com.alg.social_media.domain.model.Follow;
import com.alg.social_media.application.port.out.FollowRepository;
import com.alg.social_media.adapter.out.persistance.FollowRepositoryImpl;
import com.alg.social_media.application.port.in.PostService;
import com.alg.social_media.application.port.out.PostRepository;
import com.alg.social_media.adapter.out.persistance.PostRepositoryImpl;
import com.alg.social_media.application.service.PostServiceImpl;
import com.alg.social_media.domain.model.Post;
import com.alg.social_media.security.CustomAccessManager;
import com.alg.social_media.utils.DBUtils;
import com.alg.social_media.utils.ServiceInvocationHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dagger.Module;
import dagger.Provides;
import io.javalin.Javalin;
import javax.inject.Singleton;
import javax.sql.DataSource;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Module
public class AppModule {
	private DBConfiguration dbConfiguration;

	public AppModule() {}

	@Provides
	@Singleton
	public Javalin provideJavalin(CustomAccessManager accessManager) {
		var app = Javalin.create(config -> {
			config.accessManager(accessManager);
			config.http.asyncTimeout = 0L;
		});

		app.exception(Exception.class, exceptionHandler);
		app.exception(RuntimeException.class, runtimeExceptionHandler);

		return app;
	}

	@Provides
	@Singleton
	public CustomAccessManager provideCustomAccessManager() {
		return new CustomAccessManager();
	}

	@Provides
	@Singleton
	public JpaEntityManagerFactory provideJpaEntityManagerFactory(DBConfiguration dbConfiguration) {
		return new JpaEntityManagerFactory(dbConfiguration, new Class[]{
				Account.class,
				Follow.class,
				Post.class,
				Comment.class,
		});
	}

	@Provides
	@Singleton
	public DBConfiguration provideDBConnection() {
		if (dbConfiguration == null) {
			this.dbConfiguration = new DBConfiguration();
		}

		return dbConfiguration;
	}

	@Provides
	@Singleton
	public FlywayConfiguration provideLiquibaseConfiguration(final DataSource dataSource) {
		return new FlywayConfiguration(dataSource);
	}

	@Provides
	@Singleton
	public DataSource provideDataSource(DBConfiguration dbConfiguration) {
		return dbConfiguration.getHikariDataSource();
	}

	@Provides
	@Singleton
	public ObjectMapper provideObjectMapper() {
		var objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		return objectMapper;
	}

	@Provides
	@Singleton
	public AccountRepository provideAccountRepository() {
    return new AccountRepositoryImpl();
	}

	@Provides
	@Singleton
	public CommentRepository provideCommentRepository() {
		return new CommentRepositoryImpl();
	}

	@Provides
	@Singleton
	public FollowRepository provideFollowRepository() {
		return new FollowRepositoryImpl();
	}

	@Provides
	@Singleton
	public PostRepository providePostRepository() {
		return new PostRepositoryImpl();
	}

	@Provides
	@Singleton
	public AccountService provideAccountService(final AccountRepository accountRepository, final
			AccountConverter accountConverter) {
    AccountService accountService = new AccountServiceImpl(accountRepository, accountConverter);

    return ServiceInvocationHandler.wrap(accountService);
	}

	@Provides
	@Singleton
	public CommentService provideCommentService(final CommentRepository commentRepository,
			final CommentConverter commentConverter, final PostService postService,
			final AccountService accountService) {
		CommentService commentService = new CommentServiceImpl(commentRepository, commentConverter,
				postService,
				accountService);

		return ServiceInvocationHandler.wrap(commentService);
	}

	@Provides
	@Singleton
	public FollowService provideFollowService(final FollowRepository followRepository,
			final AccountService accountService, final AccountConverter accountConverter) {
    FollowService followService = new FollowServiceImpl(followRepository, accountService, accountConverter);
    return ServiceInvocationHandler.wrap(followService);
	}

	@Provides
	@Singleton
	public PostService providePostService(final PostRepository postRepository,
			final PostConverter postConverter, final AccountService accountService) {
		PostService postService = new PostServiceImpl(postRepository, postConverter, accountService);
		return ServiceInvocationHandler.wrap(postService);
	}

	@Provides
	@Singleton
	public BCryptPasswordEncoder providePasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Provides
	@Singleton
	public DBUtils provideDBUtils(final JpaEntityManagerFactory jpaEntityManagerFactory) {
		return new DBUtils(jpaEntityManagerFactory);
	}
	/**
	 * Bean for model mapper with matching strategy to strict mode.
	 *
	 * @return the ModelMapper.
	 */
	@Provides
	@Singleton
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(STRICT);
		return modelMapper;
	}

	@Provides
	@Singleton
	public PostConverter postConverter(ModelMapper modelMapper) {
		return new PostConverter(modelMapper);
	}

	@Provides
	@Singleton
	public CommentConverter commentConverter(ModelMapper modelMapper) {
		return new CommentConverter(modelMapper);
	}
}
