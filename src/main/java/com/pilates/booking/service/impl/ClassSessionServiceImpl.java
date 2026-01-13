package com.pilates.booking.service.impl;

import com.pilates.booking.domain.ClassSession;
import com.pilates.booking.repository.ClassSessionRepository;
import com.pilates.booking.service.ClassSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.pilates.booking.domain.ClassSession}.
 */
@Service
@Transactional
public class ClassSessionServiceImpl implements ClassSessionService {

    private static final Logger LOG = LoggerFactory.getLogger(ClassSessionServiceImpl.class);

    private final ClassSessionRepository classSessionRepository;

    public ClassSessionServiceImpl(ClassSessionRepository classSessionRepository) {
        this.classSessionRepository = classSessionRepository;
    }

    @Override
    public Mono<ClassSession> save(ClassSession classSession) {
        LOG.debug("Request to save ClassSession : {}", classSession);
        return classSessionRepository.save(classSession);
    }

    @Override
    public Mono<ClassSession> update(ClassSession classSession) {
        LOG.debug("Request to update ClassSession : {}", classSession);
        return classSessionRepository.save(classSession);
    }

    @Override
    public Mono<ClassSession> partialUpdate(ClassSession classSession) {
        LOG.debug("Request to partially update ClassSession : {}", classSession);

        return classSessionRepository
            .findById(classSession.getId())
            .map(existingClassSession -> {
                if (classSession.getCoachName() != null) {
                    existingClassSession.setCoachName(classSession.getCoachName());
                }
                if (classSession.getStartAt() != null) {
                    existingClassSession.setStartAt(classSession.getStartAt());
                }
                if (classSession.getEndAt() != null) {
                    existingClassSession.setEndAt(classSession.getEndAt());
                }
                if (classSession.getCapacity() != null) {
                    existingClassSession.setCapacity(classSession.getCapacity());
                }
                if (classSession.getStatus() != null) {
                    existingClassSession.setStatus(classSession.getStatus());
                }

                return existingClassSession;
            })
            .flatMap(classSessionRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ClassSession> findAll() {
        LOG.debug("Request to get all ClassSessions");
        return classSessionRepository.findAll();
    }

    public Mono<Long> countAll() {
        return classSessionRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ClassSession> findOne(Long id) {
        LOG.debug("Request to get ClassSession : {}", id);
        return classSessionRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete ClassSession : {}", id);
        return classSessionRepository.deleteById(id);
    }
}
