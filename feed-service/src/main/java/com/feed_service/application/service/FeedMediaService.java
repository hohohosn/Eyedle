package com.feed_service.application.service;

import com.feed_service.domain.model.Feed;
import com.feed_service.domain.model.MediaType;
import com.feed_service.domain.repository.FeedMediaRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FeedMediaService {

    private final FeedMediaRepository feedMediaRepository;
//    private final S3Uploader s3Uploader;

    public void uploadMedias(Feed feed, List<MultipartFile> files){

        if(files == null || files.isEmpty()) return;

        for(MultipartFile file : files){
//            String url = s3Uploader.upload(file, "feeds");

            MediaType mediaType = detectMediaType(file);

//            FeedMedia media = new FeedMedia(feed, url, mediaType);
//            feedMediaRepository.save(media);
        }
    }

    private MediaType detectMediaType(MultipartFile file){
        String contentType = file.getContentType();

        if(contentType == null) return MediaType.IMAGE;

        if(contentType.startsWith("video")) return MediaType.VIDEO;
        return MediaType.IMAGE;
    }
}