package com.alg.social_media.application.port.in;

import com.alg.social_media.domain.dto.AccountRegistrationDto;
import com.alg.social_media.domain.dto.AccountResponseDto;
import com.alg.social_media.domain.model.Account;
import com.alg.social_media.exceptions.AccountExistsException;

public interface AccountService {
  Account accountRegistration(AccountRegistrationDto dto) throws AccountExistsException;

  Account findByUsername(String username);

  AccountResponseDto getAccountDtoByUsername(String username);

  AccountResponseDto getAccountDtoByEmail(String email);
}
