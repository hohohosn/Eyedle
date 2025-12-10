//package com.feed_service.domain.repository;
//
//import com.feed_service.domain.model.Feed;
//import com.feed_service.domain.model.QFeed;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//
//import java.util.List;
//
//@RequiredArgsConstructor
//public class FeedRepositoryImpl implements FeedRepositoryCustom {
//
//    private final JPAQueryFactory queryFactory;
//
//    @Override
//    public Page<Feed> findFeeds(Pageable pageable) {
//
//        List<Feed> feeds = queryFactory
//                .selectFrom(QFeed.feed)
//                .where(QFeed.feed.isDeleted.eq(false))
//                .orderBy(QFeed.feed.createdAt.desc())
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();
//
//        long total = queryFactory
//                .select(QFeed.feed.count())
//                .from(QFeed.feed)
//                .where(QFeed.feed.isDeleted.eq(false))
//                .fetchOne();
//
//        return new PageImpl<>(feeds, pageable, total);
//    }
//}
