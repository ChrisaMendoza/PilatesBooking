package com.pilates.booking.service;

import com.pilates.booking.domain.ClassType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.pilates.booking.domain.ClassType}.
 */
public interface ClassTypeService {
    /**
     * Save a classType.
     *
     * @param classType the entity to save.
     * @return the persisted entity.
     */
    Mono<ClassType> save(ClassType classType);

    /**
     * Updates a classType.
     *
     * @param classType the entity to update.
     * @return the persisted entity.
     */
    Mono<ClassType> update(ClassType classType);

    /**
     * Partially updates a classType.
     *
     * @param classType the entity to update partially.
     * @return the persisted entity.
     */
    Mono<ClassType> partialUpdate(ClassType classType);

    /**
     * Get all the classTypes.
     *
     * @return the list of entities.
     */
    Flux<ClassType> findAll();

    /**
     * Returns the number of classTypes available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" classType.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ClassType> findOne(Long id);

    /**
     * Delete the "id" classType.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
