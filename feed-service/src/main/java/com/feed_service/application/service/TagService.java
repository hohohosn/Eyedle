package com.feed_service.application.service;

import com.feed_service.domain.model.Feed;
import com.feed_service.domain.model.FeedTag;
import com.feed_service.domain.model.Tag;
import com.feed_service.domain.repository.FeedTagRepository;
import com.feed_service.domain.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final FeedTagRepository feedTagRepository;

    @Transactional
    public void applyTags(Feed feed, List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return;
        }

        List<FeedTag> feedTags = new ArrayList<>();

        for (String rawTag : tags) {
            String tagName = rawTag.replace("#", "").trim().toLowerCase();

            if (tagName.isEmpty()) {
                continue;
            }

            Tag tag = tagRepository.findByName(tagName)
                    .orElseGet(() -> {
                        Tag newTag = new Tag(tagName);
                        return tagRepository.save(newTag);
                    });

            tag.increaseCount();
            tagRepository.save(tag);

            feedTags.add(new FeedTag(feed, tag));
        }

        if (!feedTags.isEmpty()) {
            feedTagRepository.saveAll(feedTags);
        }
    }

    @Transactional
    public void updateTags(Feed feed, List<String> tags) {
        //기존 태그 카운트 감소
        List<FeedTag> oldFeedTags = feedTagRepository.findByFeed(feed);

        for (FeedTag feedTag : oldFeedTags) {
            Tag tag = feedTag.getTag();
            tag.decreaseCount();
            tagRepository.save(tag);
        }

        //기존 연결 삭제
        feedTagRepository.deleteAllByFeed(feed);

        //새 태그 적용
        applyTags(feed, tags);
    }

    @Transactional(readOnly = true)
    public List<Tag> getTagsByFeed(Feed feed) {
        return feedTagRepository.findByFeed(feed)
                .stream()
                .map(FeedTag::getTag)
                .toList();
    }
}
