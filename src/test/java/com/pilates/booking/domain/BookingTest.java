package com.pilates.booking.domain;

import static com.pilates.booking.domain.BookingTestSamples.*;
import static com.pilates.booking.domain.EventTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.pilates.booking.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BookingTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Booking.class);
        Booking booking1 = getBookingSample1();
        Booking booking2 = new Booking();
        assertThat(booking1).isNotEqualTo(booking2);

        booking2.setId(booking1.getId());
        assertThat(booking1).isEqualTo(booking2);

        booking2 = getBookingSample2();
        assertThat(booking1).isNotEqualTo(booking2);
    }

    @Test
    void eventTest() {
        Booking booking = getBookingRandomSampleGenerator();
        Event eventBack = getEventRandomSampleGenerator();

        booking.setEvent(eventBack);
        assertThat(booking.getEvent()).isEqualTo(eventBack);

        booking.event(null);
        assertThat(booking.getEvent()).isNull();
    }
}
