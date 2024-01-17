package com.alg.social_media.converters;

import com.alg.social_media.dto.post.PostResponseDto;
import com.alg.social_media.model.Post;
import javax.inject.Inject;
import org.modelmapper.ModelMapper;

/**
 * Converts Post DTOs to domain objects and vice versa.
 */
public class PostConverter {
    private final ModelMapper modelMapper;

    @Inject
    public PostConverter(final ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public PostResponseDto toResponseDto(Post post) {
        var dto = modelMapper.map(post, PostResponseDto.class);
        dto.setAuthor(post.getAuthor().getUsername());
        return dto;
    }
}
