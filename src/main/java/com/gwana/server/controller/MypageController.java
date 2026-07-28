package com.gwana.server.controller;

import com.gwana.server.common.utils.ApiResponse;
import com.gwana.server.dto.InfiniteResponse;
import com.gwana.server.dto.mypage.*;
import com.gwana.server.service.MypageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/mypage")
@Tag(name = "마이페이지", description = "이미지 업로드 / 내정보 / 문의 / 리뷰 (인증 필요)")
@SecurityRequirement(name = "bearerAuth")
public class MypageController {
    private final MypageService mypageService;

    @Operation(summary = "프로필 이미지 업로드")
    @PostMapping("/upload/profile-image")
    public ApiResponse<String> uploadProfileImage(
            @RequestParam("profileImage") MultipartFile multipartFile,
            @RequestParam(value = "prevProfileImage", required = false, defaultValue = "") String prevProfileImage
    ) {
        return ApiResponse.ok(mypageService.uploadProfileImage(multipartFile, prevProfileImage));
    }

    @Operation(summary = "임시 이미지 업로드 (단건)")
    @PostMapping("/upload/temp-image")
    public ApiResponse<String> uploadTempImage(
            @RequestParam("image") MultipartFile multipartFile,
            @RequestParam(value = "folderPath", required = false, defaultValue = "") String folderPath
    ) {
        return ApiResponse.ok(mypageService.uploadTempImage(multipartFile, folderPath));
    }

    @Operation(summary = "내 정보 수정")
    @PostMapping("/update/myinfo")
    public ApiResponse<MyinfoUpdateResponse> updateMyinfo(@RequestBody MyinfoUpdateRequest myinfoUpdateRequest) {
        return ApiResponse.ok(mypageService.updateMyinfo(myinfoUpdateRequest));
    }

    @Operation(summary = "문의 작성")
    @PostMapping("/inquiry/create")
    public ApiResponse<Void> createInquiry(@RequestBody InquiryCreateRequest inquiryCreateRequest) {
        mypageService.createInquiry(inquiryCreateRequest);
        return ApiResponse.ok(null);
    }

    @Operation(summary = "내 문의 목록 조회")
    @PostMapping("/inquiry/list/search")
    public ApiResponse<InfiniteResponse<List<Inquiry>>> searchInquiryList(@RequestBody InquiryListSearchRequest inquiryListSearchRequest) {
        return ApiResponse.ok(mypageService.searchInquiryList(inquiryListSearchRequest));
    }

    @Operation(summary = "문의 단건 조회")
    @PostMapping("/inquiry/search")
    public ApiResponse<Inquiry> searchInquiry(@RequestBody InquirySearchRequest inquirySearchRequest) {
        return ApiResponse.ok(mypageService.searchInquiry(inquirySearchRequest));
    }

    @Operation(summary = "이미지 업로드 (다건)")
    @PostMapping("/upload/images")
    public ApiResponse<List<String>> uploadImages(
            @RequestParam("images") List<MultipartFile> multipartFiles,
            @RequestParam(value = "folderPath", defaultValue = "") String folderPath
    ) {
        return ApiResponse.ok(mypageService.uploadImages(multipartFiles, folderPath));
    }

    @Operation(summary = "리뷰 작성")
    @PostMapping("/review/create")
    public ApiResponse<Void> createReview(@RequestBody ReviewCreateRequest reviewCreateRequest) {
        mypageService.createReview(reviewCreateRequest);
        return ApiResponse.ok(null);
    }

    @Operation(summary = "리뷰 목록 조회")
    @PostMapping("/review/list/search")
    public ApiResponse<InfiniteResponse<List<Review>>> searchReviewList(@RequestBody ReviewListSearchRequest reviewListSearchRequest) {
        return ApiResponse.ok(mypageService.searchReviewList(reviewListSearchRequest));
    }
}
