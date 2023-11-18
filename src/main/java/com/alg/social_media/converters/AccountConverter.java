package com.alg.social_media.converters;

import com.alg.social_media.objects.Account;
import com.alg.social_media.objects.AccountDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Converts Account DTOs to domain objects.
 */
@Component
@RequiredArgsConstructor
public class AccountConverter {

//    private final PasswordEncoder passwordEncoder;

    public Account toAccount(AccountDto accountDto) {

        var account = new Account();

        account.setUsername(accountDto.getUsername());
        account.setEmail(accountDto.getEmail());

        account.setPassword(accountDto.getPassword());
//        account.setPassword(passwordEncoder.encode(accountDto.getPassword()));

        account.setRole(accountDto.getRole());

        return account;
    }
}
