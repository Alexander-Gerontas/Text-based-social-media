package com.alg.social_media.configuration.converters;

import com.alg.social_media.domain.dto.CommentResponseDto;
import com.alg.social_media.domain.dto.PostResponseDto;
import com.alg.social_media.domain.model.Post;
import java.util.List;
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

        var propertyMapper = modelMapper.createTypeMap(Post.class, PostResponseDto.class);

        propertyMapper.addMappings(mapper -> {
            mapper.map(src -> src.getAuthor().getUsername(), PostResponseDto::setAuthor);
            mapper.skip(PostResponseDto::setComments);
        });
    }

    public PostResponseDto toResponseDto(Post post) {
        var dto = modelMapper.map(post, PostResponseDto.class);

        List<CommentResponseDto> commentResponseDtos = post.getComments().stream()
            .map(comment -> modelMapper.map(comment, CommentResponseDto.class))
            .toList();

        dto.setComments(commentResponseDtos);
        return dto;
    }
}
