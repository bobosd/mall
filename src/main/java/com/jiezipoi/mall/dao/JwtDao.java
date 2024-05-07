package com.jiezipoi.mall.dao;

import com.jiezipoi.mall.entity.UserRefreshToken;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface JwtDao {
    int insertRefreshToken(UserRefreshToken userRefreshToken);

    int deleteByUUID(@Param("uuid") String uuid);

    List<UserRefreshToken> findByEmail(@Param("email") String email);

    int deleteAllByEmail(@Param("email") String email);
}
