package com.alg.social_media.controllers;

import static com.alg.social_media.constants.Keywords.USERNAME;
import static com.alg.social_media.constants.Paths.COMMENT_URI;
import static com.alg.social_media.constants.Paths.POST_URI;
import static com.alg.social_media.handler.GlobalControllerExceptionHandler.handlePostException;
import static com.alg.social_media.handler.GlobalControllerExceptionHandler.handleSubscriptionException;

import com.alg.social_media.dto.post.CommentDto;
import com.alg.social_media.dto.post.PostDto;
import com.alg.social_media.enums.AccountType;
import com.alg.social_media.exceptions.PostException;
import com.alg.social_media.exceptions.SubscriptionException;
import com.alg.social_media.service.CommentService;
import com.alg.social_media.service.PostService;
import io.javalin.Javalin;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PostController {
    private final Javalin app;
    private final PostService postService;
    private final CommentService commentService;

    @Inject
    public PostController(Javalin app, PostService postService, CommentService commentService) {
        this.app = app;
        this.postService = postService;
        this.commentService = commentService;

        configureRoutes();
    }

    private void configureRoutes() {

        // create new post
        app.post(POST_URI, createPostHandler(), AccountType.FREE, AccountType.PREMIUM);

        // comment on a post
        app.post(POST_URI + "/{id}" + COMMENT_URI, commentHandler(), AccountType.FREE, AccountType.PREMIUM);

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

    private Handler commentHandler() {
        return ctx -> {
            var commentDto = ctx.bodyAsClass(CommentDto.class);

            String username = ctx.attribute(USERNAME);

            log.info("User: " + username + " is writing a comment");

            // get post id
            long postId = Long.parseLong(ctx.pathParam("id"));

            var newComment = commentService.createComment(commentDto, postId, username);

            // Send the response
            ctx.json(newComment.getAuthor().getUsername() +
                " made a comment on post with id: " + postId);

            ctx.status(HttpStatus.OK);
        };
    }
}
