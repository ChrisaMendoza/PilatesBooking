package com.pilates.booking.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PackTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Pack getPackSample1() {
        return new Pack()
            .id(1L)
            .packName("packName1")
            .description("description1")
            .price(1)
            .billingPeriod("billingPeriod1")
            .credits(1)
            .validityDays(1);
    }

    public static Pack getPackSample2() {
        return new Pack()
            .id(2L)
            .packName("packName2")
            .description("description2")
            .price(2)
            .billingPeriod("billingPeriod2")
            .credits(2)
            .validityDays(2);
    }

    public static Pack getPackRandomSampleGenerator() {
        return new Pack()
            .id(longCount.incrementAndGet())
            .packName(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .price(intCount.incrementAndGet())
            .billingPeriod(UUID.randomUUID().toString())
            .credits(intCount.incrementAndGet())
            .validityDays(intCount.incrementAndGet());
    }
}
