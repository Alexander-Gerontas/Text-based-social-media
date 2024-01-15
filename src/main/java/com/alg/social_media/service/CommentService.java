package com.alg.social_media.service;

import static com.alg.social_media.exceptions.GenericError.COMMENT_CHARACTER_LIMIT;
import static com.alg.social_media.exceptions.GenericError.POST_DOES_NOT_EXIST;

import com.alg.social_media.dto.post.CommentDto;
import com.alg.social_media.enums.AccountType;
import com.alg.social_media.exceptions.PostDoesNotExistException;
import com.alg.social_media.exceptions.SubscriptionException;
import com.alg.social_media.model.Comment;
import com.alg.social_media.repository.CommentRepository;
import java.time.LocalDateTime;
import javax.inject.Inject;

public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;
    private final AccountService accountService;

    @Inject
    public CommentService(final CommentRepository commentRepository,
        final PostService postService,
        final AccountService accountService) {
        this.commentRepository = commentRepository;
        this.postService = postService;
        this.accountService = accountService;
    }

    public Comment createComment(CommentDto commentDto, Long postId, String username)
        throws SubscriptionException, PostDoesNotExistException {

        var post = postService.findById(postId);

        // throw exception if someone tries to comment on a post that does not exist
        if (post == null) {
            throw new PostDoesNotExistException(POST_DOES_NOT_EXIST, postId);
        }

        var account = accountService.findByUsername(username);
        int commentsMadeByUser = commentRepository.countPostsByAuthorId(account.getId(), postId);

        // throw exception if free user posts more than 5 times
        if (account.getRole().equals(AccountType.FREE) && commentsMadeByUser > 4) {
            throw new SubscriptionException(COMMENT_CHARACTER_LIMIT, username);
        }

        Comment comment = Comment.builder()
            .content(commentDto.getContent())
            .author(account)
            .post(post)
            .createDate(LocalDateTime.now())
            .build();

        return commentRepository.save(comment);
    }
}
