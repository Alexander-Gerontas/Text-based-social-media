package com.alg.social_media.application.port.out;

import com.alg.social_media.domain.model.Post;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface PostRepository extends BaseRepository<Post, Long> {

  List<Post> findAccountPostAndCommentsReverseChronologically(Long accountId, int page,
      int postsLimit, int commentLimit);

  List<Post> findAccountPostReverseChronologically(Set<Long> accountIds, int page, int size);

  Post findPostByUuid(UUID postUuid, int commentLimit);
}
