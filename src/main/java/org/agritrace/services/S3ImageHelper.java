package org.agritrace.services;

import javafx.scene.image.Image;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class S3ImageHelper {
    private static S3Presigner presigner;
    private static S3Client s3Client;
    private static String bucketName;
    private static Properties awsProps;
    
    // Cache for storing loaded images
    private static final ConcurrentHashMap<String, CachedImage> imageCache = new ConcurrentHashMap<>();
    
    // How long to keep images in cache (30 minutes)
    private static final long CACHE_DURATION_MS = 30 * 60 * 1000;
    
    static {
        initializeS3();
    }
    
    private static class CachedImage {
        final Image image;
        final long timestamp;
        
        CachedImage(Image image) {
            this.image = image;
            this.timestamp = System.currentTimeMillis();
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_DURATION_MS;
        }
    }
    
    private static void initializeS3() {
        try {
            awsProps = new Properties();
            awsProps.load(S3ImageHelper.class.getResourceAsStream("/aws.properties"));
            
            bucketName = awsProps.getProperty("aws.bucket.name");
            Region region = Region.of(awsProps.getProperty("aws.region"));
            
            AwsBasicCredentials credentials = AwsBasicCredentials.create(
                awsProps.getProperty("aws.access.key"),
                awsProps.getProperty("aws.secret.key")
            );
            
            s3Client = S3Client.builder()
                    .region(region)
                    .credentialsProvider(StaticCredentialsProvider.create(credentials))
                    .build();
            
            presigner = S3Presigner.builder()
                    .region(region)
                    .credentialsProvider(StaticCredentialsProvider.create(credentials))
                    .build();
            
            // Verify bucket access
            HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();
            s3Client.headBucket(headBucketRequest);
        } catch (Exception e) {
            System.err.println("Error initializing S3: " + e.getMessage());
        }
    }
    
    public static Image getImage(String objectKey) {
        // Check cache first
        CachedImage cached = imageCache.get(objectKey);
        if (cached != null && !cached.isExpired()) {
            return cached.image;
        }
        
        // If not in cache or expired, load from S3
        try {
            if (!doesObjectExist(objectKey)) {
                return null;
            }
            
            String presignedUrl = getPresignedUrl(objectKey);
            if (presignedUrl == null) {
                return null;
            }
            
            Image image = new Image(presignedUrl, true); // true enables background loading
            
            // Cache the new image
            imageCache.put(objectKey, new CachedImage(image));
            
            return image;
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            return null;
        }
    }
    
    public static void clearCache() {
        imageCache.clear();
    }
    
    private static String getPresignedUrl(String objectKey) {
        try {
            if (presigner == null || s3Client == null) {
                initializeS3();
                if (presigner == null || s3Client == null) {
                    return null;
                }
            }

            String key = objectKey.startsWith("services/") ? objectKey : "services/" + objectKey;

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(60))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
            return presignedRequest.url().toString();
        } catch (Exception e) {
            System.err.println("Error generating presigned URL: " + e.getMessage());
            return null;
        }
    }
    
    public static boolean doesObjectExist(String objectKey) {
        try {
            String key = objectKey.startsWith("services/") ? objectKey : "services/" + objectKey;
            
            HeadObjectRequest headRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            
            try {
                s3Client.headObject(headRequest);
                return true;
            } catch (NoSuchKeyException e) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
