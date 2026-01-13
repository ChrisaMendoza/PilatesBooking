package com.pilates.booking.web.rest;

import static com.pilates.booking.domain.PeriodSubscriptionAsserts.*;
import static com.pilates.booking.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pilates.booking.IntegrationTest;
import com.pilates.booking.domain.PeriodSubscription;
import com.pilates.booking.repository.EntityManager;
import com.pilates.booking.repository.PeriodSubscriptionRepository;
import com.pilates.booking.repository.UserRepository;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link PeriodSubscriptionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PeriodSubscriptionResourceIT {

    private static final String DEFAULT_SUBSCRIPTION_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SUBSCRIPTION_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_PRICE = 1;
    private static final Integer UPDATED_PRICE = 2;

    private static final String DEFAULT_BILLING_PERIOD = "AAAAAAAAAA";
    private static final String UPDATED_BILLING_PERIOD = "BBBBBBBBBB";

    private static final Integer DEFAULT_CREDITS_PER_PERIOD = 1;
    private static final Integer UPDATED_CREDITS_PER_PERIOD = 2;

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/period-subscriptions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PeriodSubscriptionRepository periodSubscriptionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private PeriodSubscription periodSubscription;

    private PeriodSubscription insertedPeriodSubscription;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PeriodSubscription createEntity() {
        return new PeriodSubscription()
            .subscriptionName(DEFAULT_SUBSCRIPTION_NAME)
            .description(DEFAULT_DESCRIPTION)
            .price(DEFAULT_PRICE)
            .billingPeriod(DEFAULT_BILLING_PERIOD)
            .creditsPerPeriod(DEFAULT_CREDITS_PER_PERIOD)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PeriodSubscription createUpdatedEntity() {
        return new PeriodSubscription()
            .subscriptionName(UPDATED_SUBSCRIPTION_NAME)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .billingPeriod(UPDATED_BILLING_PERIOD)
            .creditsPerPeriod(UPDATED_CREDITS_PER_PERIOD)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(PeriodSubscription.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        periodSubscription = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPeriodSubscription != null) {
            periodSubscriptionRepository.delete(insertedPeriodSubscription).block();
            insertedPeriodSubscription = null;
        }
        deleteEntities(em);
        userRepository.deleteAllUserAuthorities().block();
        userRepository.deleteAll().block();
    }

    @Test
    void createPeriodSubscription() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PeriodSubscription
        var returnedPeriodSubscription = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(periodSubscription))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(PeriodSubscription.class)
            .returnResult()
            .getResponseBody();

        // Validate the PeriodSubscription in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertPeriodSubscriptionUpdatableFieldsEquals(
            returnedPeriodSubscription,
            getPersistedPeriodSubscription(returnedPeriodSubscription)
        );

        insertedPeriodSubscription = returnedPeriodSubscription;
    }

    @Test
    void createPeriodSubscriptionWithExistingId() throws Exception {
        // Create the PeriodSubscription with an existing ID
        periodSubscription.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(periodSubscription))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PeriodSubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkSubscriptionNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        periodSubscription.setSubscriptionName(null);

        // Create the PeriodSubscription, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(periodSubscription))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        periodSubscription.setPrice(null);

        // Create the PeriodSubscription, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(periodSubscription))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCreditsPerPeriodIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        periodSubscription.setCreditsPerPeriod(null);

        // Create the PeriodSubscription, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(periodSubscription))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllPeriodSubscriptionsAsStream() {
        // Initialize the database
        periodSubscriptionRepository.save(periodSubscription).block();

        List<PeriodSubscription> periodSubscriptionList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(PeriodSubscription.class)
            .getResponseBody()
            .filter(periodSubscription::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(periodSubscriptionList).isNotNull();
        assertThat(periodSubscriptionList).hasSize(1);
        PeriodSubscription testPeriodSubscription = periodSubscriptionList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertPeriodSubscriptionAllPropertiesEquals(periodSubscription, testPeriodSubscription);
        assertPeriodSubscriptionUpdatableFieldsEquals(periodSubscription, testPeriodSubscription);
    }

    @Test
    void getAllPeriodSubscriptions() {
        // Initialize the database
        insertedPeriodSubscription = periodSubscriptionRepository.save(periodSubscription).block();

        // Get all the periodSubscriptionList
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
            .value(hasItem(periodSubscription.getId().intValue()))
            .jsonPath("$.[*].subscriptionName")
            .value(hasItem(DEFAULT_SUBSCRIPTION_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].price")
            .value(hasItem(DEFAULT_PRICE))
            .jsonPath("$.[*].billingPeriod")
            .value(hasItem(DEFAULT_BILLING_PERIOD))
            .jsonPath("$.[*].creditsPerPeriod")
            .value(hasItem(DEFAULT_CREDITS_PER_PERIOD))
            .jsonPath("$.[*].startDate")
            .value(hasItem(DEFAULT_START_DATE.toString()))
            .jsonPath("$.[*].endDate")
            .value(hasItem(DEFAULT_END_DATE.toString()));
    }

    @Test
    void getPeriodSubscription() {
        // Initialize the database
        insertedPeriodSubscription = periodSubscriptionRepository.save(periodSubscription).block();

        // Get the periodSubscription
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, periodSubscription.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(periodSubscription.getId().intValue()))
            .jsonPath("$.subscriptionName")
            .value(is(DEFAULT_SUBSCRIPTION_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.price")
            .value(is(DEFAULT_PRICE))
            .jsonPath("$.billingPeriod")
            .value(is(DEFAULT_BILLING_PERIOD))
            .jsonPath("$.creditsPerPeriod")
            .value(is(DEFAULT_CREDITS_PER_PERIOD))
            .jsonPath("$.startDate")
            .value(is(DEFAULT_START_DATE.toString()))
            .jsonPath("$.endDate")
            .value(is(DEFAULT_END_DATE.toString()));
    }

    @Test
    void getNonExistingPeriodSubscription() {
        // Get the periodSubscription
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingPeriodSubscription() throws Exception {
        // Initialize the database
        insertedPeriodSubscription = periodSubscriptionRepository.save(periodSubscription).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the periodSubscription
        PeriodSubscription updatedPeriodSubscription = periodSubscriptionRepository.findById(periodSubscription.getId()).block();
        updatedPeriodSubscription
            .subscriptionName(UPDATED_SUBSCRIPTION_NAME)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .billingPeriod(UPDATED_BILLING_PERIOD)
            .creditsPerPeriod(UPDATED_CREDITS_PER_PERIOD)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedPeriodSubscription.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedPeriodSubscription))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PeriodSubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPeriodSubscriptionToMatchAllProperties(updatedPeriodSubscription);
    }

    @Test
    void putNonExistingPeriodSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        periodSubscription.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, periodSubscription.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(periodSubscription))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PeriodSubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPeriodSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        periodSubscription.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(periodSubscription))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PeriodSubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPeriodSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        periodSubscription.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(periodSubscription))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PeriodSubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePeriodSubscriptionWithPatch() throws Exception {
        // Initialize the database
        insertedPeriodSubscription = periodSubscriptionRepository.save(periodSubscription).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the periodSubscription using partial update
        PeriodSubscription partialUpdatedPeriodSubscription = new PeriodSubscription();
        partialUpdatedPeriodSubscription.setId(periodSubscription.getId());

        partialUpdatedPeriodSubscription
            .subscriptionName(UPDATED_SUBSCRIPTION_NAME)
            .creditsPerPeriod(UPDATED_CREDITS_PER_PERIOD)
            .endDate(UPDATED_END_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPeriodSubscription.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedPeriodSubscription))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PeriodSubscription in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPeriodSubscriptionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPeriodSubscription, periodSubscription),
            getPersistedPeriodSubscription(periodSubscription)
        );
    }

    @Test
    void fullUpdatePeriodSubscriptionWithPatch() throws Exception {
        // Initialize the database
        insertedPeriodSubscription = periodSubscriptionRepository.save(periodSubscription).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the periodSubscription using partial update
        PeriodSubscription partialUpdatedPeriodSubscription = new PeriodSubscription();
        partialUpdatedPeriodSubscription.setId(periodSubscription.getId());

        partialUpdatedPeriodSubscription
            .subscriptionName(UPDATED_SUBSCRIPTION_NAME)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .billingPeriod(UPDATED_BILLING_PERIOD)
            .creditsPerPeriod(UPDATED_CREDITS_PER_PERIOD)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPeriodSubscription.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedPeriodSubscription))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PeriodSubscription in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPeriodSubscriptionUpdatableFieldsEquals(
            partialUpdatedPeriodSubscription,
            getPersistedPeriodSubscription(partialUpdatedPeriodSubscription)
        );
    }

    @Test
    void patchNonExistingPeriodSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        periodSubscription.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, periodSubscription.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(periodSubscription))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PeriodSubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPeriodSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        periodSubscription.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(periodSubscription))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PeriodSubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPeriodSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        periodSubscription.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(periodSubscription))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PeriodSubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePeriodSubscription() {
        // Initialize the database
        insertedPeriodSubscription = periodSubscriptionRepository.save(periodSubscription).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the periodSubscription
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, periodSubscription.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return periodSubscriptionRepository.count().block();
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

    protected PeriodSubscription getPersistedPeriodSubscription(PeriodSubscription periodSubscription) {
        return periodSubscriptionRepository.findById(periodSubscription.getId()).block();
    }

    protected void assertPersistedPeriodSubscriptionToMatchAllProperties(PeriodSubscription expectedPeriodSubscription) {
        // Test fails because reactive api returns an empty object instead of null
        // assertPeriodSubscriptionAllPropertiesEquals(expectedPeriodSubscription, getPersistedPeriodSubscription(expectedPeriodSubscription));
        assertPeriodSubscriptionUpdatableFieldsEquals(
            expectedPeriodSubscription,
            getPersistedPeriodSubscription(expectedPeriodSubscription)
        );
    }

    protected void assertPersistedPeriodSubscriptionToMatchUpdatableProperties(PeriodSubscription expectedPeriodSubscription) {
        // Test fails because reactive api returns an empty object instead of null
        // assertPeriodSubscriptionAllUpdatablePropertiesEquals(expectedPeriodSubscription, getPersistedPeriodSubscription(expectedPeriodSubscription));
        assertPeriodSubscriptionUpdatableFieldsEquals(
            expectedPeriodSubscription,
            getPersistedPeriodSubscription(expectedPeriodSubscription)
        );
    }
}
