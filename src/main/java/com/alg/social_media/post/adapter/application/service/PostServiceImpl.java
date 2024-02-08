package com.alg.social_media.post.adapter.application.service;

import static com.alg.social_media.account.adapter.domain.enums.GenericError.SUBSCRIPTION_ERROR;

import com.alg.social_media.account.adapter.application.port.in.AccountService;
import com.alg.social_media.account.adapter.domain.enums.AccountType;
import com.alg.social_media.account.adapter.domain.enums.GenericError;
import com.alg.social_media.account.adapter.domain.model.Account;
import com.alg.social_media.account.adapter.domain.model.Follow;
import com.alg.social_media.configuration.converters.PostConverter;
import com.alg.social_media.configuration.exceptions.PostException;
import com.alg.social_media.configuration.exceptions.SubscriptionException;
import com.alg.social_media.post.adapter.application.port.in.PostService;
import com.alg.social_media.post.adapter.application.port.out.PostRepository;
import com.alg.social_media.post.adapter.domain.dto.PostDto;
import com.alg.social_media.post.adapter.domain.dto.PostResponseDto;
import com.alg.social_media.post.adapter.domain.model.Post;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.inject.Inject;

@Transactional
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostConverter postConverter;
    private final AccountService accountService;

    @Inject
    public PostServiceImpl(final PostRepository postRepository, final PostConverter postConverter,
        final AccountService accountService) {
        this.postRepository = postRepository;
        this.postConverter = postConverter;
        this.accountService = accountService;
    }

    public Post createPost(PostDto postDto, String username)
        throws SubscriptionException, PostException {
        var account = accountService.findByUsername(username);

        if (account.getRole() == AccountType.FREE && postDto.getContent().length() > 1000) {
            throw new SubscriptionException(SUBSCRIPTION_ERROR, account.getUsername());
        } else if (account.getRole() == AccountType.PREMIUM
            && postDto.getContent().length() > 3000) {
            throw new PostException(GenericError.POST_CHARACTER_LIMIT);
        }

        Post post = Post.builder()
            .content(postDto.getContent())
            .author(account)
            .createDate(LocalDate.now())
            .build();

        return postRepository.save(post);
    }

    public Post findById(Long id) {
        return postRepository.findById(id);
    }

    public List<PostResponseDto> getFollowerPosts(String username, int page, int pageSize) {
        Account account = accountService.findByUsername(username);

        var followerIds = account.getFollowers().stream()
            .map(Follow::getFollowing)
            .map(Account::getId)
            .collect(Collectors.toSet());

        List<Post> followerPost = postRepository.findAccountPostReverseChronologically(followerIds, page, pageSize);

        return followerPost.stream()
            .map(postConverter::toResponseDto)
            .toList();
    }

    public List<PostResponseDto> getAccountPosts(String username, int page, int pageSize, int commentLimit) {
        Account account = accountService.findByUsername(username);

        List<Post> latestPosts = postRepository
            .findAccountPostAndCommentsReverseChronologically(account.getId(), page, pageSize, commentLimit);

        return latestPosts.stream()
            .map(postConverter::toResponseDto)
            .toList();
    }

    public PostResponseDto getPostByUuid(UUID postId, int commentLimit) {
        Post post = postRepository.findPostByUuid(postId, commentLimit);
        return postConverter.toResponseDto(post);
    }
}
