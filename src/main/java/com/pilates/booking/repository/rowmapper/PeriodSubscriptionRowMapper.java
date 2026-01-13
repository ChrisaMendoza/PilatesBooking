package com.pilates.booking.repository.rowmapper;

import com.pilates.booking.domain.PeriodSubscription;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link PeriodSubscription}, with proper type conversions.
 */
@Service
public class PeriodSubscriptionRowMapper implements BiFunction<Row, String, PeriodSubscription> {

    private final ColumnConverter converter;

    public PeriodSubscriptionRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link PeriodSubscription} stored in the database.
     */
    @Override
    public PeriodSubscription apply(Row row, String prefix) {
        PeriodSubscription entity = new PeriodSubscription();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setSubscriptionName(converter.fromRow(row, prefix + "_subscription_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setPrice(converter.fromRow(row, prefix + "_price", Integer.class));
        entity.setBillingPeriod(converter.fromRow(row, prefix + "_billing_period", String.class));
        entity.setCreditsPerPeriod(converter.fromRow(row, prefix + "_credits_per_period", Integer.class));
        entity.setStartDate(converter.fromRow(row, prefix + "_start_date", LocalDate.class));
        entity.setEndDate(converter.fromRow(row, prefix + "_end_date", LocalDate.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        return entity;
    }
}
