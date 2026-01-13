package com.pilates.booking.service.impl;

import com.pilates.booking.domain.Booking;
import com.pilates.booking.repository.BookingRepository;
import com.pilates.booking.service.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.pilates.booking.domain.Booking}.
 */
@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private static final Logger LOG = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final BookingRepository bookingRepository;

    public BookingServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Mono<Booking> save(Booking booking) {
        LOG.debug("Request to save Booking : {}", booking);
        return bookingRepository.save(booking);
    }

    @Override
    public Mono<Booking> update(Booking booking) {
        LOG.debug("Request to update Booking : {}", booking);
        return bookingRepository.save(booking);
    }

    @Override
    public Mono<Booking> partialUpdate(Booking booking) {
        LOG.debug("Request to partially update Booking : {}", booking);

        return bookingRepository
            .findById(booking.getId())
            .map(existingBooking -> {
                if (booking.getStatus() != null) {
                    existingBooking.setStatus(booking.getStatus());
                }
                if (booking.getCreatedAt() != null) {
                    existingBooking.setCreatedAt(booking.getCreatedAt());
                }
                if (booking.getCancelledAt() != null) {
                    existingBooking.setCancelledAt(booking.getCancelledAt());
                }

                return existingBooking;
            })
            .flatMap(bookingRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Booking> findAll() {
        LOG.debug("Request to get all Bookings");
        return bookingRepository.findAll();
    }

    public Mono<Long> countAll() {
        return bookingRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Booking> findOne(Long id) {
        LOG.debug("Request to get Booking : {}", id);
        return bookingRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Booking : {}", id);
        return bookingRepository.deleteById(id);
    }
}
