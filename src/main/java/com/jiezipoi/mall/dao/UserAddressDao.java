package com.jiezipoi.mall.dao;

import com.jiezipoi.mall.entity.UserAddress;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserAddressDao {
    int insert(UserAddress userAddress);

    int updateByPrimaryKeySelective(UserAddress userAddress);

    List<UserAddress> findByUserId(Long userId);

    UserAddress selectByPrimaryKey(Long userAddressId);

    int deleteByPrimaryKeyAndUserId(@Param("userId") Long userId, @Param("userAddressId") Long addressId);

    int unsetDefaultAddressByUserId(@Param("userId") Long userId);

    int setAnyAsDefaultAddress(Long userId);

    int countByUserId(Long userId);
}
