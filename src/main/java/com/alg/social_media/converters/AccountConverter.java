package com.alg.social_media.converters;

import com.alg.social_media.dto.account.AccountRegistrationDto;
import com.alg.social_media.dto.account.AccountResponseDto;
import com.alg.social_media.model.Account;
import com.alg.social_media.utils.PasswordEncoder;
import java.util.List;
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

    public AccountResponseDto toResponseDto(Account account) {
        if (account == null) {
            return null;
        }

        var responseDto = modelMapper.map(account, AccountResponseDto.class);
        return responseDto;
    }

    public List<AccountResponseDto> toResponseDtos(List<Account> account) {
        var responseDtos = modelMapper.map(account, AccountResponseDto[].class);
        return List.of(responseDtos);
    }
}
