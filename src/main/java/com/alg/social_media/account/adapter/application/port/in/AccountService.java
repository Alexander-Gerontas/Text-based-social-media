package com.alg.social_media.account.adapter.application.port.in;

import com.alg.social_media.account.adapter.domain.dto.AccountRegistrationDto;
import com.alg.social_media.account.adapter.domain.dto.AccountResponseDto;
import com.alg.social_media.account.adapter.domain.model.Account;
import com.alg.social_media.configuration.exceptions.AccountExistsException;

public interface AccountService {
  Account accountRegistration(AccountRegistrationDto dto) throws AccountExistsException;

  Account findByUsername(String username);

  AccountResponseDto getAccountDtoByUsername(String username);

  AccountResponseDto getAccountDtoByEmail(String email);
}
