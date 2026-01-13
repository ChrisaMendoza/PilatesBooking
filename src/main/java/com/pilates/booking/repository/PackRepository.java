package com.pilates.booking.repository;

import com.pilates.booking.domain.Pack;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Pack entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PackRepository extends ReactiveCrudRepository<Pack, Long>, PackRepositoryInternal {
    @Query("SELECT * FROM pack entity WHERE entity.user_id = :id")
    Flux<Pack> findByUser(Long id);

    @Query("SELECT * FROM pack entity WHERE entity.user_id IS NULL")
    Flux<Pack> findAllWhereUserIsNull();

    @Override
    <S extends Pack> Mono<S> save(S entity);

    @Override
    Flux<Pack> findAll();

    @Override
    Mono<Pack> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface PackRepositoryInternal {
    <S extends Pack> Mono<S> save(S entity);

    Flux<Pack> findAllBy(Pageable pageable);

    Flux<Pack> findAll();

    Mono<Pack> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Pack> findAllBy(Pageable pageable, Criteria criteria);
}
