package com.jiezipoi.mall.dao;

import com.jiezipoi.mall.entity.AdminUser;
import org.apache.ibatis.annotations.Param;

public interface AdminUserDao {
    AdminUser login(@Param("userName") String userName, @Param("password") String password);

    AdminUser getUserById(@Param("id") int id);

    int updateNickname(@Param("id") int id, @Param("nickname") String nickname);

    int updatePassword(@Param("id") int id,
                       @Param("newMD5Password") String newMD5Password);
}
