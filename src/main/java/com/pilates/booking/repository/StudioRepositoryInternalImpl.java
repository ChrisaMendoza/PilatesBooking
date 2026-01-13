package com.pilates.booking.repository;

import com.pilates.booking.domain.Studio;
import com.pilates.booking.repository.rowmapper.StudioRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoin;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Studio entity.
 */
@SuppressWarnings("unused")
class StudioRepositoryInternalImpl extends SimpleR2dbcRepository<Studio, Long> implements StudioRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final StudioRowMapper studioMapper;

    private static final Table entityTable = Table.aliased("studio", EntityManager.ENTITY_ALIAS);

    public StudioRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        StudioRowMapper studioMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Studio.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.studioMapper = studioMapper;
    }

    @Override
    public Flux<Studio> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Studio> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = StudioSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Studio.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Studio> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Studio> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Studio process(Row row, RowMetadata metadata) {
        Studio entity = studioMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends Studio> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
