package com.gwana.server.mapper;

import com.gwana.server.dto.mypage.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MypageMapper {
    void updateMyinfo(MyinfoUpdateRequest myinfoUpdateRequest);

    int createInquiry(InquiryCreateRequest inquiryCreateRequest);

    List<InquiryResponse> selectInquiryList(InquiryListSearchRequest inquiryListSearchRequest);

    InquiryResponse selectInquiry(InquirySearchRequest inquirySearchRequest);

    void updateIsAnswered(String inquiryId);
}
