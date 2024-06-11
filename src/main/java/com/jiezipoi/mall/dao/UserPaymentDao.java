package com.jiezipoi.mall.dao;

import com.jiezipoi.mall.entity.UserPayment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserPaymentDao {
    int insert(UserPayment userPayment);

    List<UserPayment> findByUserId(long userId);

    int deleteByPrimaryKey(@Param("userId") long userId,@Param("paymentId") long paymentId);
}
