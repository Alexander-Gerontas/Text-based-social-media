package com.alg.social_media.service;

import static com.alg.social_media.enums.GenericError.COMMENT_CHARACTER_LIMIT;
import static com.alg.social_media.enums.GenericError.POST_DOES_NOT_EXIST;

import com.alg.social_media.converters.CommentConverter;
import com.alg.social_media.dto.post.CommentDto;
import com.alg.social_media.dto.post.CommentResponseDto;
import com.alg.social_media.enums.AccountType;
import com.alg.social_media.exceptions.PostDoesNotExistException;
import com.alg.social_media.exceptions.SubscriptionException;
import com.alg.social_media.model.Account;
import com.alg.social_media.model.Comment;
import com.alg.social_media.model.Follow;
import com.alg.social_media.repository.CommentRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;

//@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentConverter commentConverter;
    private final PostService postService;
    private final AccountService accountService;

    @Inject
    public CommentServiceImpl(final CommentRepository commentRepository,
        final CommentConverter commentConverter,
        final PostService postService,
        final AccountService accountService) {
        this.commentRepository = commentRepository;
        this.commentConverter = commentConverter;
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
            .createDate(LocalDate.now())
            .build();

        return commentRepository.save(comment);
    }

    public List<CommentResponseDto> getAccountPostComments(String username, int page, int pageSize) {
        Account account = accountService.findByUsername(username);

        List<Comment> latestComments = commentRepository
            .findAccountPostCommentsReverseChronologically(account.getId(), page, pageSize);

        return latestComments.stream()
            .map(commentConverter::toResponseDto)
            .toList();
    }

    public List<CommentResponseDto> getFollowerPostComments(String username, int page, int pageSize) {
        Account account = accountService.findByUsername(username);

        // get follower ids
        var followerIds = account.getFollowers().stream().map(Follow::getFollower)
            .map(Account::getId)
            .collect(Collectors.toList());

        // add user's id in the id list
        followerIds.add(account.getId());

        List<Comment> latestComments = commentRepository
            .findFollowersPostCommentsReverseChronologically(followerIds, page, pageSize);

        return latestComments.stream()
            .map(commentConverter::toResponseDto)
            .toList();
    }
}
