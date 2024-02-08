package com.alg.social_media.utils;

import java.util.List;

/**
 * The Base repository.
 *
 * @param <E> the entity
 * @param <P> the primary key of the entity
 */
public interface BaseRepository<E, P> {
  E save(E entity);

  void update(E entity);

  E findById(P id);
  List<E> findAll();

  void deleteAll();
}
