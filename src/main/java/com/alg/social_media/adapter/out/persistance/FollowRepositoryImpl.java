package com.alg.social_media.adapter.out.persistance;

import com.alg.social_media.domain.model.Follow;
import com.alg.social_media.application.port.out.FollowRepository;
import javax.inject.Inject;

public class FollowRepositoryImpl extends BaseRepositoryImpl<Follow, Long> implements
    FollowRepository {
  @Inject
  public FollowRepositoryImpl() {
    super(Follow.class);
  }
}
