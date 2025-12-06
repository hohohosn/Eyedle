package com.chatservice.domain.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QChatParticipate is a Querydsl query type for ChatParticipate
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChatParticipate extends EntityPathBase<ChatParticipate> {

    private static final long serialVersionUID = 645928085L;

    public static final QChatParticipate chatParticipate = new QChatParticipate("chatParticipate");

    public final com.common.database.QBaseTimeEntity _super = new com.common.database.QBaseTimeEntity(this);

    public final NumberPath<Long> chatRoomId = createNumber("chatRoomId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final BooleanPath isLeft = createBoolean("isLeft");

    public final DateTimePath<java.time.LocalDateTime> leftAt = createDateTime("leftAt", java.time.LocalDateTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QChatParticipate(String variable) {
        super(ChatParticipate.class, forVariable(variable));
    }

    public QChatParticipate(Path<? extends ChatParticipate> path) {
        super(path.getType(), path.getMetadata());
    }

    public QChatParticipate(PathMetadata metadata) {
        super(ChatParticipate.class, metadata);
    }

}

