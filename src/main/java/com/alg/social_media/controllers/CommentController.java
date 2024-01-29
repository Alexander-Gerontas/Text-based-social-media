package com.alg.social_media.controllers;

import static com.alg.social_media.configuration.security.SecurityMiddleware.authenticationHandler;
import static com.alg.social_media.constants.ControllerArgs.PAGE;
import static com.alg.social_media.constants.ControllerArgs.PAGE_SIZE;
import static com.alg.social_media.constants.Keywords.USERNAME;
import static com.alg.social_media.constants.Paths.COMMENT_URI;
import static com.alg.social_media.constants.Paths.MY_FOLLOWERS_POST_COMMENTS_URI;
import static com.alg.social_media.constants.Paths.MY_POST_COMMENTS_URI;

import com.alg.social_media.dto.post.CommentResponseDto;
import com.alg.social_media.enums.AccountType;
import com.alg.social_media.service.CommentService;
import io.javalin.Javalin;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import java.util.List;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommentController {
    private final Javalin app;
    private final CommentService commentService;

    @Inject
    public CommentController(final Javalin app, final CommentService commentService) {
        this.app = app;
        this.commentService = commentService;

        configureRoutes();
    }

    private void configureRoutes() {
        // set authentication
        app.before(COMMENT_URI + "*", authenticationHandler());
        app.before(COMMENT_URI + "/*", authenticationHandler());

        // get latest comments from a user's posts
        app.get(MY_POST_COMMENTS_URI, getAccountCommentsHandler(), AccountType.FREE, AccountType.PREMIUM);

        // get latest comments from a user's and his follower's posts
        app.get(MY_FOLLOWERS_POST_COMMENTS_URI, getFollowerPostCommentsHandler(), AccountType.FREE, AccountType.PREMIUM);
    }

    private Handler getAccountCommentsHandler() {
        return ctx -> {
            String username = ctx.attribute(USERNAME);
            String pageParam = ctx.queryParam(PAGE);
            String pageSizeParam = ctx.queryParam(PAGE_SIZE);

            int page = (pageParam != null) ? Integer.parseInt(pageParam) : 0;
            int pageSize = (pageSizeParam != null) ? Integer.parseInt(pageSizeParam) : 100;

            log.info("User: " + username + " wants to see the latest comments on his posts");

            List<CommentResponseDto> accountComments = commentService.getAccountPostComments(username, page, pageSize);

            // Send the response
            ctx.json(accountComments);
            ctx.status(HttpStatus.OK);
        };
    }

    private Handler getFollowerPostCommentsHandler() {
        return ctx -> {
            String username = ctx.attribute(USERNAME);
            String pageParam = ctx.queryParam(PAGE);
            String pageSizeParam = ctx.queryParam(PAGE_SIZE);

            int page = (pageParam != null) ? Integer.parseInt(pageParam) : 0;
            int pageSize = (pageSizeParam != null) ? Integer.parseInt(pageSizeParam) : 100;

            log.info("User: " + username + " wants to see the latest comments on his and his followers posts");

            List<CommentResponseDto> accountComments = commentService.getFollowerPostComments(username, page, pageSize);

            // Send the response
            ctx.json(accountComments);
            ctx.status(HttpStatus.OK);
        };
    }
}
