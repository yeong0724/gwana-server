package com.gwana.server.mapper;

import com.gwana.server.dto.mypage.MyinfoUpdateRequest;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MypageMapper {
    void updateMyinfo(MyinfoUpdateRequest myinfoUpdateRequest);
}
