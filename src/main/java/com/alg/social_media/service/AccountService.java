package com.alg.social_media.service;

import com.alg.social_media.dto.account.AccountRegistrationDto;
import com.alg.social_media.dto.account.AccountResponseDto;
import com.alg.social_media.exceptions.AccountExistsException;
import com.alg.social_media.model.Account;

public interface AccountService {
  Account accountRegistration(AccountRegistrationDto dto) throws AccountExistsException;

  Account findByUsername(String username);

  AccountResponseDto getAccountDtoByUsername(String username);

  AccountResponseDto getAccountDtoByEmail(String email);
}
