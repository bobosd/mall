package com.jiezipoi.mall.dao;

import com.jiezipoi.mall.entity.Order;

public interface OrderDao {
    int insert(Order order);
    int updateByPrimaryKeySelective(Order order);
}
