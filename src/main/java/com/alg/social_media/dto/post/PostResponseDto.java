package com.alg.social_media.dto.post;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Post Response dto
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {
    @NotEmpty
    private String content;

    @NotEmpty
    private String author;

    @NotNull
    private LocalDate createDate;

    @NotNull
    List<CommentResponseDto> comments;
}
