package com.jiezipoi.mall.dao;

import com.jiezipoi.mall.entity.UserPayment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserPaymentDao {
    int insert(UserPayment userPayment);

    UserPayment selectByPaymentIdAndUserId(@Param("paymentId") long paymentId,@Param("userId") long userId);

    List<UserPayment> findByUserId(long userId);

    int deleteByPrimaryKey(@Param("userId") long userId, @Param("paymentId") long paymentId);

    int unsetDefaultPaymentByUserId(@Param("userId") long userId);

    int setAsDefaultPayment(@Param("userId") long userId, @Param("paymentId") long paymentId);

    int updateByPrimaryKeySelective(UserPayment userPayment);
}
