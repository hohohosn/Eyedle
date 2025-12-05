package com.common.database;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBaseRootEntity is a Querydsl query type for BaseRootEntity
 */
@Generated("com.querydsl.codegen.DefaultSupertypeSerializer")
public class QBaseRootEntity extends EntityPathBase<BaseRootEntity> {

    private static final long serialVersionUID = -874492653L;

    public static final QBaseRootEntity baseRootEntity = new QBaseRootEntity("baseRootEntity");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QBaseRootEntity(String variable) {
        super(BaseRootEntity.class, forVariable(variable));
    }

    public QBaseRootEntity(Path<? extends BaseRootEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBaseRootEntity(PathMetadata metadata) {
        super(BaseRootEntity.class, metadata);
    }

}

