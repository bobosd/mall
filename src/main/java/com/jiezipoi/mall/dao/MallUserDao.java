package com.jiezipoi.mall.dao;

import com.jiezipoi.mall.entity.MallUser;
import com.jiezipoi.mall.entity.MallUserRefreshToken;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MallUserDao {
    int insertSelective(MallUser user);

    MallUser selectByEmail(@Param("email") String email);

    MallUser selectByEmailAndPassword(@Param("email") String email, @Param("password") String password);

    int insertRefreshToken(MallUserRefreshToken mallUserRefreshToken);

    int deleteRefreshToken(@Param("uuid") String uuid);

    List<MallUserRefreshToken> selectRefreshTokenByEmail(@Param("email") String email);
}