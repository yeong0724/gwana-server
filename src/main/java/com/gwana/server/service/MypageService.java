package com.gwana.server.service;

import com.gwana.server.client.S3UploadClient;
import com.gwana.server.common.enums.ErrorCode;
import com.gwana.server.common.exception.CommonException;
import com.gwana.server.common.security.JwtTokenProvider;
import com.gwana.server.common.utils.Validate;
import com.gwana.server.dto.mypage.*;
import com.gwana.server.dto.user.AuthUser;
import com.gwana.server.dto.user.UserDto;
import com.gwana.server.mapper.MypageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        int count = mypageMapper.createInquiry(inquiryCreateRequest);
        if (count <= 0) {
            throw new CommonException(ErrorCode.DEFAULT_ERROR);
        }

        String upperInquiryId = inquiryCreateRequest.getUpperInquiryId();
        if (StringUtils.hasText(upperInquiryId)) {
            mypageMapper.updateIsAnswered(upperInquiryId);
        }
    }

    public List<InquiryResponse> searchInquiryList(InquiryListSearchRequest inquiryListSearchRequest) {
        AuthUser authUser = jwtTokenProvider.getUserInfo();
        String userId = authUser.getUserId();
        String role = jwtTokenProvider.getRole();
        inquiryListSearchRequest.setUserId(userId);
        inquiryListSearchRequest.setRole(role);

        return mypageMapper.selectInquiryList(inquiryListSearchRequest);
    }

    public InquiryResponse searchInquiry(InquirySearchRequest inquirySearchRequest) {
        return mypageMapper.selectInquiry(inquirySearchRequest);
    }
}
