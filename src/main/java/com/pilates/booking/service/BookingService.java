package com.pilates.booking.service;

import com.pilates.booking.domain.Booking;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.pilates.booking.domain.Booking}.
 */
public interface BookingService {
    Mono<Booking> save(Booking booking);

    Mono<Booking> update(Booking booking);

    Mono<Booking> partialUpdate(Booking booking);

    Flux<Booking> findAll();

    Mono<Long> countAll();

    Mono<Booking> findOne(Long id);

    Mono<Void> delete(Long id);

    /**
     * Cancel a booking (penalty -5€ if less than 12h before event start).
     */
    Mono<Booking> cancel(Long id);
}
