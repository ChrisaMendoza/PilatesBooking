package com.pilates.booking.web.rest;

import com.pilates.booking.domain.Pack;
import com.pilates.booking.repository.PackRepository;
import com.pilates.booking.service.PackService;
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
 * REST controller for managing {@link com.pilates.booking.domain.Pack}.
 */
@RestController
@RequestMapping("/api/packs")
public class PackResource {

    private static final Logger LOG = LoggerFactory.getLogger(PackResource.class);

    private static final String ENTITY_NAME = "pack";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PackService packService;

    private final PackRepository packRepository;

    public PackResource(PackService packService, PackRepository packRepository) {
        this.packService = packService;
        this.packRepository = packRepository;
    }

    /**
     * {@code POST  /packs} : Create a new pack.
     *
     * @param pack the pack to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pack, or with status {@code 400 (Bad Request)} if the pack has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Pack>> createPack(@Valid @RequestBody Pack pack) throws URISyntaxException {
        LOG.debug("REST request to save Pack : {}", pack);
        if (pack.getId() != null) {
            throw new BadRequestAlertException("A new pack cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return packService
            .save(pack)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/packs/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /packs/:id} : Updates an existing pack.
     *
     * @param id the id of the pack to save.
     * @param pack the pack to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pack,
     * or with status {@code 400 (Bad Request)} if the pack is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pack couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Pack>> updatePack(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Pack pack
    ) throws URISyntaxException {
        LOG.debug("REST request to update Pack : {}, {}", id, pack);
        if (pack.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pack.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return packRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return packService
                    .update(pack)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /packs/:id} : Partial updates given fields of an existing pack, field will ignore if it is null
     *
     * @param id the id of the pack to save.
     * @param pack the pack to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pack,
     * or with status {@code 400 (Bad Request)} if the pack is not valid,
     * or with status {@code 404 (Not Found)} if the pack is not found,
     * or with status {@code 500 (Internal Server Error)} if the pack couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Pack>> partialUpdatePack(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Pack pack
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Pack partially : {}, {}", id, pack);
        if (pack.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pack.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return packRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Pack> result = packService.partialUpdate(pack);

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
     * {@code GET  /packs} : get all the packs.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of packs in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Pack>> getAllPacks() {
        LOG.debug("REST request to get all Packs");
        return packService.findAll().collectList();
    }

    /**
     * {@code GET  /packs} : get all the packs as a stream.
     * @return the {@link Flux} of packs.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Pack> getAllPacksAsStream() {
        LOG.debug("REST request to get all Packs as a stream");
        return packService.findAll();
    }

    /**
     * {@code GET  /packs/:id} : get the "id" pack.
     *
     * @param id the id of the pack to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pack, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Pack>> getPack(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Pack : {}", id);
        Mono<Pack> pack = packService.findOne(id);
        return ResponseUtil.wrapOrNotFound(pack);
    }

    /**
     * {@code DELETE  /packs/:id} : delete the "id" pack.
     *
     * @param id the id of the pack to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deletePack(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Pack : {}", id);
        return packService
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
