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

    public String uploadProfileImage(MultipartFile multipartFile, String prevProfileImage) {
        long maxFileSize = 2 * 1024 * 1024;
        Validate.validateFile(multipartFile, maxFileSize);

        if (StringUtils.hasText(prevProfileImage)) {
            try {
                s3UploadClient.deleteImage(prevProfileImage);
            } catch (Exception e) {
                log.warn("기존 프로필 이미지 삭제 실패: {}", prevProfileImage);
            }
        }

        String folderName = "images/profile";
        return s3UploadClient.uploadImage(multipartFile, folderName);
    }

    public String uploadTempImage(MultipartFile multipartFile, String folderPath) {
        long maxFileSize = 5 * 1024 * 1024;
        Validate.validateFile(multipartFile, maxFileSize);
        return s3UploadClient.uploadImage(multipartFile, folderPath);
    }

    public List<String> uploadImages(List<MultipartFile> multipartFiles, String folderPath, long maxFileSize, int maxFileCount) {
        long MAX_FILE_SIZE = maxFileSize * 1024 * 1024;

        for (MultipartFile multipartFile : multipartFiles) {
            Validate.validateFile(multipartFile, MAX_FILE_SIZE);
        }

        if (multipartFiles.size() > maxFileCount) {
            throw new CustomException(FILE_COUNT_EXCEEDED.getCode(), "이미지는 최대 " + maxFileCount + "개까지 업로드 가능합니다.");
        }

        return multipartFiles.parallelStream()
                .map(file -> s3UploadClient.uploadImage(file, folderPath))
                .toList();
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
        int reviewCreateResult = mypageMapper.insertReview(reviewCreateRequest);

        if (reviewCreateResult <= 0) {
            throw new CommonException(REVIEW_CREATE_FAILED);
        }

        int result = mypageMapper.upsertReviewStats(reviewCreateRequest);

        if (result <= 0) {
            throw new CommonException(REVIEW_STAT_UPSERT_FAILED);
        }
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
