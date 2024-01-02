package com.alg.social_media.objects;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Account registration dto
 */
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {

    @NotEmpty
    private String username;

    @NotEmpty
    private String email;

    @NotEmpty
    private String password;

    private AccountType role;
}
