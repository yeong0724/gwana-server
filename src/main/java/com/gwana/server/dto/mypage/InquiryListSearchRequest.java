package com.gwana.server.dto.mypage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InquiryListSearchRequest {
    private String userId;
    private Long productId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String isAnswered;
    private String role;

    // 페이징
    private int page;
    private int size;
    private int offset;
}
