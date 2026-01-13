package com.pilates.booking.web.rest;

import com.pilates.booking.domain.ClassSession;
import com.pilates.booking.repository.ClassSessionRepository;
import com.pilates.booking.service.ClassSessionService;
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
 * REST controller for managing {@link com.pilates.booking.domain.ClassSession}.
 */
@RestController
@RequestMapping("/api/class-sessions")
public class ClassSessionResource {

    private static final Logger LOG = LoggerFactory.getLogger(ClassSessionResource.class);

    private static final String ENTITY_NAME = "classSession";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClassSessionService classSessionService;

    private final ClassSessionRepository classSessionRepository;

    public ClassSessionResource(ClassSessionService classSessionService, ClassSessionRepository classSessionRepository) {
        this.classSessionService = classSessionService;
        this.classSessionRepository = classSessionRepository;
    }

    /**
     * {@code POST  /class-sessions} : Create a new classSession.
     *
     * @param classSession the classSession to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new classSession, or with status {@code 400 (Bad Request)} if the classSession has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<ClassSession>> createClassSession(@Valid @RequestBody ClassSession classSession) throws URISyntaxException {
        LOG.debug("REST request to save ClassSession : {}", classSession);
        if (classSession.getId() != null) {
            throw new BadRequestAlertException("A new classSession cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return classSessionService
            .save(classSession)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/class-sessions/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /class-sessions/:id} : Updates an existing classSession.
     *
     * @param id the id of the classSession to save.
     * @param classSession the classSession to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated classSession,
     * or with status {@code 400 (Bad Request)} if the classSession is not valid,
     * or with status {@code 500 (Internal Server Error)} if the classSession couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<ClassSession>> updateClassSession(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ClassSession classSession
    ) throws URISyntaxException {
        LOG.debug("REST request to update ClassSession : {}, {}", id, classSession);
        if (classSession.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, classSession.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return classSessionRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return classSessionService
                    .update(classSession)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /class-sessions/:id} : Partial updates given fields of an existing classSession, field will ignore if it is null
     *
     * @param id the id of the classSession to save.
     * @param classSession the classSession to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated classSession,
     * or with status {@code 400 (Bad Request)} if the classSession is not valid,
     * or with status {@code 404 (Not Found)} if the classSession is not found,
     * or with status {@code 500 (Internal Server Error)} if the classSession couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ClassSession>> partialUpdateClassSession(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ClassSession classSession
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ClassSession partially : {}, {}", id, classSession);
        if (classSession.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, classSession.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return classSessionRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ClassSession> result = classSessionService.partialUpdate(classSession);

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
     * {@code GET  /class-sessions} : get all the classSessions.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of classSessions in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<ClassSession>> getAllClassSessions() {
        LOG.debug("REST request to get all ClassSessions");
        return classSessionService.findAll().collectList();
    }

    /**
     * {@code GET  /class-sessions} : get all the classSessions as a stream.
     * @return the {@link Flux} of classSessions.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ClassSession> getAllClassSessionsAsStream() {
        LOG.debug("REST request to get all ClassSessions as a stream");
        return classSessionService.findAll();
    }

    /**
     * {@code GET  /class-sessions/:id} : get the "id" classSession.
     *
     * @param id the id of the classSession to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the classSession, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ClassSession>> getClassSession(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ClassSession : {}", id);
        Mono<ClassSession> classSession = classSessionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(classSession);
    }

    /**
     * {@code DELETE  /class-sessions/:id} : delete the "id" classSession.
     *
     * @param id the id of the classSession to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteClassSession(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ClassSession : {}", id);
        return classSessionService
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
