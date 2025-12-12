package com.feed_service.infra.s3;

public interface S3Uploader {

    String upload(org.springframework.web.multipart.MultipartFile file, String directory);

    void delete(String fileUrl);
}
