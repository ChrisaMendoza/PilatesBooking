package com.pilates.booking.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class StudioTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Studio getStudioSample1() {
        return new Studio().id(1L).name("name1").address("address1").category("category1");
    }

    public static Studio getStudioSample2() {
        return new Studio().id(2L).name("name2").address("address2").category("category2");
    }

    public static Studio getStudioRandomSampleGenerator() {
        return new Studio()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .address(UUID.randomUUID().toString())
            .category(UUID.randomUUID().toString());
    }
}
