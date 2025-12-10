package com.feed_service.infra.s3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@ConditionalOnProperty(name = "app.storage.type", havingValue = "s3")
@RequiredArgsConstructor
@Slf4j
public class S3UploaderImpl implements S3Uploader {

    @Override
    public String upload(MultipartFile file, String directory) {

        throw new UnsupportedOperationException("S3UploaderImpl.upload not implemented yet");
    }

    @Override
    public void delete(String fileUrl) {

    }

}
