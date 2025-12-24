package com.feed_service.domain.repository;

import com.feed_service.domain.model.Feed;
import com.feed_service.domain.model.QFeed;
import com.feed_service.domain.model.QFeedTag;
import com.feed_service.domain.model.QTag;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class FeedRepositoryImpl implements FeedRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Feed> findFeeds(Pageable pageable) {
        //페이징 처리를 위해 ID 목록만 먼저 조회
        List<Long> feedIds = queryFactory
                .select(QFeed.feed.id)
                .from(QFeed.feed)
                .where(QFeed.feed.deleted.isFalse())
                .orderBy(QFeed.feed.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (feedIds.isEmpty()) {
            return Page.empty(pageable);
        }
        
        List<Feed> content = queryFactory
                .selectDistinct(QFeed.feed)
                .from(QFeed.feed)
                .leftJoin(QFeed.feed.mediaList).fetchJoin()
                .leftJoin(QFeed.feed.feedTags, QFeedTag.feedTag).fetchJoin()
                .leftJoin(QFeedTag.feedTag.tag, QTag.tag).fetchJoin()
                .where(QFeed.feed.id.in(feedIds))
                .orderBy(QFeed.feed.createdAt.desc())
                .fetch();

        //전체 카운트 조회
        Long total = queryFactory
                .select(QFeed.feed.id.count())
                .from(QFeed.feed)
                .where(QFeed.feed.deleted.isFalse())
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    @Override
    public List<Feed> findRecentFeeds(LocalDateTime since, String keyword) {
        BooleanExpression baseCondition = QFeed.feed.deleted.isFalse()
                .and(QFeed.feed.updatedAt.goe(since));

        BooleanExpression keywordCondition = null;

        if (keyword != null && !keyword.isBlank()) {
            keywordCondition = QFeed.feed.content.containsIgnoreCase(keyword)
                    .or(QTag.tag.name.containsIgnoreCase(keyword));
        }

        return queryFactory
                .selectDistinct(QFeed.feed)
                .from(QFeed.feed)
                .leftJoin(QFeed.feed.feedTags, QFeedTag.feedTag).fetchJoin()
                .leftJoin(QFeedTag.feedTag.tag, QTag.tag).fetchJoin()
                .leftJoin(QFeed.feed.mediaList).fetchJoin()
                .where(baseCondition, keywordCondition)
                .orderBy(QFeed.feed.updatedAt.desc())
                .fetch();
    }

    @Override
    public List<Feed> findByIdsWithRelations(List<Long> feedIds) {
        if (feedIds == null || feedIds.isEmpty()) {
            return Collections.emptyList();
        }

        
        return queryFactory
                .selectDistinct(QFeed.feed)
                .from(QFeed.feed)
                .leftJoin(QFeed.feed.mediaList).fetchJoin()
                .leftJoin(QFeed.feed.feedTags, QFeedTag.feedTag).fetchJoin()
                .leftJoin(QFeedTag.feedTag.tag, QTag.tag).fetchJoin()
                .where(QFeed.feed.id.in(feedIds)
                        .and(QFeed.feed.deleted.isFalse()))
                .orderBy(QFeed.feed.createdAt.desc())
                .fetch();
    }
}