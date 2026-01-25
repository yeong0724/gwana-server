package com.gwana.server.dto.mypage;

import com.gwana.server.dto.BaseDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MyinfoUpdateRequest extends BaseDto {
    private String userId;
    private String email;
    private String phone;
    private String profileImage;
    private String zonecode;
    private String roadAddress;
    private String detailAddress;
}
