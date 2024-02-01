package com.alg.social_media.repository;

import com.alg.social_media.model.Follow;
import javax.inject.Inject;

public class FollowRepository extends BaseRepository<Follow, Long> {
  @Inject
  public FollowRepository() {
    super(Follow.class);
  }
}
