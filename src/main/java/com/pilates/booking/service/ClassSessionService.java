package com.pilates.booking.service;

import com.pilates.booking.domain.ClassSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.pilates.booking.domain.ClassSession}.
 */
public interface ClassSessionService {
    /**
     * Save a classSession.
     *
     * @param classSession the entity to save.
     * @return the persisted entity.
     */
    Mono<ClassSession> save(ClassSession classSession);

    /**
     * Updates a classSession.
     *
     * @param classSession the entity to update.
     * @return the persisted entity.
     */
    Mono<ClassSession> update(ClassSession classSession);

    /**
     * Partially updates a classSession.
     *
     * @param classSession the entity to update partially.
     * @return the persisted entity.
     */
    Mono<ClassSession> partialUpdate(ClassSession classSession);

    /**
     * Get all the classSessions.
     *
     * @return the list of entities.
     */
    Flux<ClassSession> findAll();

    /**
     * Returns the number of classSessions available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" classSession.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ClassSession> findOne(Long id);

    /**
     * Delete the "id" classSession.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
