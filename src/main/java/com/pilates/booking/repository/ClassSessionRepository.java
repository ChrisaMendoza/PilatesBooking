package com.pilates.booking.repository;

import com.pilates.booking.domain.ClassSession;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the ClassSession entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClassSessionRepository extends ReactiveCrudRepository<ClassSession, Long>, ClassSessionRepositoryInternal {
    @Query("SELECT * FROM class_session entity WHERE entity.studio_id = :id")
    Flux<ClassSession> findByStudio(Long id);

    @Query("SELECT * FROM class_session entity WHERE entity.studio_id IS NULL")
    Flux<ClassSession> findAllWhereStudioIsNull();

    @Query("SELECT * FROM class_session entity WHERE entity.class_type_id = :id")
    Flux<ClassSession> findByClassType(Long id);

    @Query("SELECT * FROM class_session entity WHERE entity.class_type_id IS NULL")
    Flux<ClassSession> findAllWhereClassTypeIsNull();

    @Override
    <S extends ClassSession> Mono<S> save(S entity);

    @Override
    Flux<ClassSession> findAll();

    @Override
    Mono<ClassSession> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ClassSessionRepositoryInternal {
    <S extends ClassSession> Mono<S> save(S entity);

    Flux<ClassSession> findAllBy(Pageable pageable);

    Flux<ClassSession> findAll();

    Mono<ClassSession> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<ClassSession> findAllBy(Pageable pageable, Criteria criteria);
}
