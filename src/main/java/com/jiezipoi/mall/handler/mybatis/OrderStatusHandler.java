package com.jiezipoi.mall.handler.mybatis;


import com.jiezipoi.mall.enums.OrderStatus;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderStatusHandler implements TypeHandler<OrderStatus> {

    @Override
    public void setParameter(PreparedStatement ps, int i, OrderStatus parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            ps.setString(i, null);
        } else {
            ps.setString(i, parameter.name());
        }
    }

    @Override
    public OrderStatus getResult(ResultSet rs, String columnName) throws SQLException {
        return OrderStatus.valueOf(rs.getString(columnName));
    }

    @Override
    public OrderStatus getResult(ResultSet rs, int columnIndex) throws SQLException {
        return OrderStatus.valueOf(rs.getString(columnIndex));
    }

    @Override
    public OrderStatus getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return OrderStatus.valueOf(cs.getString(columnIndex));
    }
}
