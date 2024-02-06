package com.alg.social_media.account.adapter.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Account login dto
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AccountLoginDto {

    @NotEmpty
    private String username;

    @NotEmpty
    private String password;
}
