package com.jiezipoi.mall.dao;

import org.apache.ibatis.annotations.Param;

public interface RoleDao {
    int insertUserHasRole(@Param("userId") long userId,@Param("roleId") long roleId);
}
