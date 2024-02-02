package com.alg.social_media.repository;

import com.alg.social_media.model.Follow;
import javax.inject.Inject;

public class FollowRepositoryImpl extends BaseRepositoryImpl<Follow, Long> implements FollowRepository {
  @Inject
  public FollowRepositoryImpl() {
    super(Follow.class);
  }
}
