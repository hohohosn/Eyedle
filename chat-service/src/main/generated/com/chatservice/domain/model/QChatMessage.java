package com.chatservice.domain.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QChatMessage is a Querydsl query type for ChatMessage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChatMessage extends EntityPathBase<ChatMessage> {

    private static final long serialVersionUID = -1208870850L;

    public static final QChatMessage chatMessage = new QChatMessage("chatMessage");

    public final com.common.database.QBaseTimeEntity _super = new com.common.database.QBaseTimeEntity(this);

    public final NumberPath<Long> chatRoomId = createNumber("chatRoomId", Long.class);

    public final EnumPath<ContentType> contentType = createEnum("contentType", ContentType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final BooleanPath isReported = createBoolean("isReported");

    public final StringPath messageContent = createString("messageContent");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QChatMessage(String variable) {
        super(ChatMessage.class, forVariable(variable));
    }

    public QChatMessage(Path<? extends ChatMessage> path) {
        super(path.getType(), path.getMetadata());
    }

    public QChatMessage(PathMetadata metadata) {
        super(ChatMessage.class, metadata);
    }

}

