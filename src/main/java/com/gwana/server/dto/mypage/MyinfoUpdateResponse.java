package com.gwana.server.dto.mypage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyinfoUpdateResponse {
    private String phone;
    private String profileImage;
    private String zonecode;
    private String roadAddress;
    private String detailAddress;
}
