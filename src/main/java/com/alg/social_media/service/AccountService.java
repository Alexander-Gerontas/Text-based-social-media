package com.alg.social_media.service;

import com.alg.social_media.converters.AccountConverter;
import com.alg.social_media.exceptions.AccountDoesNotExistException;
import com.alg.social_media.exceptions.AccountExistsException;
import com.alg.social_media.exceptions.GenericError;
import com.alg.social_media.objects.Account;
import com.alg.social_media.dto.AccountRegistrationDto;
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

    public Account findByUsername(String username)
        throws AccountExistsException, AccountDoesNotExistException {

        // search for account with the same username
        var account = accountRepository.findByUsername(username);

        // if an account does not exist throw exception
        if (account == null) {
            throw new AccountDoesNotExistException(GenericError.ACCOUNT_DOES_NOT_EXIST, username);
        }

        return account;
    }
}
