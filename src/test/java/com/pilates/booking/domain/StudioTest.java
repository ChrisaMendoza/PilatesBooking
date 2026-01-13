package com.pilates.booking.domain;

import static com.pilates.booking.domain.ClassSessionTestSamples.*;
import static com.pilates.booking.domain.StudioTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.pilates.booking.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class StudioTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Studio.class);
        Studio studio1 = getStudioSample1();
        Studio studio2 = new Studio();
        assertThat(studio1).isNotEqualTo(studio2);

        studio2.setId(studio1.getId());
        assertThat(studio1).isEqualTo(studio2);

        studio2 = getStudioSample2();
        assertThat(studio1).isNotEqualTo(studio2);
    }

    @Test
    void classSessionTest() {
        Studio studio = getStudioRandomSampleGenerator();
        ClassSession classSessionBack = getClassSessionRandomSampleGenerator();

        studio.addClassSession(classSessionBack);
        assertThat(studio.getClassSessions()).containsOnly(classSessionBack);
        assertThat(classSessionBack.getStudio()).isEqualTo(studio);

        studio.removeClassSession(classSessionBack);
        assertThat(studio.getClassSessions()).doesNotContain(classSessionBack);
        assertThat(classSessionBack.getStudio()).isNull();

        studio.classSessions(new HashSet<>(Set.of(classSessionBack)));
        assertThat(studio.getClassSessions()).containsOnly(classSessionBack);
        assertThat(classSessionBack.getStudio()).isEqualTo(studio);

        studio.setClassSessions(new HashSet<>());
        assertThat(studio.getClassSessions()).doesNotContain(classSessionBack);
        assertThat(classSessionBack.getStudio()).isNull();
    }
}
