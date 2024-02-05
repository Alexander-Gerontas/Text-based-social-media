package com.alg.social_media.application.service;

import com.alg.social_media.application.port.in.AccountService;
import com.alg.social_media.application.port.out.AccountRepository;
import com.alg.social_media.configuration.converters.AccountConverter;
import com.alg.social_media.configuration.exceptions.AccountExistsException;
import com.alg.social_media.domain.dto.AccountRegistrationDto;
import com.alg.social_media.domain.dto.AccountResponseDto;
import com.alg.social_media.domain.enums.GenericError;
import com.alg.social_media.domain.model.Account;
import jakarta.transaction.Transactional;
import javax.inject.Inject;

@Transactional
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountConverter accountConverter;

    @Inject
    public AccountServiceImpl(AccountRepository accountRepository, AccountConverter accountConverter) {
        this.accountRepository = accountRepository;
        this.accountConverter = accountConverter;
    }

    public Account accountRegistration(AccountRegistrationDto dto) throws AccountExistsException {

        // search for account with the same username
        var existingAccount = accountRepository.findByUsername(dto.getUsername());

        // if an account already exists throw exception
        if (existingAccount != null) {
            throw new AccountExistsException(GenericError.ACCOUNT_WITH_SAME_USERNAME_EXISTS, dto.getUsername());
        }

        // create a new account if not
        var newAccount = accountConverter.toAccount(dto);

        accountRepository.save(newAccount);

        return newAccount;
    }

    public Account findByUsername(String username) {
      return accountRepository.findByUsername(username);
    }

    public AccountResponseDto getAccountDtoByUsername(String username) {
      var account = accountRepository.findByUsername(username);
      return accountConverter.toResponseDto(account);
    }

    public AccountResponseDto getAccountDtoByEmail(String email) {
        var account = accountRepository.findByEmail(email);
        return accountConverter.toResponseDto(account);
    }
}
