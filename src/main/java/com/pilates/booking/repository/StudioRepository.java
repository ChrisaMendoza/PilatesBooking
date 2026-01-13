package com.pilates.booking.repository;

import com.pilates.booking.domain.Studio;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Studio entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StudioRepository extends ReactiveCrudRepository<Studio, Long>, StudioRepositoryInternal {
    @Override
    <S extends Studio> Mono<S> save(S entity);

    @Override
    Flux<Studio> findAll();

    @Override
    Mono<Studio> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface StudioRepositoryInternal {
    <S extends Studio> Mono<S> save(S entity);

    Flux<Studio> findAllBy(Pageable pageable);

    Flux<Studio> findAll();

    Mono<Studio> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Studio> findAllBy(Pageable pageable, Criteria criteria);
}
