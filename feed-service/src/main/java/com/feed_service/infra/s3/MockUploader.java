package com.feed_service.infra.s3;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Component
@ConditionalOnProperty(name = "app.storage.type", havingValue = "mock", matchIfMissing = true)
@Slf4j
public class MockUploader implements S3Uploader {

    @Override
    public String upload(MultipartFile file, String directory) {
        try {
            log.info("Mock upload: name={}, size={}, contentType={}",
                    file.getOriginalFilename(), file.getSize(), file.getContentType());
        } catch (Exception e) {
            log.warn("Mock upload inspect failed", e);
        }
        String fileName = directory + UUID.randomUUID() + "_" + "_" + sanitize(file.getOriginalFilename());

        return "https://mock-storage.internal/" + fileName;
    }

    @Override
    public void delete(String fileUrl) {
        log.info("Mock delete: {}", fileUrl);
    }

    private String sanitize(String s) {
        if (s == null) return "file";
        return s.replaceAll("[^a-zA-Z0-9.\\-_]", "_");
    }
}
