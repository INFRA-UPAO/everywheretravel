package com.everywhere.backend.service.aws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3DocumentService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${S3_DOCS_BUCKET:}")
    private String bucketName;

    public void uploadDocument(String key, byte[] content, String contentType) {
        if (bucketName == null || bucketName.isBlank()) {
            log.warn("S3_DOCS_BUCKET no configurado, omitiendo subida de documento: {}", key);
            return;
        }

        log.info("Subiendo documento a S3: bucket={}, key={}, size={} bytes", bucketName, key, content.length);

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .build();

        s3Client.putObject(putRequest, RequestBody.fromBytes(content));

        log.info("Documento subido exitosamente a S3: {}", key);
    }

    public String generatePresignedUrl(String key, Duration expiration) {
        if (bucketName == null || bucketName.isBlank()) {
            log.warn("S3_DOCS_BUCKET no configurado, no se puede generar URL pre-firmada para: {}", key);
            return null;
        }

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(expiration)
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

        String url = presignedRequest.url().toString();
        log.info("URL pre-firmada generada para {}: expira en {}", key, expiration);

        return url;
    }
}
