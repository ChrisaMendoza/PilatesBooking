package com.pilates.booking.web.rest;

import static com.pilates.booking.domain.StudioAsserts.*;
import static com.pilates.booking.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pilates.booking.IntegrationTest;
import com.pilates.booking.domain.Studio;
import com.pilates.booking.repository.EntityManager;
import com.pilates.booking.repository.StudioRepository;
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
 * Integration tests for the {@link StudioResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class StudioResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_CATEGORY = "AAAAAAAAAA";
    private static final String UPDATED_CATEGORY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/studios";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StudioRepository studioRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Studio studio;

    private Studio insertedStudio;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Studio createEntity() {
        return new Studio().name(DEFAULT_NAME).address(DEFAULT_ADDRESS).category(DEFAULT_CATEGORY);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Studio createUpdatedEntity() {
        return new Studio().name(UPDATED_NAME).address(UPDATED_ADDRESS).category(UPDATED_CATEGORY);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Studio.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        studio = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedStudio != null) {
            studioRepository.delete(insertedStudio).block();
            insertedStudio = null;
        }
        deleteEntities(em);
    }

    @Test
    void createStudio() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Studio
        var returnedStudio = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(studio))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Studio.class)
            .returnResult()
            .getResponseBody();

        // Validate the Studio in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertStudioUpdatableFieldsEquals(returnedStudio, getPersistedStudio(returnedStudio));

        insertedStudio = returnedStudio;
    }

    @Test
    void createStudioWithExistingId() throws Exception {
        // Create the Studio with an existing ID
        studio.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(studio))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Studio in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        studio.setName(null);

        // Create the Studio, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(studio))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllStudiosAsStream() {
        // Initialize the database
        studioRepository.save(studio).block();

        List<Studio> studioList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Studio.class)
            .getResponseBody()
            .filter(studio::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(studioList).isNotNull();
        assertThat(studioList).hasSize(1);
        Studio testStudio = studioList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertStudioAllPropertiesEquals(studio, testStudio);
        assertStudioUpdatableFieldsEquals(studio, testStudio);
    }

    @Test
    void getAllStudios() {
        // Initialize the database
        insertedStudio = studioRepository.save(studio).block();

        // Get all the studioList
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
            .value(hasItem(studio.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].address")
            .value(hasItem(DEFAULT_ADDRESS))
            .jsonPath("$.[*].category")
            .value(hasItem(DEFAULT_CATEGORY));
    }

    @Test
    void getStudio() {
        // Initialize the database
        insertedStudio = studioRepository.save(studio).block();

        // Get the studio
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, studio.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(studio.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.address")
            .value(is(DEFAULT_ADDRESS))
            .jsonPath("$.category")
            .value(is(DEFAULT_CATEGORY));
    }

    @Test
    void getNonExistingStudio() {
        // Get the studio
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingStudio() throws Exception {
        // Initialize the database
        insertedStudio = studioRepository.save(studio).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the studio
        Studio updatedStudio = studioRepository.findById(studio.getId()).block();
        updatedStudio.name(UPDATED_NAME).address(UPDATED_ADDRESS).category(UPDATED_CATEGORY);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedStudio.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedStudio))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Studio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStudioToMatchAllProperties(updatedStudio);
    }

    @Test
    void putNonExistingStudio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studio.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, studio.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(studio))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Studio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchStudio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studio.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(studio))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Studio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamStudio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studio.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(studio))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Studio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateStudioWithPatch() throws Exception {
        // Initialize the database
        insertedStudio = studioRepository.save(studio).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the studio using partial update
        Studio partialUpdatedStudio = new Studio();
        partialUpdatedStudio.setId(studio.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedStudio.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedStudio))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Studio in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStudioUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedStudio, studio), getPersistedStudio(studio));
    }

    @Test
    void fullUpdateStudioWithPatch() throws Exception {
        // Initialize the database
        insertedStudio = studioRepository.save(studio).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the studio using partial update
        Studio partialUpdatedStudio = new Studio();
        partialUpdatedStudio.setId(studio.getId());

        partialUpdatedStudio.name(UPDATED_NAME).address(UPDATED_ADDRESS).category(UPDATED_CATEGORY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedStudio.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedStudio))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Studio in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStudioUpdatableFieldsEquals(partialUpdatedStudio, getPersistedStudio(partialUpdatedStudio));
    }

    @Test
    void patchNonExistingStudio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studio.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, studio.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(studio))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Studio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchStudio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studio.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(studio))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Studio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamStudio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studio.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(studio))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Studio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteStudio() {
        // Initialize the database
        insertedStudio = studioRepository.save(studio).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the studio
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, studio.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return studioRepository.count().block();
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

    protected Studio getPersistedStudio(Studio studio) {
        return studioRepository.findById(studio.getId()).block();
    }

    protected void assertPersistedStudioToMatchAllProperties(Studio expectedStudio) {
        // Test fails because reactive api returns an empty object instead of null
        // assertStudioAllPropertiesEquals(expectedStudio, getPersistedStudio(expectedStudio));
        assertStudioUpdatableFieldsEquals(expectedStudio, getPersistedStudio(expectedStudio));
    }

    protected void assertPersistedStudioToMatchUpdatableProperties(Studio expectedStudio) {
        // Test fails because reactive api returns an empty object instead of null
        // assertStudioAllUpdatablePropertiesEquals(expectedStudio, getPersistedStudio(expectedStudio));
        assertStudioUpdatableFieldsEquals(expectedStudio, getPersistedStudio(expectedStudio));
    }
}
