package com.pilates.booking.service;

import com.pilates.booking.domain.Studio;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.pilates.booking.domain.Studio}.
 */
public interface StudioService {
    /**
     * Save a studio.
     *
     * @param studio the entity to save.
     * @return the persisted entity.
     */
    Mono<Studio> save(Studio studio);

    /**
     * Updates a studio.
     *
     * @param studio the entity to update.
     * @return the persisted entity.
     */
    Mono<Studio> update(Studio studio);

    /**
     * Partially updates a studio.
     *
     * @param studio the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Studio> partialUpdate(Studio studio);

    /**
     * Get all the studios.
     *
     * @return the list of entities.
     */
    Flux<Studio> findAll();

    /**
     * Returns the number of studios available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" studio.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Studio> findOne(Long id);

    /**
     * Delete the "id" studio.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
