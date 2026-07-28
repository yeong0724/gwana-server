package com.gwana.server.common.utils;

import static com.gwana.server.common.enums.ErrorCode.*;
import com.gwana.server.common.exception.CustomException;
import org.apache.tika.Tika;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class Validate {
    private static final Tika tika = new Tika();

    // 허용 MIME → 저장 확장자 (확장자/Content-Type 모두 실제 판별값 기준으로 결정)
    private static final Map<String, String> MIME_TO_EXTENSION = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/gif", ".gif",
            "image/webp", ".webp"
    );

    // 업로드 허용 최상위 네임스페이스 + 경로 형식(영숫자/_/-, 하위경로 허용, 상위이동 금지)
    private static final Pattern FOLDER_PATH_PATTERN =
            Pattern.compile("^(images|temp)(/[A-Za-z0-9_-]+)*$");

    /**
     * 파일 검증 후 실제 판별된 MIME 타입을 반환한다.
     * (반환값을 저장 Content-Type/확장자 결정에 사용)
     */
    public static String validateFile(MultipartFile file, long maxFileSize) {
        if (file.isEmpty()) {
            throw new CustomException(FILE_EMPTY.getCode(), FILE_EMPTY.getMessage());
        }

        if (file.getSize() > maxFileSize) {
            throw new CustomException(FILE_SIZE_EXCEEDED.getCode(), FILE_SIZE_EXCEEDED.getMessage());
        }

        try {
            String mimeType = tika.detect(file.getInputStream());

            if (!MIME_TO_EXTENSION.containsKey(mimeType)) {
                throw new CustomException(NOT_ALLOWED_FILE_TYPE.getCode(), NOT_ALLOWED_FILE_TYPE.getMessage());
            }

            return mimeType;
        } catch (IOException e) {
            throw new CustomException(FILE_VALIDATION_ERROR.getCode(), FILE_VALIDATION_ERROR.getMessage());
        }
    }

    /**
     * 판별된 MIME 에 대응하는 저장 확장자 반환.
     */
    public static String extensionForMime(String mimeType) {
        return MIME_TO_EXTENSION.getOrDefault(mimeType, "");
    }

    /**
     * 업로드 폴더 경로 검증. 허용 네임스페이스(images/, temp/) 하위의
     * 안전한 경로만 허용하고, 상위 이동(..)·절대경로·이상문자를 차단한다.
     * 검증을 통과한(정규화된) 경로를 반환한다.
     */
    public static String validateFolderPath(String folderPath) {
        if (folderPath == null) {
            throw new CustomException(INVALID_FOLDER_PATH.getCode(), INVALID_FOLDER_PATH.getMessage());
        }

        String normalized = folderPath.strip();
        // 끝의 슬래시 제거
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }

        if (!FOLDER_PATH_PATTERN.matcher(normalized).matches()) {
            throw new CustomException(INVALID_FOLDER_PATH.getCode(), INVALID_FOLDER_PATH.getMessage());
        }

        return normalized;
    }

    public static String cleanHtml(String content) {
        Document doc = Jsoup.parse(content);

        for (Element img : doc.select("img[containerstyle]")) {
            String containerStyle = img.attr("containerstyle");
            if (containerStyle.contains("margin: 0px auto")) {
                String currentStyle = img.attr("style");
                img.attr("style", currentStyle + " margin-left: auto; margin-right: auto;");
            }
            img.removeAttr("containerstyle");
            img.removeAttr("wrapperstyle");
        }

        Safelist safelist = Safelist.basic()
                .addTags("img", "span")
                .addAttributes("img", "src", "alt", "title", "width", "height", "style")
                .addAttributes("span", "style")  // 폰트 색상, 크기용
                .addProtocols("img", "src", "https");

        return Jsoup.clean(doc.body().html(), safelist);
    }
}
