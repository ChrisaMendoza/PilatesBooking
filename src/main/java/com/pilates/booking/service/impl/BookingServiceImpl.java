package com.pilates.booking.service.impl;

import com.pilates.booking.domain.Booking;
import com.pilates.booking.repository.BookingRepository;
import com.pilates.booking.repository.EventRepository;
import com.pilates.booking.repository.UserRepository;
import com.pilates.booking.service.BookingService;
import java.time.Duration;
import java.time.ZonedDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.pilates.booking.domain.Booking}.
 */
@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private static final Logger LOG = LoggerFactory.getLogger(BookingServiceImpl.class);

    private static final String STATUS_BOOKED = "BOOKED";
    private static final String STATUS_CANCELLED = "CANCELLED";
    private static final String STATUS_FULL = "FULL";

    private static final long PENALTY_CENTS = 500L; // 5€
    private static final long PENALTY_WINDOW_HOURS = 12L;

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public BookingServiceImpl(BookingRepository bookingRepository, EventRepository eventRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Mono<Booking> save(Booking booking) {
        LOG.debug("Request to save Booking with business checks : {}", booking);

        if (booking.getEventId() == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "eventId is required"));
        }
        if (booking.getUserId() == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required"));
        }

        return eventRepository
            .findById(booking.getEventId())
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event not found")))
            .flatMap(event -> {
                ZonedDateTime now = ZonedDateTime.now();

                // séance doit être dans le futur
                if (!event.getStartAt().isAfter(now)) {
                    return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot book past or ongoing event"));
                }

                return bookingRepository.countByEventIdAndStatus(event.getId(), STATUS_BOOKED).flatMap(bookedCount -> {
                    booking.setCreatedAt(now);
                    booking.setCancelledAt(null);

                    // capacité dispo
                    if (bookedCount < event.getCapacity()) {
                        booking.setStatus(STATUS_BOOKED);
                        return bookingRepository.save(booking);
                    }

                    // capacité pleine -> FULL si waitlist ouverte
                    if (Boolean.TRUE.equals(event.getWaitlistOpen())) {
                        booking.setStatus(STATUS_FULL);
                        return bookingRepository.save(booking);
                    }

                    // capacité pleine et waitlist fermée -> refus
                    return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "Session is full and waitlist is closed"));
                });
            });
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
                if (booking.getUserId() != null) {
                    existingBooking.setUserId(booking.getUserId());
                }
                if (booking.getEventId() != null) {
                    existingBooking.setEventId(booking.getEventId());
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

    @Override
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

    @Override
    public Mono<Booking> cancel(Long id) {
        LOG.debug("Request to cancel Booking : {}", id);

        return bookingRepository
            .findById(id)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found")))
            .flatMap(booking ->
                eventRepository
                    .findById(booking.getEventId())
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Linked event not found")))
                    .flatMap(event -> {
                        ZonedDateTime now = ZonedDateTime.now();

                        booking.setStatus(STATUS_CANCELLED);
                        booking.setCancelledAt(now);

                        Mono<Booking> saveBookingMono = bookingRepository.save(booking);

                        Duration untilStart = Duration.between(now, event.getStartAt());
                        boolean penaltyApplies = !untilStart.isNegative() && untilStart.toHours() < PENALTY_WINDOW_HOURS;

                        if (!penaltyApplies) {
                            return saveBookingMono;
                        }

                        return saveBookingMono.flatMap(saved ->
                            userRepository
                                .findById(saved.getUserId())
                                .flatMap(user -> {
                                    long current = user.getBalanceCents() == null ? 0L : user.getBalanceCents();
                                    user.setBalanceCents(current - PENALTY_CENTS);
                                    return userRepository.save(user).thenReturn(saved);
                                })
                        );
                    })
            );
    }
}
