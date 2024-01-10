package com.alg.social_media.controllers;

import static com.alg.social_media.configuration.constants.Paths.POST_URI;
import static com.alg.social_media.handler.GlobalControllerExceptionHandler.handleSubscriptionException;
import static com.alg.social_media.handler.GlobalControllerExceptionHandler.handlePostException;

import com.alg.social_media.dto.post.PostDto;
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
    private PostService postService;

    @Inject
    public PostController(Javalin app, PostService postService) {
        this.app = app;
        this.postService = postService;

        configureRoutes();
    }

    private void configureRoutes() {
        app.post(POST_URI, createPost);

        app.exception(SubscriptionException.class, handleSubscriptionException);
        app.exception(PostException.class, handlePostException);
    }

    private final Handler createPost = ctx -> {
        var postDto = ctx.bodyAsClass(PostDto.class);

        String username = ctx.attribute("username");

        log.info("User: " + username + " is making a new post");

        var newPost = postService.createPost(postDto, username);

        // Send the response
        ctx.json("account with username: " + newPost.getAuthor().getUsername() +
            " made a new post");

        ctx.status(200);
    };
}
