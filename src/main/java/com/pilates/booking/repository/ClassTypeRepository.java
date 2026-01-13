package com.pilates.booking.repository;

import com.pilates.booking.domain.ClassType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the ClassType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClassTypeRepository extends ReactiveCrudRepository<ClassType, Long>, ClassTypeRepositoryInternal {
    @Override
    <S extends ClassType> Mono<S> save(S entity);

    @Override
    Flux<ClassType> findAll();

    @Override
    Mono<ClassType> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ClassTypeRepositoryInternal {
    <S extends ClassType> Mono<S> save(S entity);

    Flux<ClassType> findAllBy(Pageable pageable);

    Flux<ClassType> findAll();

    Mono<ClassType> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<ClassType> findAllBy(Pageable pageable, Criteria criteria);
}
