package com.alg.social_media.controllers;

import static com.alg.social_media.constants.ControllerArgs.COMMENT_LIMIT;
import static com.alg.social_media.constants.ControllerArgs.PAGE;
import static com.alg.social_media.constants.ControllerArgs.PAGE_SIZE;
import static com.alg.social_media.constants.Keywords.USERNAME;
import static com.alg.social_media.constants.Paths.COMMENT_URI;
import static com.alg.social_media.constants.Paths.FOLLOWER_POSTS_URI;
import static com.alg.social_media.constants.Paths.MY_POSTS_URI;
import static com.alg.social_media.constants.Paths.POST_URI;
import static com.alg.social_media.handler.GlobalControllerExceptionHandler.handlePostException;
import static com.alg.social_media.handler.GlobalControllerExceptionHandler.handleSubscriptionException;

import com.alg.social_media.dto.post.CommentDto;
import com.alg.social_media.dto.post.PostDto;
import com.alg.social_media.dto.post.PostResponseDto;
import com.alg.social_media.enums.AccountType;
import com.alg.social_media.exceptions.PostException;
import com.alg.social_media.exceptions.SubscriptionException;
import com.alg.social_media.service.CommentService;
import com.alg.social_media.service.PostService;
import io.javalin.Javalin;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import java.util.List;
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

        // get follower posts
        app.get(FOLLOWER_POSTS_URI, getFollowerPostsHandler(), AccountType.FREE, AccountType.PREMIUM);

        // user gets his own posts
        app.get(MY_POSTS_URI, getAccountPostsHandler(), AccountType.FREE, AccountType.PREMIUM);

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

    private Handler getAccountPostsHandler() {
        return ctx -> {
            String username = ctx.attribute(USERNAME);
            String pageParam = ctx.queryParam(PAGE);
            String pageSizeParam = ctx.queryParam(PAGE_SIZE);
            String commentLimitParam = ctx.queryParam(COMMENT_LIMIT);

            int page = (pageParam != null) ? Integer.parseInt(pageParam) : 0;
            int pageSize = (pageSizeParam != null) ? Integer.parseInt(pageSizeParam) : 10;
            int commentLimit = (commentLimitParam != null) ? Integer.parseInt(commentLimitParam) : 50;

            log.info("User: " + username + " wants to see what his posts");

            List<PostResponseDto> accountPosts = postService.getAccountPosts(username, page, pageSize, commentLimit);

            // Send the response
            ctx.json(accountPosts);
            ctx.status(HttpStatus.OK);
        };
    }

    private Handler getFollowerPostsHandler() {
        return ctx -> {
            String username = ctx.attribute(USERNAME);

            int page = 0;
            int pageSize = 10;

            String pageParam = ctx.queryParam(PAGE);
            String pageSizeParam = ctx.queryParam(PAGE_SIZE);

            if (pageParam != null) {
                page = Integer.parseInt(pageParam);
            }

            if (pageSizeParam != null) {
                pageSize = Integer.parseInt(pageSizeParam);
            }

            log.info("User: " + username + " wants to see what his followers are posting");

            List<PostResponseDto> followerPosts = postService.getFollowerPosts(username, page, pageSize);

            // Send the response
            ctx.json(followerPosts);
            ctx.status(HttpStatus.OK);
        };
    }
}
