package com.alg.social_media.account.adapter.out.persistance;

import com.alg.social_media.account.adapter.application.port.out.FollowRepository;
import com.alg.social_media.account.adapter.domain.model.Follow;
import com.alg.social_media.utils.BaseRepositoryImpl;
import javax.inject.Inject;

public class FollowRepositoryImpl extends BaseRepositoryImpl<Follow, Long> implements
    FollowRepository {
  @Inject
  public FollowRepositoryImpl() {
    super(Follow.class);
  }
}
