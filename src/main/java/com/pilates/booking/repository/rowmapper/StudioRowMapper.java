package com.pilates.booking.repository.rowmapper;

import com.pilates.booking.domain.Studio;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Studio}, with proper type conversions.
 */
@Service
public class StudioRowMapper implements BiFunction<Row, String, Studio> {

    private final ColumnConverter converter;

    public StudioRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Studio} stored in the database.
     */
    @Override
    public Studio apply(Row row, String prefix) {
        Studio entity = new Studio();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setAddress(converter.fromRow(row, prefix + "_address", String.class));
        entity.setCategory(converter.fromRow(row, prefix + "_category", String.class));
        return entity;
    }
}
