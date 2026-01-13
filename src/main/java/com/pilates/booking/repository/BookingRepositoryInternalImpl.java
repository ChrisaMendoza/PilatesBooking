package com.pilates.booking.repository;

import com.pilates.booking.domain.Booking;
import com.pilates.booking.repository.rowmapper.BookingRowMapper;
import com.pilates.booking.repository.rowmapper.ClassSessionRowMapper;
import com.pilates.booking.repository.rowmapper.UserRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Booking entity.
 */
@SuppressWarnings("unused")
class BookingRepositoryInternalImpl extends SimpleR2dbcRepository<Booking, Long> implements BookingRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final UserRowMapper userMapper;
    private final ClassSessionRowMapper classsessionMapper;
    private final BookingRowMapper bookingMapper;

    private static final Table entityTable = Table.aliased("booking", EntityManager.ENTITY_ALIAS);
    private static final Table userTable = Table.aliased("jhi_user", "e_user");
    private static final Table classSessionTable = Table.aliased("class_session", "classSession");

    public BookingRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        UserRowMapper userMapper,
        ClassSessionRowMapper classsessionMapper,
        BookingRowMapper bookingMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Booking.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.userMapper = userMapper;
        this.classsessionMapper = classsessionMapper;
        this.bookingMapper = bookingMapper;
    }

    @Override
    public Flux<Booking> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Booking> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = BookingSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(UserSqlHelper.getColumns(userTable, "user"));
        columns.addAll(ClassSessionSqlHelper.getColumns(classSessionTable, "classSession"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(userTable)
            .on(Column.create("user_id", entityTable))
            .equals(Column.create("id", userTable))
            .leftOuterJoin(classSessionTable)
            .on(Column.create("class_session_id", entityTable))
            .equals(Column.create("id", classSessionTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Booking.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Booking> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Booking> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Booking process(Row row, RowMetadata metadata) {
        Booking entity = bookingMapper.apply(row, "e");
        entity.setUser(userMapper.apply(row, "user"));
        entity.setClassSession(classsessionMapper.apply(row, "classSession"));
        return entity;
    }

    @Override
    public <S extends Booking> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
