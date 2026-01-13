package com.pilates.booking.service.impl;

import com.pilates.booking.domain.PeriodSubscription;
import com.pilates.booking.repository.PeriodSubscriptionRepository;
import com.pilates.booking.service.PeriodSubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.pilates.booking.domain.PeriodSubscription}.
 */
@Service
@Transactional
public class PeriodSubscriptionServiceImpl implements PeriodSubscriptionService {

    private static final Logger LOG = LoggerFactory.getLogger(PeriodSubscriptionServiceImpl.class);

    private final PeriodSubscriptionRepository periodSubscriptionRepository;

    public PeriodSubscriptionServiceImpl(PeriodSubscriptionRepository periodSubscriptionRepository) {
        this.periodSubscriptionRepository = periodSubscriptionRepository;
    }

    @Override
    public Mono<PeriodSubscription> save(PeriodSubscription periodSubscription) {
        LOG.debug("Request to save PeriodSubscription : {}", periodSubscription);
        return periodSubscriptionRepository.save(periodSubscription);
    }

    @Override
    public Mono<PeriodSubscription> update(PeriodSubscription periodSubscription) {
        LOG.debug("Request to update PeriodSubscription : {}", periodSubscription);
        return periodSubscriptionRepository.save(periodSubscription);
    }

    @Override
    public Mono<PeriodSubscription> partialUpdate(PeriodSubscription periodSubscription) {
        LOG.debug("Request to partially update PeriodSubscription : {}", periodSubscription);

        return periodSubscriptionRepository
            .findById(periodSubscription.getId())
            .map(existingPeriodSubscription -> {
                if (periodSubscription.getSubscriptionName() != null) {
                    existingPeriodSubscription.setSubscriptionName(periodSubscription.getSubscriptionName());
                }
                if (periodSubscription.getDescription() != null) {
                    existingPeriodSubscription.setDescription(periodSubscription.getDescription());
                }
                if (periodSubscription.getPrice() != null) {
                    existingPeriodSubscription.setPrice(periodSubscription.getPrice());
                }
                if (periodSubscription.getBillingPeriod() != null) {
                    existingPeriodSubscription.setBillingPeriod(periodSubscription.getBillingPeriod());
                }
                if (periodSubscription.getCreditsPerPeriod() != null) {
                    existingPeriodSubscription.setCreditsPerPeriod(periodSubscription.getCreditsPerPeriod());
                }
                if (periodSubscription.getStartDate() != null) {
                    existingPeriodSubscription.setStartDate(periodSubscription.getStartDate());
                }
                if (periodSubscription.getEndDate() != null) {
                    existingPeriodSubscription.setEndDate(periodSubscription.getEndDate());
                }

                return existingPeriodSubscription;
            })
            .flatMap(periodSubscriptionRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<PeriodSubscription> findAll() {
        LOG.debug("Request to get all PeriodSubscriptions");
        return periodSubscriptionRepository.findAll();
    }

    public Mono<Long> countAll() {
        return periodSubscriptionRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<PeriodSubscription> findOne(Long id) {
        LOG.debug("Request to get PeriodSubscription : {}", id);
        return periodSubscriptionRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete PeriodSubscription : {}", id);
        return periodSubscriptionRepository.deleteById(id);
    }
}
