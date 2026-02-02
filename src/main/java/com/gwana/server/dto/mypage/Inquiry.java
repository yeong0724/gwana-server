package com.gwana.server.dto.mypage;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Inquiry {
    // 문의글 ID
    private Long inquiryId;

    // 문의대상 상풍 정보 (단순 문의글인 경우 nullable)
    private String productId;
    private String productName;

    private String title;
    private String content;
    private String isSecret;
    private String isAnswered;

    // 작성일
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    // 작성자 Id
    private String createdBy;

    // 작성자 이름
    private String username;
    // 작성자 연락처
    private String phone;

    private InquiryAnswerResponse answer;
}
