package com.alg.social_media.dto.account;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Account follow dto
 */
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FollowDto {
    @NotNull
    private String followingUsername;
}
