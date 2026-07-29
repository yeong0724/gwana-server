package com.gwana.server.service;

import com.gwana.server.client.S3UploadClient;
import com.gwana.server.common.enums.Role;
import com.gwana.server.common.exception.CommonException;
import com.gwana.server.common.exception.CustomException;
import com.gwana.server.common.security.JwtTokenProvider;
import com.gwana.server.common.utils.Validate;
import com.gwana.server.dto.AuthAware;
import com.gwana.server.dto.InfiniteResponse;
import com.gwana.server.dto.mypage.*;
import com.gwana.server.dto.user.AuthUser;
import com.gwana.server.dto.user.UserDto;
import com.gwana.server.mapper.MypageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.gwana.server.common.enums.ErrorCode.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class MypageService {
    private final S3UploadClient s3UploadClient;
    private final JwtTokenProvider jwtTokenProvider;
    private final MypageMapper mypageMapper;
    private final UserService userService;

    // 업로드 제한은 서버에서 고정 (클라이언트가 조정 불가)
    private static final long PROFILE_IMAGE_MAX_SIZE = 2 * 1024 * 1024;
    private static final long TEMP_IMAGE_MAX_SIZE = 5 * 1024 * 1024;
    private static final long IMAGES_MAX_SIZE = 3 * 1024 * 1024;
    private static final int IMAGES_MAX_COUNT = 5;

    // 문의 임시 이미지 키 형식: temp/inquiry/<파일명>.<확장자> 만 허용 (하위경로/상위이동 차단)
    private static final Pattern INQUIRY_TEMP_KEY_PATTERN =
            Pattern.compile("^temp/inquiry/[A-Za-z0-9_-]+\\.[A-Za-z0-9]+$");

    public String uploadProfileImage(MultipartFile multipartFile, String prevProfileImage) {
        String contentType = Validate.validateFile(multipartFile, PROFILE_IMAGE_MAX_SIZE);

        if (StringUtils.hasText(prevProfileImage)) {
            try {
                s3UploadClient.deleteImage(prevProfileImage);
            } catch (Exception e) {
                log.warn("기존 프로필 이미지 삭제 실패: {}", prevProfileImage);
            }
        }

        String folderName = "images/profile";
        return s3UploadClient.uploadImage(multipartFile, folderName, contentType);
    }

    public String uploadTempImage(MultipartFile multipartFile, String folderPath) {
        String contentType = Validate.validateFile(multipartFile, TEMP_IMAGE_MAX_SIZE);
        String safeFolder = Validate.validateFolderPath(folderPath);
        return s3UploadClient.uploadImage(multipartFile, safeFolder, contentType);
    }

    public List<String> uploadImages(List<MultipartFile> multipartFiles, String folderPath) {
        if (multipartFiles.size() > IMAGES_MAX_COUNT) {
            throw new CustomException(FILE_COUNT_EXCEEDED.getCode(), "이미지는 최대 " + IMAGES_MAX_COUNT + "개까지 업로드 가능합니다.");
        }

        String safeFolder = Validate.validateFolderPath(folderPath);

        // 순차 업로드 + 부분 실패 시 이미 올라간 파일 정리 (고아 객체 방지, 공용 스레드풀 사용 회피)
        List<String> uploadedKeys = new ArrayList<>();
        try {
            for (MultipartFile multipartFile : multipartFiles) {
                String contentType = Validate.validateFile(multipartFile, IMAGES_MAX_SIZE);
                uploadedKeys.add(s3UploadClient.uploadImage(multipartFile, safeFolder, contentType));
            }
        } catch (RuntimeException e) {
            for (String key : uploadedKeys) {
                try {
                    s3UploadClient.deleteImage(key);
                } catch (Exception ignore) {
                    log.warn("업로드 롤백 중 삭제 실패: {}", key);
                }
            }
            throw e;
        }

        return uploadedKeys;
    }

    public MyinfoUpdateResponse updateMyinfo(MyinfoUpdateRequest myinfoUpdateRequest) {
        AuthUser authUser = jwtTokenProvider.getUserInfo();
        String userId = authUser.getUserId();

        myinfoUpdateRequest.setUserId(userId);

        mypageMapper.updateMyinfo(myinfoUpdateRequest);

        String email = myinfoUpdateRequest.getEmail();
        UserDto user = userService.findUserByEmail(email);

        return MyinfoUpdateResponse.builder()
                .phone(user.getPhone())
                .profileImage(user.getProfileImage())
                .zonecode(user.getZonecode())
                .roadAddress(user.getRoadAddress())
                .detailAddress(user.getDetailAddress())
                .build();
    }

    public void createInquiry(InquiryCreateRequest inquiryCreateRequest) {
        String content = inquiryCreateRequest.getContent();
        Pattern pattern = Pattern.compile("<img[^>]+src=\"([^\"]*temp/inquiry/[^\"]+)\"");
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            String fullUrl = matcher.group(1);

            String tempKey = fullUrl.substring(fullUrl.indexOf("temp/inquiry/"));

            // 사용자 본문에서 추출한 키를 그대로 신뢰하지 않고 형식 검증 (임의 객체 복사 방지)
            if (!INQUIRY_TEMP_KEY_PATTERN.matcher(tempKey).matches()) {
                throw new CustomException(INVALID_IMAGE_REFERENCE.getCode(), INVALID_IMAGE_REFERENCE.getMessage());
            }

            String newKey = s3UploadClient.moveImage(tempKey, "images/inquiry");
            content = content.replace(tempKey, newKey);
        }

        String cleanHtml = Validate.cleanHtml(content);

        inquiryCreateRequest.setContent(cleanHtml);

        int count = mypageMapper.insertInquiry(inquiryCreateRequest);
        if (count <= 0) {
            throw new CommonException(INQUIRY_CREATE_FAILED);
        }

        String upperInquiryId = inquiryCreateRequest.getUpperInquiryId();
        if (StringUtils.hasText(upperInquiryId)) {
            mypageMapper.updateIsAnswered(upperInquiryId);
        }
    }

    public InfiniteResponse<List<Inquiry>> searchInquiryList(InquiryListSearchRequest inquiryListSearchRequest) {
        injectAuthInfo(inquiryListSearchRequest);

        int page = inquiryListSearchRequest.getPage();
        int size = inquiryListSearchRequest.getSize();
        int offset = page * size;
        inquiryListSearchRequest.setOffset(offset);

        long totalCount = mypageMapper.selectInquiryCount(inquiryListSearchRequest);
        boolean hasNext = (long) (page + 1) * size < totalCount;
        List<Inquiry> list = mypageMapper.selectInquiryList(inquiryListSearchRequest);

        return InfiniteResponse.<List<Inquiry>>builder()
                .data(list)
                .page(page)
                .size(size)
                .totalCount(totalCount)
                .hasNext(hasNext)
                .build();
    }

    public Inquiry searchInquiry(InquirySearchRequest inquirySearchRequest) {
        return mypageMapper.selectInquiry(inquirySearchRequest);
    }

    public void createReview(ReviewCreateRequest reviewCreateRequest) {
        AuthUser authUser = jwtTokenProvider.getUserInfo();
        reviewCreateRequest.setUserId(authUser.getUserId());

        int reviewCreateResult = mypageMapper.insertReview(reviewCreateRequest);
        if (reviewCreateResult <= 0) {
            throw new CommonException(REVIEW_CREATE_FAILED);
        }

        String[] images = reviewCreateRequest.getReviewImages();
        if (images != null) {
            for (int i = 0; i < images.length; i++) {
                mypageMapper.insertReviewImage(reviewCreateRequest.getProductReviewId(), images[i], i);
            }
        }

        mypageMapper.recomputeReviewStats(reviewCreateRequest.getProductId());
    }

    public InfiniteResponse<List<Review>> searchReviewList(ReviewListSearchRequest reviewListSearchRequest) {
        int page = reviewListSearchRequest.getPage();
        int size = reviewListSearchRequest.getSize();
        int offset = page * size;
        reviewListSearchRequest.setOffset(offset);

        Long productId = reviewListSearchRequest.getProductId();
        ReviewCountResponse reviewCountResponse = mypageMapper.selectReviewCount(productId);

        long totalCount = 0;
        BigDecimal averageRating = BigDecimal.ZERO;

        if (reviewCountResponse != null) {
            totalCount = reviewCountResponse.getTotalCount();
            averageRating = reviewCountResponse.getAverageRating();
        }

        boolean hasNext = (long) (page + 1) * size < totalCount;
        List<Review> list = mypageMapper.selectReviewList(reviewListSearchRequest);

        // 리뷰별 이미지 로드
        for (Review review : list) {
            review.setReviewImages(mypageMapper.selectReviewImages(review.getReviewId()));
        }

        return InfiniteResponse.<List<Review>>builder()
                .data(list)
                .page(page)
                .size(size)
                .totalCount(totalCount)
                .hasNext(hasNext)
                .averageRating(averageRating)
                .build();
    }

    private void injectAuthInfo(AuthAware request) {
        AuthUser authUser = jwtTokenProvider.getUserInfo();
        request.setUserId(authUser.getUserId());
        request.setRole(jwtTokenProvider.getRole());
    }
}
