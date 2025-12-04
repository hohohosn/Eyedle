package com.common.database;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBaseTimeEntity is a Querydsl query type for BaseTimeEntity
 */
@Generated("com.querydsl.codegen.DefaultSupertypeSerializer")
public class QBaseTimeEntity extends EntityPathBase<BaseTimeEntity> {

    private static final long serialVersionUID = 1368965822L;

    public static final QBaseTimeEntity baseTimeEntity = new QBaseTimeEntity("baseTimeEntity");

    public final QBaseRootEntity _super = new QBaseRootEntity(this);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QBaseTimeEntity(String variable) {
        super(BaseTimeEntity.class, forVariable(variable));
    }

    public QBaseTimeEntity(Path<? extends BaseTimeEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBaseTimeEntity(PathMetadata metadata) {
        super(BaseTimeEntity.class, metadata);
    }

}

