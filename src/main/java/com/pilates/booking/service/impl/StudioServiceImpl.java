package com.pilates.booking.service.impl;

import com.pilates.booking.domain.Studio;
import com.pilates.booking.repository.StudioRepository;
import com.pilates.booking.service.StudioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.pilates.booking.domain.Studio}.
 */
@Service
@Transactional
public class StudioServiceImpl implements StudioService {

    private static final Logger LOG = LoggerFactory.getLogger(StudioServiceImpl.class);

    private final StudioRepository studioRepository;

    public StudioServiceImpl(StudioRepository studioRepository) {
        this.studioRepository = studioRepository;
    }

    @Override
    public Mono<Studio> save(Studio studio) {
        LOG.debug("Request to save Studio : {}", studio);
        return studioRepository.save(studio);
    }

    @Override
    public Mono<Studio> update(Studio studio) {
        LOG.debug("Request to update Studio : {}", studio);
        return studioRepository.save(studio);
    }

    @Override
    public Mono<Studio> partialUpdate(Studio studio) {
        LOG.debug("Request to partially update Studio : {}", studio);

        return studioRepository
            .findById(studio.getId())
            .map(existingStudio -> {
                if (studio.getName() != null) {
                    existingStudio.setName(studio.getName());
                }
                if (studio.getAddress() != null) {
                    existingStudio.setAddress(studio.getAddress());
                }
                if (studio.getCategory() != null) {
                    existingStudio.setCategory(studio.getCategory());
                }

                return existingStudio;
            })
            .flatMap(studioRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Studio> findAll() {
        LOG.debug("Request to get all Studios");
        return studioRepository.findAll();
    }

    public Mono<Long> countAll() {
        return studioRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Studio> findOne(Long id) {
        LOG.debug("Request to get Studio : {}", id);
        return studioRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Studio : {}", id);
        return studioRepository.deleteById(id);
    }
}
