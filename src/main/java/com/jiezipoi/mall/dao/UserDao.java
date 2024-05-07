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

    String selectEmailByVerificationCode(@Param("verificationCode") String verificationCode);

    int deleteByEmail(@Param("email") String email);

    int updateByEmail(@Param("email") String email, @Param("password") String password);
}