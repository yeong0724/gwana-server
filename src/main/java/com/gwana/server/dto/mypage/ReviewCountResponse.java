package com.gwana.server.dto.mypage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewCountResponse {
    // 리뷰 총개수
    private long totalCount;

    // 평균 별점
    private BigDecimal averageRating;
}
