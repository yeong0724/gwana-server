package com.gwana.server.mapper;

import com.gwana.server.dto.mypage.InquiryCreateRequest;
import com.gwana.server.dto.mypage.InquiryListSearchRequest;
import com.gwana.server.dto.mypage.InquiryResponse;
import com.gwana.server.dto.mypage.MyinfoUpdateRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MypageMapper {
    void updateMyinfo(MyinfoUpdateRequest myinfoUpdateRequest);

    int createInquiry(InquiryCreateRequest inquiryCreateRequest);

    List<InquiryResponse> selectInquiryList(InquiryListSearchRequest inquiryListSearchRequest);
}
