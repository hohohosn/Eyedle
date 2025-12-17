//package com.feed_service.s3;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.UUID;
//
//@Component
//@RequiredArgsConstructor
//public class S3UploaderImpl implements S3Uploader {
//
//    private final S3Properties props;
//
//    @Override
//    public String upload(MultipartFile file, String dir) {
//        String fileName = dir + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
//
//        try {
//            PutObjectRequest putRequest = PutObjectRequest.builder()
//                    .bucket(props.getBucket())
//                    .key(fileName)
//                    .contentType(file.getContentType())
//                    .build();
//
//            s3Client.putObject(
//                    putRequest,
//                    RequestBody.fromBytes(file.getBytes())
//            );
//
//            return props.getBaseUrl() + "/" + fileName;
//
//        } catch (IOException e) {
//            throw new RuntimeException("S3 Upload failed", e);
//        }
//    }
//
//    @Override
//    public void delete(String s3Url) {
//        String key = s3Url.replace(props.getBaseUrl() + "/", "");
//
//        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
//                .bucket(props.getBucket())
//                .key(key)
//                .build();
//
//        s3Client.deleteObject(deleteRequest);
//    }
//}
