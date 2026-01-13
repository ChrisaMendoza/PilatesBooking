package com.pilates.booking.domain;

import static com.pilates.booking.domain.ClassSessionTestSamples.*;
import static com.pilates.booking.domain.ClassTypeTestSamples.*;
import static com.pilates.booking.domain.StudioTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.pilates.booking.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClassSessionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClassSession.class);
        ClassSession classSession1 = getClassSessionSample1();
        ClassSession classSession2 = new ClassSession();
        assertThat(classSession1).isNotEqualTo(classSession2);

        classSession2.setId(classSession1.getId());
        assertThat(classSession1).isEqualTo(classSession2);

        classSession2 = getClassSessionSample2();
        assertThat(classSession1).isNotEqualTo(classSession2);
    }

    @Test
    void studioTest() {
        ClassSession classSession = getClassSessionRandomSampleGenerator();
        Studio studioBack = getStudioRandomSampleGenerator();

        classSession.setStudio(studioBack);
        assertThat(classSession.getStudio()).isEqualTo(studioBack);

        classSession.studio(null);
        assertThat(classSession.getStudio()).isNull();
    }

    @Test
    void classTypeTest() {
        ClassSession classSession = getClassSessionRandomSampleGenerator();
        ClassType classTypeBack = getClassTypeRandomSampleGenerator();

        classSession.setClassType(classTypeBack);
        assertThat(classSession.getClassType()).isEqualTo(classTypeBack);

        classSession.classType(null);
        assertThat(classSession.getClassType()).isNull();
    }
}
