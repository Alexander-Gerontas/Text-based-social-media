package com.alg.social_media.repository;

import com.alg.social_media.model.Account;

public interface AccountRepository extends BaseRepository<Account, Long> {

  Account findByUsername(String username);

  Account findByEmail(String email);
}