package com.pilates.booking.repository.rowmapper;

import com.pilates.booking.domain.Pack;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Pack}, with proper type conversions.
 */
@Service
public class PackRowMapper implements BiFunction<Row, String, Pack> {

    private final ColumnConverter converter;

    public PackRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Pack} stored in the database.
     */
    @Override
    public Pack apply(Row row, String prefix) {
        Pack entity = new Pack();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setPackName(converter.fromRow(row, prefix + "_pack_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setPrice(converter.fromRow(row, prefix + "_price", Integer.class));
        entity.setBillingPeriod(converter.fromRow(row, prefix + "_billing_period", String.class));
        entity.setCredits(converter.fromRow(row, prefix + "_credits", Integer.class));
        entity.setValidityDays(converter.fromRow(row, prefix + "_validity_days", Integer.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        return entity;
    }
}
