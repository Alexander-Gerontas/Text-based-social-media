package com.alg.social_media.service;

import com.alg.social_media.converters.AccountConverter;
import com.alg.social_media.exceptions.AccountExistsException;
import com.alg.social_media.exceptions.GenericError;
import com.alg.social_media.model.Account;
import com.alg.social_media.dto.account.AccountRegistrationDto;
import com.alg.social_media.repository.AccountRepository;
import javax.inject.Inject;

public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountConverter accountConverter;

    @Inject
    public AccountService(AccountRepository accountRepository, AccountConverter accountConverter) {
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
}
