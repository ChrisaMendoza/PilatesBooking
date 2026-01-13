package com.pilates.booking.web.rest;

import com.pilates.booking.domain.Studio;
import com.pilates.booking.repository.StudioRepository;
import com.pilates.booking.service.StudioService;
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
 * REST controller for managing {@link com.pilates.booking.domain.Studio}.
 */
@RestController
@RequestMapping("/api/studios")
public class StudioResource {

    private static final Logger LOG = LoggerFactory.getLogger(StudioResource.class);

    private static final String ENTITY_NAME = "studio";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StudioService studioService;

    private final StudioRepository studioRepository;

    public StudioResource(StudioService studioService, StudioRepository studioRepository) {
        this.studioService = studioService;
        this.studioRepository = studioRepository;
    }

    /**
     * {@code POST  /studios} : Create a new studio.
     *
     * @param studio the studio to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new studio, or with status {@code 400 (Bad Request)} if the studio has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Studio>> createStudio(@Valid @RequestBody Studio studio) throws URISyntaxException {
        LOG.debug("REST request to save Studio : {}", studio);
        if (studio.getId() != null) {
            throw new BadRequestAlertException("A new studio cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return studioService
            .save(studio)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/studios/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /studios/:id} : Updates an existing studio.
     *
     * @param id the id of the studio to save.
     * @param studio the studio to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated studio,
     * or with status {@code 400 (Bad Request)} if the studio is not valid,
     * or with status {@code 500 (Internal Server Error)} if the studio couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Studio>> updateStudio(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Studio studio
    ) throws URISyntaxException {
        LOG.debug("REST request to update Studio : {}, {}", id, studio);
        if (studio.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, studio.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return studioRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return studioService
                    .update(studio)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /studios/:id} : Partial updates given fields of an existing studio, field will ignore if it is null
     *
     * @param id the id of the studio to save.
     * @param studio the studio to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated studio,
     * or with status {@code 400 (Bad Request)} if the studio is not valid,
     * or with status {@code 404 (Not Found)} if the studio is not found,
     * or with status {@code 500 (Internal Server Error)} if the studio couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Studio>> partialUpdateStudio(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Studio studio
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Studio partially : {}, {}", id, studio);
        if (studio.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, studio.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return studioRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Studio> result = studioService.partialUpdate(studio);

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
     * {@code GET  /studios} : get all the studios.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of studios in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Studio>> getAllStudios() {
        LOG.debug("REST request to get all Studios");
        return studioService.findAll().collectList();
    }

    /**
     * {@code GET  /studios} : get all the studios as a stream.
     * @return the {@link Flux} of studios.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Studio> getAllStudiosAsStream() {
        LOG.debug("REST request to get all Studios as a stream");
        return studioService.findAll();
    }

    /**
     * {@code GET  /studios/:id} : get the "id" studio.
     *
     * @param id the id of the studio to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the studio, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Studio>> getStudio(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Studio : {}", id);
        Mono<Studio> studio = studioService.findOne(id);
        return ResponseUtil.wrapOrNotFound(studio);
    }

    /**
     * {@code DELETE  /studios/:id} : delete the "id" studio.
     *
     * @param id the id of the studio to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteStudio(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Studio : {}", id);
        return studioService
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
