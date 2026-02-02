package com.gwana.server.controller;

import com.gwana.server.common.utils.ApiResponse;
import com.gwana.server.dto.InfiniteResponse;
import com.gwana.server.dto.mypage.*;
import com.gwana.server.service.MypageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/mypage")
public class MypageController {
    private final MypageService mypageService;

    @PostMapping("/upload/profile-image")
    public ApiResponse<String> uploadProfileImage(
            @RequestParam("profileImage") MultipartFile multipartFile,
            @RequestParam(value = "prevProfileImage", required = false, defaultValue = "") String prevProfileImage
    ) {
        return ApiResponse.ok(mypageService.uploadProfileImage(multipartFile, prevProfileImage));
    }

    @PostMapping("/upload/temp-image")
    public ApiResponse<String> uploadTempImage(
            @RequestParam("image") MultipartFile multipartFile,
            @RequestParam(value = "folderPath", required = false, defaultValue = "") String folderPath
    ) {
        return ApiResponse.ok(mypageService.uploadTempImage(multipartFile, folderPath));
    }

    @PostMapping("/update/myinfo")
    public ApiResponse<MyinfoUpdateResponse> updateMyinfo(@RequestBody MyinfoUpdateRequest myinfoUpdateRequest) {
        return ApiResponse.ok(mypageService.updateMyinfo(myinfoUpdateRequest));
    }

    @PostMapping("/create/inquiry")
    public ApiResponse<Void> createInquiry(@RequestBody InquiryCreateRequest inquiryCreateRequest) {
        mypageService.createInquiry(inquiryCreateRequest);
        return ApiResponse.ok(null);
    }

    @PostMapping("/search/inquiry/list")
    public ApiResponse<InfiniteResponse<List<Inquiry>>> searchInquiryList(@RequestBody InquiryListSearchRequest inquiryListSearchRequest) {
        return ApiResponse.ok(mypageService.searchInquiryList(inquiryListSearchRequest));
    }

    @PostMapping("/search/inquiry")
    public ApiResponse<Inquiry> searchInquiry(@RequestBody InquirySearchRequest inquirySearchRequest) {
        return ApiResponse.ok(mypageService.searchInquiry(inquirySearchRequest));
    }

    @PostMapping("/upload/images")
    public ApiResponse<List<String>> uploadTempImage(
            @RequestParam("images") List<MultipartFile> multipartFiles,
            @RequestParam(value = "folderPath", defaultValue = "") String folderPath,
            @RequestParam(value = "maxFileSize", required = false, defaultValue = "3") long maxFileSize,
            @RequestParam(value = "maxFileCount", required = false, defaultValue = "5") int maxFileCount
    ) {
        return ApiResponse.ok(mypageService.uploadImages(multipartFiles, folderPath, maxFileSize, maxFileCount));
    }

    @PostMapping("/create/review")
    public ApiResponse<Void> createReview(@RequestBody ReviewCreateRequest reviewCreateRequest) {
        mypageService.createReview(reviewCreateRequest);
        return ApiResponse.ok(null);
    }

    @PostMapping("/search/review/list")
    public ApiResponse<InfiniteResponse<List<Review>>> searchReviewList(@RequestBody ReviewListSearchRequest reviewListSearchRequest) {
        return ApiResponse.ok(mypageService.searchReviewList(reviewListSearchRequest));
    }
}
