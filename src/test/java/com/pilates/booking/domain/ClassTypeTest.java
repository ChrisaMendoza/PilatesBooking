package com.pilates.booking.domain;

import static com.pilates.booking.domain.ClassSessionTestSamples.*;
import static com.pilates.booking.domain.ClassTypeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.pilates.booking.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ClassTypeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClassType.class);
        ClassType classType1 = getClassTypeSample1();
        ClassType classType2 = new ClassType();
        assertThat(classType1).isNotEqualTo(classType2);

        classType2.setId(classType1.getId());
        assertThat(classType1).isEqualTo(classType2);

        classType2 = getClassTypeSample2();
        assertThat(classType1).isNotEqualTo(classType2);
    }

    @Test
    void classSessionTest() {
        ClassType classType = getClassTypeRandomSampleGenerator();
        ClassSession classSessionBack = getClassSessionRandomSampleGenerator();

        classType.addClassSession(classSessionBack);
        assertThat(classType.getClassSessions()).containsOnly(classSessionBack);
        assertThat(classSessionBack.getClassType()).isEqualTo(classType);

        classType.removeClassSession(classSessionBack);
        assertThat(classType.getClassSessions()).doesNotContain(classSessionBack);
        assertThat(classSessionBack.getClassType()).isNull();

        classType.classSessions(new HashSet<>(Set.of(classSessionBack)));
        assertThat(classType.getClassSessions()).containsOnly(classSessionBack);
        assertThat(classSessionBack.getClassType()).isEqualTo(classType);

        classType.setClassSessions(new HashSet<>());
        assertThat(classType.getClassSessions()).doesNotContain(classSessionBack);
        assertThat(classSessionBack.getClassType()).isNull();
    }
}
