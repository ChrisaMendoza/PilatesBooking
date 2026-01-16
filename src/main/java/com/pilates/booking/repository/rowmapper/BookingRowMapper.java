package com.pilates.booking.repository.rowmapper;

import com.pilates.booking.domain.Booking;
import io.r2dbc.spi.Row;
import java.time.ZonedDateTime;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Booking}, with proper type conversions.
 */
@Service
public class BookingRowMapper implements BiFunction<Row, String, Booking> {

    private final ColumnConverter converter;

    public BookingRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Booking} stored in the database.
     */
    @Override
    public Booking apply(Row row, String prefix) {
        Booking entity = new Booking();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", String.class));
        entity.setCreatedAt(converter.fromRow(row, prefix + "_created_at", ZonedDateTime.class));
        entity.setCancelledAt(converter.fromRow(row, prefix + "_cancelled_at", ZonedDateTime.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        entity.setEventId(converter.fromRow(row, prefix + "_event_id", Long.class));
        return entity;
    }
}
