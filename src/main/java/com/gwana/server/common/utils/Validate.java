package com.gwana.server.common.utils;

import static com.gwana.server.common.enums.ErrorCode.*;
import com.gwana.server.common.exception.CustomException;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Component
public class Validate {
    private static final Tika tika = new Tika();

    private static final List<String> ALLOWED_MIME_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp"
    );

    public static void validateFile(MultipartFile file, long maxFileSize) {
        if (file.isEmpty()) {
            throw new CustomException(FILE_EMPTY.getCode(), FILE_EMPTY.getMessage());
        }

        if (file.getSize() > maxFileSize) {
            throw new CustomException(FILE_SIZE_EXCEEDED.getCode(), FILE_SIZE_EXCEEDED.getMessage());
        }

        try {
            String mimeType = tika.detect(file.getInputStream());

            if (!ALLOWED_MIME_TYPES.contains(mimeType)) {
                throw new CustomException(NOT_ALLOWED_FILE_TYPE.getCode(), NOT_ALLOWED_FILE_TYPE.getMessage());
            }
        } catch (IOException e) {
            throw new CustomException(FILE_VALIDATION_ERROR.getCode(), FILE_VALIDATION_ERROR.getMessage());
        }
    }
}
