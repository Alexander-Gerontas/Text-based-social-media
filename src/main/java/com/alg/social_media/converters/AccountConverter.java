package com.alg.social_media.converters;

import com.alg.social_media.model.Account;
import com.alg.social_media.dto.AccountRegistrationDto;
import com.alg.social_media.utils.PasswordEncoder;
import javax.inject.Inject;
import org.modelmapper.ModelMapper;

/**
 * Converts Account DTOs to domain objects.
 */
public class AccountConverter {
    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;

    @Inject
    public AccountConverter(final ModelMapper modelMapper, final PasswordEncoder passwordEncoder) {
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public Account toAccount(AccountRegistrationDto registrationDto) {
        var account = modelMapper.map(registrationDto, Account.class);
        account.setPassword(passwordEncoder.encryptPassword(registrationDto.getPassword()));
        return account;
    }
}
