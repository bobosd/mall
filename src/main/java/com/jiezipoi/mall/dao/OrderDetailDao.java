package com.jiezipoi.mall.dao;

import com.jiezipoi.mall.entity.OrderDetail;

import java.util.List;

public interface OrderDetailDao {
    int insert(OrderDetail orderDetail);
    int insertBatch(List<OrderDetail> orderDetails);
    int updateByPrimaryKeySelective(OrderDetail orderDetail);
}
