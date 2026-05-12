package com.gwana.server.dto.mypage;

import com.gwana.server.common.enums.Role;
import com.gwana.server.dto.AuthAware;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductInquiryListSearchRequest implements AuthAware {
    private Long productId;     // 필수
    private String userId;      // 서비스에서 주입 (비로그인 시 null)
    private Role role;        // 서비스에서 주입 (비로그인 시 null)
    private String isAnswered;
    private String excludeSecret; // 비밀글 제외 여부

    private int page;
    private int size;
    private int offset;
}
