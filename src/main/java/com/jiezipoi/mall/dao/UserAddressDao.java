package com.jiezipoi.mall.dao;

import com.jiezipoi.mall.entity.UserAddress;

import java.util.List;

public interface UserAddressDao {
    int insert(UserAddress userAddress);
    int updateByPrimaryKeySelective(UserAddress userAddress);
    List<UserAddress> findByUserId(Long userId);
}
