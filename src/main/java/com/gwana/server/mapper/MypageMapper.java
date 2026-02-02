package com.gwana.server.mapper;

import com.gwana.server.dto.mypage.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MypageMapper {
    void updateMyinfo(MyinfoUpdateRequest myinfoUpdateRequest);

    int insertInquiry(InquiryCreateRequest inquiryCreateRequest);

    long selectInquiryCount(InquiryListSearchRequest inquiryListSearchRequest);

    List<Inquiry> selectInquiryList(InquiryListSearchRequest inquiryListSearchRequest);

    Inquiry selectInquiry(InquirySearchRequest inquirySearchRequest);

    void updateIsAnswered(String inquiryId);

    int insertReview(ReviewCreateRequest reviewCreateRequest);

    ReviewCountResponse selectReviewCount(String productId);

    List<Review> selectReviewList(ReviewListSearchRequest reviewListSearchRequest);
}
