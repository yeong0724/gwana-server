package com.gwana.server.dto.mypage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewListSearchRequest {
    private String productId;
    /**
     * - recommended: 별점순
     * - latest: 최신순
     */
    private String sortBy;

    // 포토리뷰만 (true / false)
    private boolean photoOnly;

    // 페이징
    private int page;
    private int size;
    private int offset;
}
