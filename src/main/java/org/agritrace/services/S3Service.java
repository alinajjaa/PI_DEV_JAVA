package org.agritrace.services;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

public class S3Service {
    private final S3Client s3Client;
    private final String bucketName;
    private final Region region;

    public S3Service(String accessKey, String secretKey, String bucketName, Region region) {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        this.s3Client = S3Client.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
        this.bucketName = bucketName;
        this.region = region;
    }

    public String uploadFile(File file) throws IOException {
        String key = generateUniqueKey(file.getName());
        String contentType = Files.probeContentType(file.toPath());
        
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .build();

        s3Client.putObject(request, RequestBody.fromFile(file));
        return key;
    }

    private String generateUniqueKey(String originalFilename) {
        String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        return "services/" + UUID.randomUUID() + extension;
    }
}
