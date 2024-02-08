package com.alg.social_media.account.adapter.application.port.out;

import com.alg.social_media.account.adapter.domain.model.Account;
import com.alg.social_media.utils.BaseRepository;

public interface AccountRepository extends BaseRepository<Account, Long> {

  Account findByUsername(String username);

  Account findByEmail(String email);
}