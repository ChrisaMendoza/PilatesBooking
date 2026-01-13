package com.pilates.booking.service.impl;

import com.pilates.booking.domain.Pack;
import com.pilates.booking.repository.PackRepository;
import com.pilates.booking.service.PackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.pilates.booking.domain.Pack}.
 */
@Service
@Transactional
public class PackServiceImpl implements PackService {

    private static final Logger LOG = LoggerFactory.getLogger(PackServiceImpl.class);

    private final PackRepository packRepository;

    public PackServiceImpl(PackRepository packRepository) {
        this.packRepository = packRepository;
    }

    @Override
    public Mono<Pack> save(Pack pack) {
        LOG.debug("Request to save Pack : {}", pack);
        return packRepository.save(pack);
    }

    @Override
    public Mono<Pack> update(Pack pack) {
        LOG.debug("Request to update Pack : {}", pack);
        return packRepository.save(pack);
    }

    @Override
    public Mono<Pack> partialUpdate(Pack pack) {
        LOG.debug("Request to partially update Pack : {}", pack);

        return packRepository
            .findById(pack.getId())
            .map(existingPack -> {
                if (pack.getPackName() != null) {
                    existingPack.setPackName(pack.getPackName());
                }
                if (pack.getDescription() != null) {
                    existingPack.setDescription(pack.getDescription());
                }
                if (pack.getPrice() != null) {
                    existingPack.setPrice(pack.getPrice());
                }
                if (pack.getBillingPeriod() != null) {
                    existingPack.setBillingPeriod(pack.getBillingPeriod());
                }
                if (pack.getCredits() != null) {
                    existingPack.setCredits(pack.getCredits());
                }
                if (pack.getValidityDays() != null) {
                    existingPack.setValidityDays(pack.getValidityDays());
                }

                return existingPack;
            })
            .flatMap(packRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Pack> findAll() {
        LOG.debug("Request to get all Packs");
        return packRepository.findAll();
    }

    public Mono<Long> countAll() {
        return packRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Pack> findOne(Long id) {
        LOG.debug("Request to get Pack : {}", id);
        return packRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Pack : {}", id);
        return packRepository.deleteById(id);
    }
}
