package com.gwana.server.service;

import com.gwana.server.client.S3UploadClient;
import com.gwana.server.common.security.JwtTokenProvider;
import com.gwana.server.common.utils.Validate;
import com.gwana.server.dto.mypage.MyinfoUpdateRequest;
import com.gwana.server.dto.mypage.MyinfoUpdateResponse;
import com.gwana.server.dto.user.AuthUser;
import com.gwana.server.dto.user.UserDto;
import com.gwana.server.mapper.MypageMapper;
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
    private final JwtTokenProvider jwtTokenProvider;
    private final MypageMapper mypageMapper;
    private final UserService userService;

    public String uploadProfileImage(MultipartFile multipartFile) {
        long maxFileSize = 1024 * 1024;
        Validate.validateFile(multipartFile, maxFileSize);

        String folderName = "profile";
        return s3UploadClient.uploadImage(multipartFile, folderName);
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
}
