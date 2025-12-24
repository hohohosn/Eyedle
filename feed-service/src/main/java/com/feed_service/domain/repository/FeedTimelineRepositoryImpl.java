package com.feed_service.domain.repository;

import com.feed_service.domain.model.FeedTimeline;
import com.feed_service.domain.model.QFeedTimeline;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class FeedTimelineRepositoryImpl implements FeedTimelineRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public List<FeedTimeline> findTimeline(Long userId, LocalDateTime cursorCreatedAt, Long cursorId, int size) {

        BooleanExpression condition = QFeedTimeline.feedTimeline.userId.eq(userId);

        if (cursorCreatedAt != null && cursorId != null) {
            condition = condition.and(
                    QFeedTimeline.feedTimeline.createdAt.lt(cursorCreatedAt)
                            .or(
                                    QFeedTimeline.feedTimeline.createdAt.eq(cursorCreatedAt)
                                            .and(QFeedTimeline.feedTimeline.id.lt(cursorId))
                            )
            );
        }

        return queryFactory
                .selectFrom(QFeedTimeline.feedTimeline)
                .where(condition)
                .orderBy(QFeedTimeline.feedTimeline.createdAt.desc(),
                        QFeedTimeline.feedTimeline.id.desc())
                .limit(size)
                .fetch();
    }
}
