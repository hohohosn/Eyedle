package com.feed_service.application.service;

import com.common.exception.CustomException;
import com.common.response.ErrorCode;
import com.feed_service.domain.model.Feed;
import com.feed_service.domain.model.FeedTag;
import com.feed_service.domain.model.Tag;
import com.feed_service.domain.repository.FeedTagRepository;
import com.feed_service.domain.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TagService {

    private final TagRepository tagRepository;
    private final FeedTagRepository feedTagRepository;

    public void applyTags(Feed feed, List<String> tags){

        if(tags == null || tags.isEmpty()) return;

        for(String rawTag : tags){
            String tagName = rawTag.replace("#", "").toLowerCase();

            Tag tag = tagRepository.findByName(tagName)
                    .orElseGet(() -> tagRepository.save(new Tag(tagName)));

            tag.increaseCount();

            feedTagRepository.save(new FeedTag(feed, tag));

        }
    }

    public void updateTags(Feed feed, List<String> tags){
        feedTagRepository.deleteAllByFeed(feed);
        applyTags(feed, tags);
    }

}
