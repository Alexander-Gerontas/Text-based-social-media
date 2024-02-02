package com.alg.social_media.application.port.out;

import com.alg.social_media.domain.model.Account;

public interface AccountRepository extends BaseRepository<Account, Long> {

  Account findByUsername(String username);

  Account findByEmail(String email);
}