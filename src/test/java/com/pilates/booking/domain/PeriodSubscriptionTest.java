package com.pilates.booking.domain;

import static com.pilates.booking.domain.PeriodSubscriptionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.pilates.booking.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PeriodSubscriptionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PeriodSubscription.class);
        PeriodSubscription periodSubscription1 = getPeriodSubscriptionSample1();
        PeriodSubscription periodSubscription2 = new PeriodSubscription();
        assertThat(periodSubscription1).isNotEqualTo(periodSubscription2);

        periodSubscription2.setId(periodSubscription1.getId());
        assertThat(periodSubscription1).isEqualTo(periodSubscription2);

        periodSubscription2 = getPeriodSubscriptionSample2();
        assertThat(periodSubscription1).isNotEqualTo(periodSubscription2);
    }
}
