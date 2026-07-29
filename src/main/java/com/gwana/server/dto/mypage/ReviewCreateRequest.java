package com.gwana.server.dto.mypage;

import com.gwana.server.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ReviewCreateRequest extends BaseDto {
    private Long productReviewId;      // insert 후 채워짐(keyProperty)
    private Long productId;
    private Long productVariantId;     // 선택
    private String userId;             // 서버에서 주입
    private String content;
    private String[] reviewImages;
    private BigDecimal rating;
}
