package com.pilates.booking.web.rest;

import static com.pilates.booking.domain.PackAsserts.*;
import static com.pilates.booking.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pilates.booking.IntegrationTest;
import com.pilates.booking.domain.Pack;
import com.pilates.booking.repository.EntityManager;
import com.pilates.booking.repository.PackRepository;
import com.pilates.booking.repository.UserRepository;
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
 * Integration tests for the {@link PackResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PackResourceIT {

    private static final String DEFAULT_PACK_NAME = "AAAAAAAAAA";
    private static final String UPDATED_PACK_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_PRICE = 1;
    private static final Integer UPDATED_PRICE = 2;

    private static final String DEFAULT_BILLING_PERIOD = "AAAAAAAAAA";
    private static final String UPDATED_BILLING_PERIOD = "BBBBBBBBBB";

    private static final Integer DEFAULT_CREDITS = 1;
    private static final Integer UPDATED_CREDITS = 2;

    private static final Integer DEFAULT_VALIDITY_DAYS = 1;
    private static final Integer UPDATED_VALIDITY_DAYS = 2;

    private static final String ENTITY_API_URL = "/api/packs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PackRepository packRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Pack pack;

    private Pack insertedPack;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pack createEntity() {
        return new Pack()
            .packName(DEFAULT_PACK_NAME)
            .description(DEFAULT_DESCRIPTION)
            .price(DEFAULT_PRICE)
            .billingPeriod(DEFAULT_BILLING_PERIOD)
            .credits(DEFAULT_CREDITS)
            .validityDays(DEFAULT_VALIDITY_DAYS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pack createUpdatedEntity() {
        return new Pack()
            .packName(UPDATED_PACK_NAME)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .billingPeriod(UPDATED_BILLING_PERIOD)
            .credits(UPDATED_CREDITS)
            .validityDays(UPDATED_VALIDITY_DAYS);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Pack.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        pack = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPack != null) {
            packRepository.delete(insertedPack).block();
            insertedPack = null;
        }
        deleteEntities(em);
        userRepository.deleteAllUserAuthorities().block();
        userRepository.deleteAll().block();
    }

    @Test
    void createPack() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Pack
        var returnedPack = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(pack))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Pack.class)
            .returnResult()
            .getResponseBody();

        // Validate the Pack in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertPackUpdatableFieldsEquals(returnedPack, getPersistedPack(returnedPack));

        insertedPack = returnedPack;
    }

    @Test
    void createPackWithExistingId() throws Exception {
        // Create the Pack with an existing ID
        pack.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(pack))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pack in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkPackNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pack.setPackName(null);

        // Create the Pack, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(pack))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pack.setPrice(null);

        // Create the Pack, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(pack))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCreditsIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pack.setCredits(null);

        // Create the Pack, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(pack))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllPacksAsStream() {
        // Initialize the database
        packRepository.save(pack).block();

        List<Pack> packList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Pack.class)
            .getResponseBody()
            .filter(pack::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(packList).isNotNull();
        assertThat(packList).hasSize(1);
        Pack testPack = packList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertPackAllPropertiesEquals(pack, testPack);
        assertPackUpdatableFieldsEquals(pack, testPack);
    }

    @Test
    void getAllPacks() {
        // Initialize the database
        insertedPack = packRepository.save(pack).block();

        // Get all the packList
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
            .value(hasItem(pack.getId().intValue()))
            .jsonPath("$.[*].packName")
            .value(hasItem(DEFAULT_PACK_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].price")
            .value(hasItem(DEFAULT_PRICE))
            .jsonPath("$.[*].billingPeriod")
            .value(hasItem(DEFAULT_BILLING_PERIOD))
            .jsonPath("$.[*].credits")
            .value(hasItem(DEFAULT_CREDITS))
            .jsonPath("$.[*].validityDays")
            .value(hasItem(DEFAULT_VALIDITY_DAYS));
    }

    @Test
    void getPack() {
        // Initialize the database
        insertedPack = packRepository.save(pack).block();

        // Get the pack
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, pack.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(pack.getId().intValue()))
            .jsonPath("$.packName")
            .value(is(DEFAULT_PACK_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.price")
            .value(is(DEFAULT_PRICE))
            .jsonPath("$.billingPeriod")
            .value(is(DEFAULT_BILLING_PERIOD))
            .jsonPath("$.credits")
            .value(is(DEFAULT_CREDITS))
            .jsonPath("$.validityDays")
            .value(is(DEFAULT_VALIDITY_DAYS));
    }

    @Test
    void getNonExistingPack() {
        // Get the pack
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingPack() throws Exception {
        // Initialize the database
        insertedPack = packRepository.save(pack).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pack
        Pack updatedPack = packRepository.findById(pack.getId()).block();
        updatedPack
            .packName(UPDATED_PACK_NAME)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .billingPeriod(UPDATED_BILLING_PERIOD)
            .credits(UPDATED_CREDITS)
            .validityDays(UPDATED_VALIDITY_DAYS);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedPack.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedPack))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Pack in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPackToMatchAllProperties(updatedPack);
    }

    @Test
    void putNonExistingPack() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pack.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, pack.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(pack))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pack in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPack() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pack.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(pack))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pack in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPack() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pack.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(pack))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Pack in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePackWithPatch() throws Exception {
        // Initialize the database
        insertedPack = packRepository.save(pack).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pack using partial update
        Pack partialUpdatedPack = new Pack();
        partialUpdatedPack.setId(pack.getId());

        partialUpdatedPack.description(UPDATED_DESCRIPTION).validityDays(UPDATED_VALIDITY_DAYS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPack.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedPack))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Pack in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPackUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedPack, pack), getPersistedPack(pack));
    }

    @Test
    void fullUpdatePackWithPatch() throws Exception {
        // Initialize the database
        insertedPack = packRepository.save(pack).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pack using partial update
        Pack partialUpdatedPack = new Pack();
        partialUpdatedPack.setId(pack.getId());

        partialUpdatedPack
            .packName(UPDATED_PACK_NAME)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .billingPeriod(UPDATED_BILLING_PERIOD)
            .credits(UPDATED_CREDITS)
            .validityDays(UPDATED_VALIDITY_DAYS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPack.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedPack))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Pack in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPackUpdatableFieldsEquals(partialUpdatedPack, getPersistedPack(partialUpdatedPack));
    }

    @Test
    void patchNonExistingPack() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pack.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, pack.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(pack))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pack in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPack() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pack.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(pack))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pack in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPack() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pack.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(pack))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Pack in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePack() {
        // Initialize the database
        insertedPack = packRepository.save(pack).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the pack
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, pack.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return packRepository.count().block();
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

    protected Pack getPersistedPack(Pack pack) {
        return packRepository.findById(pack.getId()).block();
    }

    protected void assertPersistedPackToMatchAllProperties(Pack expectedPack) {
        // Test fails because reactive api returns an empty object instead of null
        // assertPackAllPropertiesEquals(expectedPack, getPersistedPack(expectedPack));
        assertPackUpdatableFieldsEquals(expectedPack, getPersistedPack(expectedPack));
    }

    protected void assertPersistedPackToMatchUpdatableProperties(Pack expectedPack) {
        // Test fails because reactive api returns an empty object instead of null
        // assertPackAllUpdatablePropertiesEquals(expectedPack, getPersistedPack(expectedPack));
        assertPackUpdatableFieldsEquals(expectedPack, getPersistedPack(expectedPack));
    }
}
