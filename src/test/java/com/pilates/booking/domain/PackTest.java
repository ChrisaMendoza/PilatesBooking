package com.pilates.booking.domain;

import static com.pilates.booking.domain.PackTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.pilates.booking.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PackTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Pack.class);
        Pack pack1 = getPackSample1();
        Pack pack2 = new Pack();
        assertThat(pack1).isNotEqualTo(pack2);

        pack2.setId(pack1.getId());
        assertThat(pack1).isEqualTo(pack2);

        pack2 = getPackSample2();
        assertThat(pack1).isNotEqualTo(pack2);
    }
}
