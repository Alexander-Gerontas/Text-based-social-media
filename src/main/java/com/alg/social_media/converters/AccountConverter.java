package com.alg.social_media.converters;

import com.alg.social_media.objects.Account;
import com.alg.social_media.objects.AccountDto;
import javax.inject.Inject;
import org.modelmapper.ModelMapper;

/**
 * Converts Account DTOs to domain objects.
 */
public class AccountConverter {

//    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Inject
    public AccountConverter(final ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Account toAccount(AccountDto accountDto) {
        var account = modelMapper.map(accountDto, Account.class);

//        account.setUsername(accountDto.getUsername());
//        account.setEmail(accountDto.getEmail());
//        account.setPassword(accountDto.getPassword());

//        account.setPassword(passwordEncoder.encode(accountDto.getPassword()));

//        account.setRole(accountDto.getRole());

        return account;
    }
}
