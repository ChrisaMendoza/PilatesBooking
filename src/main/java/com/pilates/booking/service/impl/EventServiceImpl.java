package com.pilates.booking.service.impl;

import com.pilates.booking.domain.Event;
import com.pilates.booking.repository.EventRepository;
import com.pilates.booking.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.pilates.booking.domain.Event}.
 */
@Service
@Transactional
public class EventServiceImpl implements EventService {

    private static final Logger LOG = LoggerFactory.getLogger(EventServiceImpl.class);

    private final EventRepository eventRepository;

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public Mono<Event> save(Event event) {
        LOG.debug("Request to save Event : {}", event);
        return eventRepository.save(event);
    }

    @Override
    public Mono<Event> update(Event event) {
        LOG.debug("Request to update Event : {}", event);
        return eventRepository.save(event);
    }

    @Override
    public Mono<Event> partialUpdate(Event event) {
        LOG.debug("Request to partially update Event : {}", event);

        return eventRepository
            .findById(event.getId())
            .map(existingEvent -> {
                if (event.getCoachName() != null) {
                    existingEvent.setCoachName(event.getCoachName());
                }
                if (event.getStartAt() != null) {
                    existingEvent.setStartAt(event.getStartAt());
                }
                if (event.getEndAt() != null) {
                    existingEvent.setEndAt(event.getEndAt());
                }
                if (event.getCapacity() != null) {
                    existingEvent.setCapacity(event.getCapacity());
                }
                if (event.getStatus() != null) {
                    existingEvent.setStatus(event.getStatus());
                }

                return existingEvent;
            })
            .flatMap(eventRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Event> findAll() {
        LOG.debug("Request to get all Events");
        return eventRepository.findAll();
    }

    public Mono<Long> countAll() {
        return eventRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Event> findOne(Long id) {
        LOG.debug("Request to get Event : {}", id);
        return eventRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Event : {}", id);
        return eventRepository.deleteById(id);
    }
}
