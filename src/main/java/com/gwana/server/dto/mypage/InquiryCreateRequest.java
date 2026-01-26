package com.gwana.server.dto.mypage;

import com.gwana.server.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InquiryCreateRequest extends BaseDto {
    private String title;
    private String content;
    private String isSecret;
    private String productId;
    private String upperInquiryId;
}
