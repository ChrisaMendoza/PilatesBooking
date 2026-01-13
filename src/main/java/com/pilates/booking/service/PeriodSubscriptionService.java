package com.pilates.booking.service;

import com.pilates.booking.domain.PeriodSubscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.pilates.booking.domain.PeriodSubscription}.
 */
public interface PeriodSubscriptionService {
    /**
     * Save a periodSubscription.
     *
     * @param periodSubscription the entity to save.
     * @return the persisted entity.
     */
    Mono<PeriodSubscription> save(PeriodSubscription periodSubscription);

    /**
     * Updates a periodSubscription.
     *
     * @param periodSubscription the entity to update.
     * @return the persisted entity.
     */
    Mono<PeriodSubscription> update(PeriodSubscription periodSubscription);

    /**
     * Partially updates a periodSubscription.
     *
     * @param periodSubscription the entity to update partially.
     * @return the persisted entity.
     */
    Mono<PeriodSubscription> partialUpdate(PeriodSubscription periodSubscription);

    /**
     * Get all the periodSubscriptions.
     *
     * @return the list of entities.
     */
    Flux<PeriodSubscription> findAll();

    /**
     * Returns the number of periodSubscriptions available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" periodSubscription.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<PeriodSubscription> findOne(Long id);

    /**
     * Delete the "id" periodSubscription.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
