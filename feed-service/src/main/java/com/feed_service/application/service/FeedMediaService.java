package com.feed_service.application.service;

import com.common.exception.CustomException;
import com.common.response.ErrorCode;
import com.feed_service.domain.model.Feed;
import com.feed_service.domain.model.FeedMedia;
import com.feed_service.domain.model.MediaType;
import com.feed_service.domain.repository.FeedMediaRepository;
import com.feed_service.s3.S3Uploader;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FeedMediaService {

    private final FeedMediaRepository feedMediaRepository;
    private final S3Uploader s3Uploader;

    public void uploadMedias(Feed feed, List<MultipartFile> files) {

        if (files == null || files.isEmpty()) return;

        validateImages(files);

        List<FeedMedia> medias = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            String url = s3Uploader.upload(file, "feeds");

            medias.add(new FeedMedia(feed, url, MediaType.IMAGE, i));
        }

        feedMediaRepository.saveAll(medias);
    }

    public void replaceImages(Feed feed, List<MultipartFile> newFiles) {

        if (newFiles == null) return;

        List<FeedMedia> oldMedias =
                feedMediaRepository.findByFeedId(feed.getId());

        // 1. S3 삭제
        oldMedias.forEach(media -> s3Uploader.delete(media.getMediaUrl()));

        // 2. DB 삭제
        feedMediaRepository.deleteAll(oldMedias);

        // 3. 새 이미지 업로드
        uploadMedias(feed, newFiles);
    }

    private void validateImages(List<MultipartFile> files) {

        if (files.size() > 5) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        for (MultipartFile file : files) {

            if (file.getSize() > 5 * 1024 * 1024) {
                throw new CustomException(ErrorCode.BAD_REQUEST);
            }

            String type = file.getContentType();

            if (type == null || !(type.equals("image/jpeg")
                             || type.equals("image/png")
                             || type.equals("image/webp"))) {
                throw new CustomException(ErrorCode.BAD_REQUEST);
            }
        }
    }

    private MediaType detectMediaType(MultipartFile file){
        String contentType = file.getContentType();

        if(contentType == null) return MediaType.IMAGE;

        if(contentType.startsWith("video")) return MediaType.VIDEO;
        return MediaType.IMAGE;
    }
}