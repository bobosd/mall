package com.jiezipoi.mall.dao;

import com.jiezipoi.mall.entity.AdminUser;
import org.apache.ibatis.annotations.Param;

public interface AdminUserDao {
    AdminUser login(@Param("userName") String userName, @Param("password") String password);
}
