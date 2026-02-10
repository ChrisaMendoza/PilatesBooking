package com.pilates.booking.web.rest;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pilates.booking.IntegrationTest;
import com.pilates.booking.domain.Booking;
import com.pilates.booking.domain.Event;
import com.pilates.booking.domain.User;
import com.pilates.booking.repository.BookingRepository;
import com.pilates.booking.repository.EventRepository;
import com.pilates.booking.repository.UserRepository;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for Booking business rules.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
class BookingResourceIT {

    private static final String ENTITY_API_URL = "/api/bookings";

    private static final String STATUS_BOOKED = "BOOKED";
    private static final String STATUS_CANCELLED = "CANCELLED";
    private static final String STATUS_FULL = "FULL";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void cleanup() {
        bookingRepository.deleteAll().block();
        eventRepository.deleteAll().block();
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ROLE_ADMIN" })
    void createBooking_asAdmin_shouldSucceed() throws Exception {
        User user = getRegularUser();
        Event event = saveFutureEvent(10, false);

        Booking payload = new Booking();
        payload.setUserId(user.getId());
        payload.setEventId(event.getId());

        Booking created = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(payload))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Booking.class)
            .returnResult()
            .getResponseBody();

        assertThat(created).isNotNull();
        assertThat(created.getStatus()).isEqualTo(STATUS_BOOKED);
        assertThat(created.getEventId()).isEqualTo(event.getId());
        assertThat(created.getUserId()).isEqualTo(user.getId());
    }

    @Test
    @WithMockUser(username = "user", authorities = { "ROLE_USER" })
    void createBooking_asUser_shouldReturnForbidden() throws Exception {
        User user = getRegularUser();
        Event event = saveFutureEvent(10, false);

        Booking payload = new Booking();
        payload.setUserId(user.getId());
        payload.setEventId(event.getId());

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(payload))
            .exchange()
            .expectStatus()
            .isForbidden();
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ROLE_ADMIN" })
    void createBooking_whenEventInPast_shouldFail() throws Exception {
        User user = getRegularUser();
        Event event = savePastEvent(10, false);

        Booking payload = new Booking();
        payload.setUserId(user.getId());
        payload.setEventId(event.getId());

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(payload))
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ROLE_ADMIN" })
    void createBooking_whenEventFullAndWaitlistClosed_shouldFail() throws Exception {
        User user = getRegularUser();
        Event event = saveFutureEvent(1, false);

        // Remplit la session
        bookingRepository
            .save(new Booking().status(STATUS_BOOKED).createdAt(ZonedDateTime.now()).userId(user.getId()).eventId(event.getId()))
            .block();

        Booking payload = new Booking();
        payload.setUserId(user.getId());
        payload.setEventId(event.getId());

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(payload))
            .exchange()
            .expectStatus()
            .isEqualTo(409);
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ROLE_ADMIN" })
    void createBooking_whenEventFullAndWaitlistOpen_shouldCreateFullStatus() throws Exception {
        User user = getRegularUser();
        Event event = saveFutureEvent(1, true);

        // Remplit la session
        bookingRepository
            .save(new Booking().status(STATUS_BOOKED).createdAt(ZonedDateTime.now()).userId(user.getId()).eventId(event.getId()))
            .block();

        Booking payload = new Booking();
        payload.setUserId(user.getId());
        payload.setEventId(event.getId());

        Booking created = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(payload))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Booking.class)
            .returnResult()
            .getResponseBody();

        assertThat(created).isNotNull();
        assertThat(created.getStatus()).isEqualTo(STATUS_FULL);
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ROLE_ADMIN" })
    void cancelBooking_moreThan12h_noPenalty() {
        User user = getRegularUser();
        long initialBalance = user.getBalanceCents() == null ? 0L : user.getBalanceCents();

        Event event = saveEventFromNowHours(24, 1, false);
        Booking booking = bookingRepository
            .save(new Booking().status(STATUS_BOOKED).createdAt(ZonedDateTime.now()).userId(user.getId()).eventId(event.getId()))
            .block();

        Booking cancelled = webTestClient
            .post()
            .uri(ENTITY_API_URL + "/" + booking.getId() + "/cancel")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(Booking.class)
            .returnResult()
            .getResponseBody();

        assertThat(cancelled).isNotNull();
        assertThat(cancelled.getStatus()).isEqualTo(STATUS_CANCELLED);
        assertThat(cancelled.getCancelledAt()).isNotNull();

        User updatedUser = userRepository.findById(user.getId()).block();
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getBalanceCents()).isEqualTo(initialBalance);
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ROLE_ADMIN" })
    void cancelBooking_lessThan12h_applyMinus500Cents() {
        User user = getRegularUser();
        long initialBalance = user.getBalanceCents() == null ? 0L : user.getBalanceCents();

        Event event = saveEventFromNowHours(6, 1, false);
        Booking booking = bookingRepository
            .save(new Booking().status(STATUS_BOOKED).createdAt(ZonedDateTime.now()).userId(user.getId()).eventId(event.getId()))
            .block();

        Booking cancelled = webTestClient
            .post()
            .uri(ENTITY_API_URL + "/" + booking.getId() + "/cancel")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(Booking.class)
            .returnResult()
            .getResponseBody();

        assertThat(cancelled).isNotNull();
        assertThat(cancelled.getStatus()).isEqualTo(STATUS_CANCELLED);

        User updatedUser = userRepository.findById(user.getId()).block();
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getBalanceCents()).isEqualTo(initialBalance - 500L);
    }

    // ---------- helpers ----------

    private User getRegularUser() {
        User user = userRepository.findOneByLogin("user").block();
        if (user != null) {
            return user;
        }
        return userRepository.findAllByIdNotNull(PageRequest.of(0, 1)).blockFirst();
    }

    private Event saveFutureEvent(int capacity, boolean waitlistOpen) {
        return saveEventFromNowHours(24, capacity, waitlistOpen);
    }

    private Event savePastEvent(int capacity, boolean waitlistOpen) {
        ZonedDateTime start = ZonedDateTime.now().minusHours(24);
        ZonedDateTime end = start.plusHours(1);
        Event event = new Event()
            .coachName("Coach")
            .startAt(start)
            .endAt(end)
            .capacity(capacity)
            .status("OPEN")
            .waitlistOpen(waitlistOpen);
        return eventRepository.save(event).block();
    }

    private Event saveEventFromNowHours(int hoursFromNow, int capacity, boolean waitlistOpen) {
        ZonedDateTime start = ZonedDateTime.now().plusHours(hoursFromNow);
        ZonedDateTime end = start.plusHours(1);
        Event event = new Event()
            .coachName("Coach")
            .startAt(start)
            .endAt(end)
            .capacity(capacity)
            .status("OPEN")
            .waitlistOpen(waitlistOpen);
        return eventRepository.save(event).block();
    }
}
