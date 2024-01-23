package com.alg.social_media.dto.post;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Comment Response dto
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {
    @NotEmpty
    private String content;

    @NotEmpty
    private String author;

    @NotEmpty
    private String postAuthor;

    @NotNull
    private LocalDate createDate;
}
