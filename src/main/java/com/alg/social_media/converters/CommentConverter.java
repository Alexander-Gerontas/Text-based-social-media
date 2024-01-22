package com.alg.social_media.converters;

import com.alg.social_media.dto.post.CommentResponseDto;
import com.alg.social_media.model.Comment;
import javax.inject.Inject;
import org.modelmapper.ModelMapper;

/**
 * Converts Comment DTOs to domain objects and vice versa.
 */
public class CommentConverter {
    private final ModelMapper modelMapper;

    @Inject
    public CommentConverter(final ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        var propertyMapper = modelMapper.createTypeMap(Comment.class, CommentResponseDto.class);

        propertyMapper.addMappings(
            mapper -> mapper.map(src -> src.getAuthor().getUsername(), CommentResponseDto::setAuthor)
        );
    }

    public CommentResponseDto toResponseDto(Comment comment) {
        var dto = modelMapper.map(comment, CommentResponseDto.class);
        return dto;
    }
}
