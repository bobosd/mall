package com.jiezipoi.mall.dao;

import com.jiezipoi.mall.entity.User;
import com.jiezipoi.mall.enums.UserStatus;
import org.apache.ibatis.annotations.Param;

public interface UserDao {
    int insertSelective(User user);

    Long selectUserIdByEmail(@Param("email") String email);

    User selectByEmail(@Param("email") String email);

    User selectByEmailAndPassword(@Param("email") String email, @Param("password") String password);

    int updateStatusByPrimaryKey(@Param("userId") long userId, @Param("status") UserStatus status);

    int insertVerificationCode(@Param("email") String email, @Param("verificationCode") String verificationCode);

    String selectEmailByVerificationCode(@Param("verificationCode") String verificationCode);

    int deleteVerificationCodeByEmail(@Param("email") String email);
}