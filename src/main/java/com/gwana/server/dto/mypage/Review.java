package com.gwana.server.dto.mypage;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gwana.server.common.annotation.EmailMasking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Review {
    // 리뷰 ID
    private Long reviewId;

    // 리뷰 대상 상품 ID
    private String productId;

    // 리뷰 내용
    private String content;

    // 리뷰 사진
    private String[] reviewImages;

    // 리뷰 별점
    private BigDecimal rating;

    // 작성일
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    // 작성자 Id
    private String createdBy;

    // 작성자 email
    @EmailMasking
    private String email;
}
