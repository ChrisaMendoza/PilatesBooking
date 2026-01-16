package com.pilates.booking.repository;

import com.pilates.booking.domain.Booking;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Booking entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BookingRepository extends ReactiveCrudRepository<Booking, Long>, BookingRepositoryInternal {
    @Query("SELECT * FROM booking entity WHERE entity.user_id = :id")
    Flux<Booking> findByUser(Long id);

    @Query("SELECT * FROM booking entity WHERE entity.user_id IS NULL")
    Flux<Booking> findAllWhereUserIsNull();

    @Query("SELECT * FROM booking entity WHERE entity.event_id = :id")
    Flux<Booking> findByEvent(Long id);

    @Query("SELECT * FROM booking entity WHERE entity.event_id IS NULL")
    Flux<Booking> findAllWhereEventIsNull();

    @Override
    <S extends Booking> Mono<S> save(S entity);

    @Override
    Flux<Booking> findAll();

    @Override
    Mono<Booking> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface BookingRepositoryInternal {
    <S extends Booking> Mono<S> save(S entity);

    Flux<Booking> findAllBy(Pageable pageable);

    Flux<Booking> findAll();

    Mono<Booking> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Booking> findAllBy(Pageable pageable, Criteria criteria);
}
