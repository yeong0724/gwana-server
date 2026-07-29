package com.gwana.server.mapper;

import com.gwana.server.dto.mypage.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

    void insertReviewImage(@Param("productReviewId") Long productReviewId,
                           @Param("url") String url,
                           @Param("sortOrder") int sortOrder);

    void recomputeReviewStats(@Param("productId") Long productId);

    List<String> selectReviewImages(@Param("productReviewId") Long productReviewId);

    ReviewCountResponse selectReviewCount(Long productId);

    List<Review> selectReviewList(ReviewListSearchRequest reviewListSearchRequest);
}
