package com.jiezipoi.mall.dao;

import com.jiezipoi.mall.entity.MallUser;
import org.apache.ibatis.annotations.Param;

public interface MallUserDao {
    int insertSelective(MallUser user);

    MallUser selectByEmail(@Param("email") String email);

    MallUser selectByEmailAndPassword(@Param("email") String email, @Param("password") String password);
}
