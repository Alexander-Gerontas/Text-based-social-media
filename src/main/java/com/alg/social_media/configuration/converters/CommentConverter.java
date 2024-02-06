package com.alg.social_media.configuration.converters;

import com.alg.social_media.account.adapter.domain.dto.CommentResponseDto;
import com.alg.social_media.account.adapter.domain.model.Comment;
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

        propertyMapper.addMappings(mapper -> {
            mapper.map(src -> src.getAuthor().getUsername(), CommentResponseDto::setAuthor);
            mapper.skip(CommentResponseDto::setPostAuthor);
        });
    }

    public CommentResponseDto toResponseDto(Comment comment) {
        var post = comment.getPost();

        var dto = modelMapper.map(comment, CommentResponseDto.class);
        dto.setPostAuthor(post.getAuthor().getUsername());

        return dto;
    }
}
