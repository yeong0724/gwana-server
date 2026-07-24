package com.gwana.server.mapper;

import com.gwana.server.dto.token.RefreshToken;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface RefreshTokenMapper {
    /** 사용자당 1행 유지: 존재하면 해시/만료를 갱신(회전), 없으면 삽입 */
    void upsert(RefreshToken refreshToken);

    Optional<RefreshToken> findByUserId(@Param("userId") String userId);

    void deleteByUserId(@Param("userId") String userId);
}
