package com.pilates.booking.repository;

import com.pilates.booking.domain.PeriodSubscription;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the PeriodSubscription entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PeriodSubscriptionRepository
    extends ReactiveCrudRepository<PeriodSubscription, Long>, PeriodSubscriptionRepositoryInternal {
    @Query("SELECT * FROM period_subscription entity WHERE entity.user_id = :id")
    Flux<PeriodSubscription> findByUser(Long id);

    @Query("SELECT * FROM period_subscription entity WHERE entity.user_id IS NULL")
    Flux<PeriodSubscription> findAllWhereUserIsNull();

    @Override
    <S extends PeriodSubscription> Mono<S> save(S entity);

    @Override
    Flux<PeriodSubscription> findAll();

    @Override
    Mono<PeriodSubscription> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface PeriodSubscriptionRepositoryInternal {
    <S extends PeriodSubscription> Mono<S> save(S entity);

    Flux<PeriodSubscription> findAllBy(Pageable pageable);

    Flux<PeriodSubscription> findAll();

    Mono<PeriodSubscription> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<PeriodSubscription> findAllBy(Pageable pageable, Criteria criteria);
}
