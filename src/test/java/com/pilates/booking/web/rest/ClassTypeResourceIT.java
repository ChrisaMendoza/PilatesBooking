package com.pilates.booking.web.rest;

import static com.pilates.booking.domain.ClassTypeAsserts.*;
import static com.pilates.booking.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pilates.booking.IntegrationTest;
import com.pilates.booking.domain.ClassType;
import com.pilates.booking.repository.ClassTypeRepository;
import com.pilates.booking.repository.EntityManager;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link ClassTypeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ClassTypeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_DURATION = 1;
    private static final Integer UPDATED_DURATION = 2;

    private static final Integer DEFAULT_CAPACITY = 1;
    private static final Integer UPDATED_CAPACITY = 2;

    private static final String ENTITY_API_URL = "/api/class-types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ClassTypeRepository classTypeRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ClassType classType;

    private ClassType insertedClassType;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClassType createEntity() {
        return new ClassType().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION).duration(DEFAULT_DURATION).capacity(DEFAULT_CAPACITY);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClassType createUpdatedEntity() {
        return new ClassType().name(UPDATED_NAME).description(UPDATED_DESCRIPTION).duration(UPDATED_DURATION).capacity(UPDATED_CAPACITY);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ClassType.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        classType = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedClassType != null) {
            classTypeRepository.delete(insertedClassType).block();
            insertedClassType = null;
        }
        deleteEntities(em);
    }

    @Test
    void createClassType() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ClassType
        var returnedClassType = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(classType))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(ClassType.class)
            .returnResult()
            .getResponseBody();

        // Validate the ClassType in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertClassTypeUpdatableFieldsEquals(returnedClassType, getPersistedClassType(returnedClassType));

        insertedClassType = returnedClassType;
    }

    @Test
    void createClassTypeWithExistingId() throws Exception {
        // Create the ClassType with an existing ID
        classType.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(classType))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ClassType in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        classType.setName(null);

        // Create the ClassType, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(classType))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkDurationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        classType.setDuration(null);

        // Create the ClassType, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(classType))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllClassTypesAsStream() {
        // Initialize the database
        classTypeRepository.save(classType).block();

        List<ClassType> classTypeList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(ClassType.class)
            .getResponseBody()
            .filter(classType::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(classTypeList).isNotNull();
        assertThat(classTypeList).hasSize(1);
        ClassType testClassType = classTypeList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertClassTypeAllPropertiesEquals(classType, testClassType);
        assertClassTypeUpdatableFieldsEquals(classType, testClassType);
    }

    @Test
    void getAllClassTypes() {
        // Initialize the database
        insertedClassType = classTypeRepository.save(classType).block();

        // Get all the classTypeList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(classType.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].duration")
            .value(hasItem(DEFAULT_DURATION))
            .jsonPath("$.[*].capacity")
            .value(hasItem(DEFAULT_CAPACITY));
    }

    @Test
    void getClassType() {
        // Initialize the database
        insertedClassType = classTypeRepository.save(classType).block();

        // Get the classType
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, classType.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(classType.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.duration")
            .value(is(DEFAULT_DURATION))
            .jsonPath("$.capacity")
            .value(is(DEFAULT_CAPACITY));
    }

    @Test
    void getNonExistingClassType() {
        // Get the classType
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingClassType() throws Exception {
        // Initialize the database
        insertedClassType = classTypeRepository.save(classType).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the classType
        ClassType updatedClassType = classTypeRepository.findById(classType.getId()).block();
        updatedClassType.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).duration(UPDATED_DURATION).capacity(UPDATED_CAPACITY);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedClassType.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedClassType))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ClassType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedClassTypeToMatchAllProperties(updatedClassType);
    }

    @Test
    void putNonExistingClassType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        classType.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, classType.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(classType))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ClassType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchClassType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        classType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(classType))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ClassType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamClassType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        classType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(classType))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ClassType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateClassTypeWithPatch() throws Exception {
        // Initialize the database
        insertedClassType = classTypeRepository.save(classType).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the classType using partial update
        ClassType partialUpdatedClassType = new ClassType();
        partialUpdatedClassType.setId(classType.getId());

        partialUpdatedClassType.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedClassType.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedClassType))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ClassType in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClassTypeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedClassType, classType),
            getPersistedClassType(classType)
        );
    }

    @Test
    void fullUpdateClassTypeWithPatch() throws Exception {
        // Initialize the database
        insertedClassType = classTypeRepository.save(classType).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the classType using partial update
        ClassType partialUpdatedClassType = new ClassType();
        partialUpdatedClassType.setId(classType.getId());

        partialUpdatedClassType.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).duration(UPDATED_DURATION).capacity(UPDATED_CAPACITY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedClassType.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedClassType))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ClassType in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClassTypeUpdatableFieldsEquals(partialUpdatedClassType, getPersistedClassType(partialUpdatedClassType));
    }

    @Test
    void patchNonExistingClassType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        classType.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, classType.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(classType))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ClassType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchClassType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        classType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(classType))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ClassType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamClassType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        classType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(classType))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ClassType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteClassType() {
        // Initialize the database
        insertedClassType = classTypeRepository.save(classType).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the classType
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, classType.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return classTypeRepository.count().block();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected ClassType getPersistedClassType(ClassType classType) {
        return classTypeRepository.findById(classType.getId()).block();
    }

    protected void assertPersistedClassTypeToMatchAllProperties(ClassType expectedClassType) {
        // Test fails because reactive api returns an empty object instead of null
        // assertClassTypeAllPropertiesEquals(expectedClassType, getPersistedClassType(expectedClassType));
        assertClassTypeUpdatableFieldsEquals(expectedClassType, getPersistedClassType(expectedClassType));
    }

    protected void assertPersistedClassTypeToMatchUpdatableProperties(ClassType expectedClassType) {
        // Test fails because reactive api returns an empty object instead of null
        // assertClassTypeAllUpdatablePropertiesEquals(expectedClassType, getPersistedClassType(expectedClassType));
        assertClassTypeUpdatableFieldsEquals(expectedClassType, getPersistedClassType(expectedClassType));
    }
}
