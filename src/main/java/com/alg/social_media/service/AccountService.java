package com.alg.social_media.service;

import com.alg.social_media.converters.AccountConverter;
import com.alg.social_media.exceptions.AccountExistsException;
import com.alg.social_media.exceptions.GenericError;
import com.alg.social_media.objects.Account;
import com.alg.social_media.objects.AccountDto;
import com.alg.social_media.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountConverter accountConverter;

//    @Override
    public Account accountRegistration(AccountDto dto) throws AccountExistsException {

        // search for account with the same username
        var existingAccount = accountRepository.findByUsername(dto.getUsername());

        // if an account already exists throw exception
        if (existingAccount != null) {
            throw new AccountExistsException(GenericError.ACCOUNT_WITH_SAME_USERNAME_EXISTS, dto.getUsername());
        }

        // create a new account if not
        var newAccount = accountConverter.toAccount(dto);
        newAccount = accountRepository.save(newAccount);

        return newAccount;
    }
}
