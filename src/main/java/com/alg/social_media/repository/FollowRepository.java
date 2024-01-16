package com.alg.social_media.repository;

import com.alg.social_media.model.Follow;
import com.alg.social_media.utils.DBUtils;
import javax.inject.Inject;

public class FollowRepository extends BaseRepository<Follow, Long> {
  @Inject
  public FollowRepository(DBUtils dbUtils) {
    super(dbUtils, Follow.class);
  }
}
