package com.jiezipoi.mall.dao;

import com.jiezipoi.mall.entity.User;
import org.apache.ibatis.annotations.Param;

public interface MallUserDao {
    int insertSelective(User user);

    User selectByEmail(@Param("email") String email);

    User selectByEmailAndPassword(@Param("email") String email,@Param("password") String password);
}
