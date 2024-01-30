package com.jiezipoi.mall.dao;

import com.jiezipoi.mall.entity.MallUser;
import com.jiezipoi.mall.enums.UserStatus;
import org.apache.ibatis.annotations.Param;

public interface MallUserDao {
    int insertSelective(MallUser user);

    MallUser selectByEmail(@Param("email") String email);

    MallUser selectByEmailAndPassword(@Param("email") String email, @Param("password") String password);

    int updateStatusByEmail(@Param("email") String email, @Param("status") UserStatus status);

    int insertVerificationCode(@Param("email") String email, @Param("verificationCode") String verificationCode);
}