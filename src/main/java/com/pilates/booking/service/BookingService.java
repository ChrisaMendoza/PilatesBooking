package com.pilates.booking.service;

import com.pilates.booking.domain.Booking;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.pilates.booking.domain.Booking}.
 */
public interface BookingService {
    /**
     * Save a booking.
     *
     * @param booking the entity to save.
     * @return the persisted entity.
     */
    Mono<Booking> save(Booking booking);

    /**
     * Updates a booking.
     *
     * @param booking the entity to update.
     * @return the persisted entity.
     */
    Mono<Booking> update(Booking booking);

    /**
     * Partially updates a booking.
     *
     * @param booking the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Booking> partialUpdate(Booking booking);

    /**
     * Get all the bookings.
     *
     * @return the list of entities.
     */
    Flux<Booking> findAll();

    /**
     * Returns the number of bookings available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" booking.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Booking> findOne(Long id);

    /**
     * Delete the "id" booking.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
