package com.pilates.booking.web.rest;

import com.pilates.booking.domain.PeriodSubscription;
import com.pilates.booking.repository.PeriodSubscriptionRepository;
import com.pilates.booking.service.PeriodSubscriptionService;
import com.pilates.booking.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.pilates.booking.domain.PeriodSubscription}.
 */
@RestController
@RequestMapping("/api/period-subscriptions")
public class PeriodSubscriptionResource {

    private static final Logger LOG = LoggerFactory.getLogger(PeriodSubscriptionResource.class);

    private static final String ENTITY_NAME = "periodSubscription";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PeriodSubscriptionService periodSubscriptionService;

    private final PeriodSubscriptionRepository periodSubscriptionRepository;

    public PeriodSubscriptionResource(
        PeriodSubscriptionService periodSubscriptionService,
        PeriodSubscriptionRepository periodSubscriptionRepository
    ) {
        this.periodSubscriptionService = periodSubscriptionService;
        this.periodSubscriptionRepository = periodSubscriptionRepository;
    }

    /**
     * {@code POST  /period-subscriptions} : Create a new periodSubscription.
     *
     * @param periodSubscription the periodSubscription to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new periodSubscription, or with status {@code 400 (Bad Request)} if the periodSubscription has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<PeriodSubscription>> createPeriodSubscription(@Valid @RequestBody PeriodSubscription periodSubscription)
        throws URISyntaxException {
        LOG.debug("REST request to save PeriodSubscription : {}", periodSubscription);
        if (periodSubscription.getId() != null) {
            throw new BadRequestAlertException("A new periodSubscription cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return periodSubscriptionService
            .save(periodSubscription)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/period-subscriptions/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /period-subscriptions/:id} : Updates an existing periodSubscription.
     *
     * @param id the id of the periodSubscription to save.
     * @param periodSubscription the periodSubscription to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated periodSubscription,
     * or with status {@code 400 (Bad Request)} if the periodSubscription is not valid,
     * or with status {@code 500 (Internal Server Error)} if the periodSubscription couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<PeriodSubscription>> updatePeriodSubscription(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PeriodSubscription periodSubscription
    ) throws URISyntaxException {
        LOG.debug("REST request to update PeriodSubscription : {}, {}", id, periodSubscription);
        if (periodSubscription.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, periodSubscription.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return periodSubscriptionRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return periodSubscriptionService
                    .update(periodSubscription)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /period-subscriptions/:id} : Partial updates given fields of an existing periodSubscription, field will ignore if it is null
     *
     * @param id the id of the periodSubscription to save.
     * @param periodSubscription the periodSubscription to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated periodSubscription,
     * or with status {@code 400 (Bad Request)} if the periodSubscription is not valid,
     * or with status {@code 404 (Not Found)} if the periodSubscription is not found,
     * or with status {@code 500 (Internal Server Error)} if the periodSubscription couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<PeriodSubscription>> partialUpdatePeriodSubscription(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PeriodSubscription periodSubscription
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PeriodSubscription partially : {}, {}", id, periodSubscription);
        if (periodSubscription.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, periodSubscription.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return periodSubscriptionRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<PeriodSubscription> result = periodSubscriptionService.partialUpdate(periodSubscription);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /period-subscriptions} : get all the periodSubscriptions.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of periodSubscriptions in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<PeriodSubscription>> getAllPeriodSubscriptions() {
        LOG.debug("REST request to get all PeriodSubscriptions");
        return periodSubscriptionService.findAll().collectList();
    }

    /**
     * {@code GET  /period-subscriptions} : get all the periodSubscriptions as a stream.
     * @return the {@link Flux} of periodSubscriptions.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<PeriodSubscription> getAllPeriodSubscriptionsAsStream() {
        LOG.debug("REST request to get all PeriodSubscriptions as a stream");
        return periodSubscriptionService.findAll();
    }

    /**
     * {@code GET  /period-subscriptions/:id} : get the "id" periodSubscription.
     *
     * @param id the id of the periodSubscription to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the periodSubscription, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<PeriodSubscription>> getPeriodSubscription(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PeriodSubscription : {}", id);
        Mono<PeriodSubscription> periodSubscription = periodSubscriptionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(periodSubscription);
    }

    /**
     * {@code DELETE  /period-subscriptions/:id} : delete the "id" periodSubscription.
     *
     * @param id the id of the periodSubscription to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deletePeriodSubscription(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PeriodSubscription : {}", id);
        return periodSubscriptionService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
