package com.jiezipoi.mall.dao;

import com.jiezipoi.mall.entity.UserRefreshToken;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface JwtDao {
    int insertRefreshToken(UserRefreshToken userRefreshToken);

    int deleteRefreshToken(@Param("uuid") String uuid);

    List<UserRefreshToken> selectRefreshTokenByEmail(@Param("email") String email);

    int deleteAllRefreshTokenOfUser(@Param("email") String email);
}
