package com.gwana.server.mapper;

import com.gwana.server.dto.token.Token;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface TokenMapper {
    void createToken(Token token);

    void deleteTokenByUserId(String userId);

    Optional<Token> findTokenByUserId(String userId);

    Optional<Token> findTokenByAccessToken(String accessToken);

    void deleteTokenByAccessToken(String accessToken);
}
