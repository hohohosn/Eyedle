package com.feed_service.domain.repository;

import com.feed_service.domain.model.Feed;
import com.feed_service.domain.model.QFeed;
import com.feed_service.domain.model.QFeedTag;
import com.feed_service.domain.model.QTag;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class FeedRepositoryImpl implements FeedRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Feed> findFeeds(Pageable pageable) {

        //페이징은 ID만
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

        //실제 데이터는 fetch join
        List<Feed> feeds = queryFactory
                .selectDistinct(QFeed.feed)
                .from(QFeed.feed)
                .leftJoin(QFeed.feed.mediaList).fetchJoin()
                .leftJoin(QFeed.feed.feedTags, QFeedTag.feedTag).fetchJoin()
                .leftJoin(QFeedTag.feedTag.tag, QTag.tag).fetchJoin()
                .where(QFeed.feed.id.in(feedIds))
                .orderBy(QFeed.feed.createdAt.desc())
                .fetch();

        //count
        Long total = queryFactory
                .select(QFeed.feed.id.count())
                .from(QFeed.feed)
                .where(QFeed.feed.deleted.isFalse())
                .fetchOne();

        return new PageImpl<>(feeds, pageable, total);
    }
}
