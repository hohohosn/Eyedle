package com.feed_service.application.service;

import com.common.exception.CustomException;
import com.common.response.ErrorCode;
import com.feed_service.domain.model.Feed;
import com.feed_service.domain.model.FeedMedia;
import com.feed_service.domain.repository.FeedRepository;
import com.feed_service.presentation.request.FeedCreateRequestDto;
import com.feed_service.presentation.request.FeedMediaUploadRequestDto;
import com.feed_service.presentation.request.FeedUpdateRequestDto;
import com.feed_service.presentation.response.FeedResponseDto;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedService {

    private final FeedRepository feedRepository;
    private final FeedMediaService feedMediaService;
    private final TagService tagService;

    @Transactional
    public Long createFeed(FeedCreateRequestDto request, List<MultipartFile> files, Long userId) {

        Feed feed = Feed.builder()
                .userId(userId)
                .content(request.getContent())
                .permission(request.getPermission())
                .build();

        feedRepository.save(feed);

//        if(request.getMedias() != null){
//            for(FeedMediaUploadRequestDto m : request.getMedias()){
//                FeedMedia media = new FeedMedia(feed, m.getMediaUrl(), m.getMediaType());
//                feed.getMediaList().add(media);
//            }
//        }
        feedMediaService.uploadMedias(feed, files);

        tagService.applyTags(feed, request.getTags());

        return feed.getId();
    }

    public FeedResponseDto findFeed(Long feedId) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        return FeedResponseDto.mapFeed(feed);
    }

//    public Page<FeedResponseDto> findAllFeeds(Pageable pageable) {
//        Page<Feed> feeds = feedRepository.findFeeds(pageable);
//        return feeds.map(FeedResponseDto::mapFeed);
//    }

    @Transactional
    public FeedResponseDto updateFeed(Long feedId, FeedUpdateRequestDto request) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        feed.updateFeed(request.getContent(), request.getPermission());

        tagService.updateTags(feed, request.getTags());

        return FeedResponseDto.mapFeed(feed);
    }

    @Transactional
    public void statusDeleted(Long feedId) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        feed.statusDeleted();
    }

}
