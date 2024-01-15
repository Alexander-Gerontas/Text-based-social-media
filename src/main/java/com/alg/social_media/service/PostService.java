package com.alg.social_media.service;

import static com.alg.social_media.exceptions.GenericError.SUBSCRIPTION_ERROR;

import com.alg.social_media.dto.post.PostDto;
import com.alg.social_media.enums.AccountType;
import com.alg.social_media.exceptions.GenericError;
import com.alg.social_media.exceptions.PostException;
import com.alg.social_media.exceptions.SubscriptionException;
import com.alg.social_media.model.Post;
import com.alg.social_media.repository.PostRepository;
import javax.inject.Inject;

public class PostService {

    private final PostRepository postRepository;
    private final AccountService accountService;

    @Inject
    public PostService(final PostRepository accountRepository,
        final AccountService accountService) {
        this.postRepository = accountRepository;
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
            .build();

        return postRepository.save(post);
    }

    public Post findById(Long id) {
        return postRepository.findById(id);
    }
}
