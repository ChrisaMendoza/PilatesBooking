package com.pilates.booking.domain;

import static com.pilates.booking.domain.ClassTypeTestSamples.*;
import static com.pilates.booking.domain.EventTestSamples.*;
import static com.pilates.booking.domain.StudioTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.pilates.booking.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EventTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Event.class);
        Event event1 = getEventSample1();
        Event event2 = new Event();
        assertThat(event1).isNotEqualTo(event2);

        event2.setId(event1.getId());
        assertThat(event1).isEqualTo(event2);

        event2 = getEventSample2();
        assertThat(event1).isNotEqualTo(event2);
    }

    @Test
    void studioTest() {
        Event event = getEventRandomSampleGenerator();
        Studio studioBack = getStudioRandomSampleGenerator();

        event.setStudio(studioBack);
        assertThat(event.getStudio()).isEqualTo(studioBack);

        event.studio(null);
        assertThat(event.getStudio()).isNull();
    }

    @Test
    void classTypeTest() {
        Event event = getEventRandomSampleGenerator();
        ClassType classTypeBack = getClassTypeRandomSampleGenerator();

        event.setClassType(classTypeBack);
        assertThat(event.getClassType()).isEqualTo(classTypeBack);

        event.classType(null);
        assertThat(event.getClassType()).isNull();
    }
}
