package com.chatservice.domain.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QChatRoom is a Querydsl query type for ChatRoom
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChatRoom extends EntityPathBase<ChatRoom> {

    private static final long serialVersionUID = 27077636L;

    public static final QChatRoom chatRoom = new QChatRoom("chatRoom");

    public final com.common.database.QBaseTimeEntity _super = new com.common.database.QBaseTimeEntity(this);

    public final StringPath chatRoomName = createString("chatRoomName");

    public final EnumPath<ChatRoomStatus> chatRoomStatus = createEnum("chatRoomStatus", ChatRoomStatus.class);

    public final EnumPath<ChatRoomType> chatRoomType = createEnum("chatRoomType", ChatRoomType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QChatRoom(String variable) {
        super(ChatRoom.class, forVariable(variable));
    }

    public QChatRoom(Path<? extends ChatRoom> path) {
        super(path.getType(), path.getMetadata());
    }

    public QChatRoom(PathMetadata metadata) {
        super(ChatRoom.class, metadata);
    }

}

