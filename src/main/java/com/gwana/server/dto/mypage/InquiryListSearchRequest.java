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
    private String productId;
    private LocalDate startDate;
    private LocalDate endDate;
}
