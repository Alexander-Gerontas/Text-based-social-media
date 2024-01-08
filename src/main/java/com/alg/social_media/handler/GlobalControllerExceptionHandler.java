package com.alg.social_media.handler;

import com.alg.social_media.exceptions.AccountDoesNotExistException;
import com.alg.social_media.exceptions.AccountExistsException;
import com.alg.social_media.exceptions.WrongPasswordException;
import io.javalin.http.ExceptionHandler;
import io.javalin.http.HttpStatus;

public class GlobalControllerExceptionHandler {
    public static final ExceptionHandler<AccountExistsException> handleAccountExists = (e, ctx) -> {
        ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
    };

    public static final ExceptionHandler<AccountDoesNotExistException> handleAccountDoesNotExist = (e, ctx) -> {
        ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
    };

    public static final ExceptionHandler<WrongPasswordException> handleWrongPassword = (e, ctx) -> {
        ctx.status(HttpStatus.UNAUTHORIZED).result(e.getMessage());
    };

    public static final ExceptionHandler<Exception> exceptionHandler = (e, ctx) -> {
        // Handle exceptions here and provide appropriate response to the client
        e.printStackTrace(); // Log the exception for debugging purposes
        ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
    };
}
