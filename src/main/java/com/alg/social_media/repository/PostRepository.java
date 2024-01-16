package com.alg.social_media.repository;

import com.alg.social_media.model.Post;
import com.alg.social_media.utils.DBUtils;
import javax.inject.Inject;

public class PostRepository extends BaseRepository<Post, Long> {
  @Inject
  public PostRepository(DBUtils dbUtils) {
    super(dbUtils, Post.class);
  }
}
