package com.pilates.booking.web.rest;

import com.pilates.booking.domain.ClassType;
import com.pilates.booking.repository.ClassTypeRepository;
import com.pilates.booking.service.ClassTypeService;
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
 * REST controller for managing {@link com.pilates.booking.domain.ClassType}.
 */
@RestController
@RequestMapping("/api/class-types")
public class ClassTypeResource {

    private static final Logger LOG = LoggerFactory.getLogger(ClassTypeResource.class);

    private static final String ENTITY_NAME = "classType";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClassTypeService classTypeService;

    private final ClassTypeRepository classTypeRepository;

    public ClassTypeResource(ClassTypeService classTypeService, ClassTypeRepository classTypeRepository) {
        this.classTypeService = classTypeService;
        this.classTypeRepository = classTypeRepository;
    }

    /**
     * {@code POST  /class-types} : Create a new classType.
     *
     * @param classType the classType to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new classType, or with status {@code 400 (Bad Request)} if the classType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<ClassType>> createClassType(@Valid @RequestBody ClassType classType) throws URISyntaxException {
        LOG.debug("REST request to save ClassType : {}", classType);
        if (classType.getId() != null) {
            throw new BadRequestAlertException("A new classType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return classTypeService
            .save(classType)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/class-types/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /class-types/:id} : Updates an existing classType.
     *
     * @param id the id of the classType to save.
     * @param classType the classType to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated classType,
     * or with status {@code 400 (Bad Request)} if the classType is not valid,
     * or with status {@code 500 (Internal Server Error)} if the classType couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<ClassType>> updateClassType(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ClassType classType
    ) throws URISyntaxException {
        LOG.debug("REST request to update ClassType : {}, {}", id, classType);
        if (classType.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, classType.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return classTypeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return classTypeService
                    .update(classType)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /class-types/:id} : Partial updates given fields of an existing classType, field will ignore if it is null
     *
     * @param id the id of the classType to save.
     * @param classType the classType to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated classType,
     * or with status {@code 400 (Bad Request)} if the classType is not valid,
     * or with status {@code 404 (Not Found)} if the classType is not found,
     * or with status {@code 500 (Internal Server Error)} if the classType couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ClassType>> partialUpdateClassType(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ClassType classType
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ClassType partially : {}, {}", id, classType);
        if (classType.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, classType.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return classTypeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ClassType> result = classTypeService.partialUpdate(classType);

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
     * {@code GET  /class-types} : get all the classTypes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of classTypes in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<ClassType>> getAllClassTypes() {
        LOG.debug("REST request to get all ClassTypes");
        return classTypeService.findAll().collectList();
    }

    /**
     * {@code GET  /class-types} : get all the classTypes as a stream.
     * @return the {@link Flux} of classTypes.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ClassType> getAllClassTypesAsStream() {
        LOG.debug("REST request to get all ClassTypes as a stream");
        return classTypeService.findAll();
    }

    /**
     * {@code GET  /class-types/:id} : get the "id" classType.
     *
     * @param id the id of the classType to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the classType, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ClassType>> getClassType(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ClassType : {}", id);
        Mono<ClassType> classType = classTypeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(classType);
    }

    /**
     * {@code DELETE  /class-types/:id} : delete the "id" classType.
     *
     * @param id the id of the classType to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteClassType(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ClassType : {}", id);
        return classTypeService
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
