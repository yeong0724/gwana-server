package com.gwana.server.service;

import com.gwana.server.client.S3UploadClient;
import com.gwana.server.common.utils.Validate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class MypageService {
    private final S3UploadClient s3UploadClient;

    public String uploadProfileImage(MultipartFile multipartFile) {
        long maxFileSize = 1024 * 1024;
        Validate.validateFile(multipartFile, maxFileSize);

        String folderName = "profile";
        return s3UploadClient.uploadImage(multipartFile, folderName);
    }
}
