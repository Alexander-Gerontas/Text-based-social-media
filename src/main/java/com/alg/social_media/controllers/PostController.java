package com.alg.social_media.controllers;

import static com.alg.social_media.constants.Keywords.USERNAME;
import static com.alg.social_media.constants.Paths.POST_URI;
import static com.alg.social_media.handler.GlobalControllerExceptionHandler.handlePostException;
import static com.alg.social_media.handler.GlobalControllerExceptionHandler.handleSubscriptionException;

import com.alg.social_media.dto.post.PostDto;
import com.alg.social_media.enums.AccountType;
import com.alg.social_media.exceptions.PostException;
import com.alg.social_media.exceptions.SubscriptionException;
import com.alg.social_media.service.PostService;
import io.javalin.Javalin;
import io.javalin.http.Handler;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PostController {
    private final Javalin app;
    private final PostService postService;
    private final Handler createPost;

    @Inject
    public PostController(Javalin app, PostService postService) {
        this.app = app;
        this.postService = postService;

        createPost = createPostHandler();

        configureRoutes();
    }

    private void configureRoutes() {
        app.post(POST_URI, createPost, AccountType.FREE, AccountType.PREMIUM);

        app.exception(SubscriptionException.class, handleSubscriptionException);
        app.exception(PostException.class, handlePostException);
    }

    private Handler createPostHandler() {
        return ctx -> {
            var postDto = ctx.bodyAsClass(PostDto.class);

            String username = ctx.attribute(USERNAME);

            log.info("User: " + username + " is making a new post");

            var newPost = postService.createPost(postDto, username);

            // Send the response
            ctx.json("account with username: " + newPost.getAuthor().getUsername() +
                " made a new post");

            ctx.status(200);
        };
    }
}
