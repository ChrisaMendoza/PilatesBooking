package com.pilates.booking.service;

import com.pilates.booking.domain.Pack;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.pilates.booking.domain.Pack}.
 */
public interface PackService {
    /**
     * Save a pack.
     *
     * @param pack the entity to save.
     * @return the persisted entity.
     */
    Mono<Pack> save(Pack pack);

    /**
     * Updates a pack.
     *
     * @param pack the entity to update.
     * @return the persisted entity.
     */
    Mono<Pack> update(Pack pack);

    /**
     * Partially updates a pack.
     *
     * @param pack the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Pack> partialUpdate(Pack pack);

    /**
     * Get all the packs.
     *
     * @return the list of entities.
     */
    Flux<Pack> findAll();

    /**
     * Returns the number of packs available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" pack.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Pack> findOne(Long id);

    /**
     * Delete the "id" pack.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
