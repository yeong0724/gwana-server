package com.gwana.server.controller;

import com.gwana.server.common.utils.ApiResponse;
import com.gwana.server.service.MypageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/mypage")
public class MypageController {
    private final MypageService mypageService;

    @PostMapping("/upload/profile-image")
    public ApiResponse<String> createPaymentSession(@RequestParam("profileImage") MultipartFile multipartFile) {
        return ApiResponse.ok(mypageService.uploadProfileImage(multipartFile));
    }
}
