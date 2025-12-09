package com.feed_service.application.service;

import com.feed_service.domain.model.Tag;
import com.feed_service.domain.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagQueryService {

    private final TagRepository tagRepository;

    public List<String> getPopularTags() {
        return tagRepository.findAll().stream()
                .sorted((a, b) -> Long.compare(b.getCount(), a.getCount()))
                .limit(20)
                .map(Tag::getName)
                .toList();
    }
}
