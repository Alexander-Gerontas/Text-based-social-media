package com.alg.social_media.handler;

import com.alg.social_media.exceptions.AccountDoesNotExistException;
import com.alg.social_media.exceptions.AccountExistsException;
import com.alg.social_media.exceptions.InvalidTokenException;
import com.alg.social_media.exceptions.NoTokenProvidedException;
import com.alg.social_media.exceptions.PostException;
import com.alg.social_media.exceptions.SubscriptionException;
import com.alg.social_media.exceptions.WrongPasswordException;
import io.javalin.http.ExceptionHandler;
import io.javalin.http.HttpStatus;

public class GlobalControllerExceptionHandler {
  // Authentication Exceptions
  public static final ExceptionHandler<AccountExistsException> handleAccountExists = (e, ctx) ->
      ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());

  public static final ExceptionHandler<AccountDoesNotExistException> handleAccountDoesNotExist = (e, ctx) ->
      ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());

  public static final ExceptionHandler<WrongPasswordException> handleWrongPassword = (e, ctx) ->
      ctx.status(HttpStatus.UNAUTHORIZED).result(e.getMessage());

  public static final ExceptionHandler<NoTokenProvidedException> handleNoTokenProvided = (e, ctx) ->
      ctx.status(HttpStatus.UNAUTHORIZED).result(e.getMessage());

  public static final ExceptionHandler<InvalidTokenException> handleInvalidTokenProvided = (e, ctx) ->
      ctx.status(HttpStatus.UNAUTHORIZED).result(e.getMessage());

  // Subscription Exceptions
  public static final ExceptionHandler<SubscriptionException> handleSubscriptionException = (e, ctx) ->
      ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());

  public static final ExceptionHandler<PostException> handlePostException = (e, ctx) ->
      ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());

  // Generic Exceptions
  public static final ExceptionHandler<Exception> exceptionHandler = (e, ctx) ->
      ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());

  public static final ExceptionHandler<RuntimeException> runtimeExceptionHandler = (e, ctx) ->
      ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
}
