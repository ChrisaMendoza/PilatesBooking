package com.pilates.booking.web.rest;

import static com.pilates.booking.domain.ClassSessionAsserts.*;
import static com.pilates.booking.web.rest.TestUtil.createUpdateProxyForBean;
import static com.pilates.booking.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pilates.booking.IntegrationTest;
import com.pilates.booking.domain.ClassSession;
import com.pilates.booking.repository.ClassSessionRepository;
import com.pilates.booking.repository.EntityManager;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
 * Integration tests for the {@link ClassSessionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ClassSessionResourceIT {

    private static final String DEFAULT_COACH_NAME = "AAAAAAAAAA";
    private static final String UPDATED_COACH_NAME = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_START_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_START_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_END_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_END_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final Integer DEFAULT_CAPACITY = 1;
    private static final Integer UPDATED_CAPACITY = 2;

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/class-sessions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ClassSessionRepository classSessionRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ClassSession classSession;

    private ClassSession insertedClassSession;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClassSession createEntity() {
        return new ClassSession()
            .coachName(DEFAULT_COACH_NAME)
            .startAt(DEFAULT_START_AT)
            .endAt(DEFAULT_END_AT)
            .capacity(DEFAULT_CAPACITY)
            .status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClassSession createUpdatedEntity() {
        return new ClassSession()
            .coachName(UPDATED_COACH_NAME)
            .startAt(UPDATED_START_AT)
            .endAt(UPDATED_END_AT)
            .capacity(UPDATED_CAPACITY)
            .status(UPDATED_STATUS);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ClassSession.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        classSession = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedClassSession != null) {
            classSessionRepository.delete(insertedClassSession).block();
            insertedClassSession = null;
        }
        deleteEntities(em);
    }

    @Test
    void createClassSession() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ClassSession
        var returnedClassSession = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(classSession))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(ClassSession.class)
            .returnResult()
            .getResponseBody();

        // Validate the ClassSession in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertClassSessionUpdatableFieldsEquals(returnedClassSession, getPersistedClassSession(returnedClassSession));

        insertedClassSession = returnedClassSession;
    }

    @Test
    void createClassSessionWithExistingId() throws Exception {
        // Create the ClassSession with an existing ID
        classSession.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(classSession))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ClassSession in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkStartAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        classSession.setStartAt(null);

        // Create the ClassSession, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(classSession))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkEndAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        classSession.setEndAt(null);

        // Create the ClassSession, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(classSession))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCapacityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        classSession.setCapacity(null);

        // Create the ClassSession, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(classSession))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllClassSessionsAsStream() {
        // Initialize the database
        classSessionRepository.save(classSession).block();

        List<ClassSession> classSessionList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(ClassSession.class)
            .getResponseBody()
            .filter(classSession::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(classSessionList).isNotNull();
        assertThat(classSessionList).hasSize(1);
        ClassSession testClassSession = classSessionList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertClassSessionAllPropertiesEquals(classSession, testClassSession);
        assertClassSessionUpdatableFieldsEquals(classSession, testClassSession);
    }

    @Test
    void getAllClassSessions() {
        // Initialize the database
        insertedClassSession = classSessionRepository.save(classSession).block();

        // Get all the classSessionList
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
            .value(hasItem(classSession.getId().intValue()))
            .jsonPath("$.[*].coachName")
            .value(hasItem(DEFAULT_COACH_NAME))
            .jsonPath("$.[*].startAt")
            .value(hasItem(sameInstant(DEFAULT_START_AT)))
            .jsonPath("$.[*].endAt")
            .value(hasItem(sameInstant(DEFAULT_END_AT)))
            .jsonPath("$.[*].capacity")
            .value(hasItem(DEFAULT_CAPACITY))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS));
    }

    @Test
    void getClassSession() {
        // Initialize the database
        insertedClassSession = classSessionRepository.save(classSession).block();

        // Get the classSession
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, classSession.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(classSession.getId().intValue()))
            .jsonPath("$.coachName")
            .value(is(DEFAULT_COACH_NAME))
            .jsonPath("$.startAt")
            .value(is(sameInstant(DEFAULT_START_AT)))
            .jsonPath("$.endAt")
            .value(is(sameInstant(DEFAULT_END_AT)))
            .jsonPath("$.capacity")
            .value(is(DEFAULT_CAPACITY))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS));
    }

    @Test
    void getNonExistingClassSession() {
        // Get the classSession
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingClassSession() throws Exception {
        // Initialize the database
        insertedClassSession = classSessionRepository.save(classSession).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the classSession
        ClassSession updatedClassSession = classSessionRepository.findById(classSession.getId()).block();
        updatedClassSession
            .coachName(UPDATED_COACH_NAME)
            .startAt(UPDATED_START_AT)
            .endAt(UPDATED_END_AT)
            .capacity(UPDATED_CAPACITY)
            .status(UPDATED_STATUS);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedClassSession.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedClassSession))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ClassSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedClassSessionToMatchAllProperties(updatedClassSession);
    }

    @Test
    void putNonExistingClassSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        classSession.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, classSession.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(classSession))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ClassSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchClassSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        classSession.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(classSession))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ClassSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamClassSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        classSession.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(classSession))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ClassSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateClassSessionWithPatch() throws Exception {
        // Initialize the database
        insertedClassSession = classSessionRepository.save(classSession).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the classSession using partial update
        ClassSession partialUpdatedClassSession = new ClassSession();
        partialUpdatedClassSession.setId(classSession.getId());

        partialUpdatedClassSession.coachName(UPDATED_COACH_NAME).endAt(UPDATED_END_AT).status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedClassSession.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedClassSession))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ClassSession in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClassSessionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedClassSession, classSession),
            getPersistedClassSession(classSession)
        );
    }

    @Test
    void fullUpdateClassSessionWithPatch() throws Exception {
        // Initialize the database
        insertedClassSession = classSessionRepository.save(classSession).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the classSession using partial update
        ClassSession partialUpdatedClassSession = new ClassSession();
        partialUpdatedClassSession.setId(classSession.getId());

        partialUpdatedClassSession
            .coachName(UPDATED_COACH_NAME)
            .startAt(UPDATED_START_AT)
            .endAt(UPDATED_END_AT)
            .capacity(UPDATED_CAPACITY)
            .status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedClassSession.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedClassSession))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ClassSession in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClassSessionUpdatableFieldsEquals(partialUpdatedClassSession, getPersistedClassSession(partialUpdatedClassSession));
    }

    @Test
    void patchNonExistingClassSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        classSession.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, classSession.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(classSession))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ClassSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchClassSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        classSession.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(classSession))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ClassSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamClassSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        classSession.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(classSession))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ClassSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteClassSession() {
        // Initialize the database
        insertedClassSession = classSessionRepository.save(classSession).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the classSession
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, classSession.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return classSessionRepository.count().block();
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

    protected ClassSession getPersistedClassSession(ClassSession classSession) {
        return classSessionRepository.findById(classSession.getId()).block();
    }

    protected void assertPersistedClassSessionToMatchAllProperties(ClassSession expectedClassSession) {
        // Test fails because reactive api returns an empty object instead of null
        // assertClassSessionAllPropertiesEquals(expectedClassSession, getPersistedClassSession(expectedClassSession));
        assertClassSessionUpdatableFieldsEquals(expectedClassSession, getPersistedClassSession(expectedClassSession));
    }

    protected void assertPersistedClassSessionToMatchUpdatableProperties(ClassSession expectedClassSession) {
        // Test fails because reactive api returns an empty object instead of null
        // assertClassSessionAllUpdatablePropertiesEquals(expectedClassSession, getPersistedClassSession(expectedClassSession));
        assertClassSessionUpdatableFieldsEquals(expectedClassSession, getPersistedClassSession(expectedClassSession));
    }
}
