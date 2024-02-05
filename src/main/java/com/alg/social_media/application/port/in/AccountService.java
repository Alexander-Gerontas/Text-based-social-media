package com.alg.social_media.application.port.in;

import com.alg.social_media.configuration.exceptions.AccountExistsException;
import com.alg.social_media.domain.dto.AccountRegistrationDto;
import com.alg.social_media.domain.dto.AccountResponseDto;
import com.alg.social_media.domain.model.Account;

public interface AccountService {
  Account accountRegistration(AccountRegistrationDto dto) throws AccountExistsException;

  Account findByUsername(String username);

  AccountResponseDto getAccountDtoByUsername(String username);

  AccountResponseDto getAccountDtoByEmail(String email);
}
