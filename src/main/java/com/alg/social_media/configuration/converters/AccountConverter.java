package com.alg.social_media.configuration.converters;

import com.alg.social_media.account.adapter.domain.dto.AccountRegistrationDto;
import com.alg.social_media.account.adapter.domain.dto.AccountResponseDto;
import com.alg.social_media.account.adapter.domain.model.Account;
import java.util.List;
import javax.inject.Inject;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Converts Account DTOs to domain objects.
 */
public class AccountConverter {
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Inject
    public AccountConverter(final ModelMapper modelMapper, final BCryptPasswordEncoder passwordEncoder) {
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public Account toAccount(AccountRegistrationDto registrationDto) {
        var account = modelMapper.map(registrationDto, Account.class);
        account.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
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
