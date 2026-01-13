package com.pilates.booking.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PeriodSubscriptionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static PeriodSubscription getPeriodSubscriptionSample1() {
        return new PeriodSubscription()
            .id(1L)
            .subscriptionName("subscriptionName1")
            .description("description1")
            .price(1)
            .billingPeriod("billingPeriod1")
            .creditsPerPeriod(1);
    }

    public static PeriodSubscription getPeriodSubscriptionSample2() {
        return new PeriodSubscription()
            .id(2L)
            .subscriptionName("subscriptionName2")
            .description("description2")
            .price(2)
            .billingPeriod("billingPeriod2")
            .creditsPerPeriod(2);
    }

    public static PeriodSubscription getPeriodSubscriptionRandomSampleGenerator() {
        return new PeriodSubscription()
            .id(longCount.incrementAndGet())
            .subscriptionName(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .price(intCount.incrementAndGet())
            .billingPeriod(UUID.randomUUID().toString())
            .creditsPerPeriod(intCount.incrementAndGet());
    }
}
