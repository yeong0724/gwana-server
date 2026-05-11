package com.gwana.server.client;

import com.gwana.server.common.exception.CustomException;
import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import static com.gwana.server.common.enums.ErrorCode.*;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3UploadClient {
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * 이미지 업로드
     */
    public String uploadImage(MultipartFile file, String folder) {
        String originalFilename = file.getOriginalFilename();
        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String fileName = folder + "/" + TSID.Factory.getTsid() + extension;

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));

            return fileName;
        } catch (IOException e) {
            log.error("파일 읽기 실패: {}", e.getMessage());
            throw new CustomException(FILE_UPLOAD_FAILED.getCode(), FILE_UPLOAD_FAILED.getMessage());
        } catch (S3Exception e) {
            log.error("S3 서버 오류: {}", e.awsErrorDetails().errorMessage());
            throw new CustomException(S3_SERVER_ERROR.getCode(), S3_SERVER_ERROR.getMessage());
        } catch (SdkClientException e) {
            log.error("S3 연결 실패: {}", e.getMessage());
            throw new CustomException(S3_CONNECTION_ERROR.getCode(), S3_CONNECTION_ERROR.getMessage());
        }
    }

    /**
     * 이미지 삭제
     */
    public void deleteImage(String fileName) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .build();

        try {
            s3Client.deleteObject(request);
        } catch (RuntimeException e) {
            throw new CustomException(S3_SERVER_ERROR.getCode(), e.getMessage());
        }
    }

    /**
     * 이미지 복사 이동
     */
    public String moveImage(String sourceKey, String destFolder) {
        // 파일명 추출
        String fileName = sourceKey.substring(sourceKey.lastIndexOf("/") + 1);
        String destKey = destFolder + "/" + fileName;

        // 복사
        CopyObjectRequest copyRequest = CopyObjectRequest.builder()
                .sourceBucket(bucket)
                .sourceKey(sourceKey)
                .destinationBucket(bucket)
                .destinationKey(destKey)
                .build();

        s3Client.copyObject(copyRequest);

        // 원본 삭제
        // deleteImage(sourceKey);

        return destKey;
    }
}
