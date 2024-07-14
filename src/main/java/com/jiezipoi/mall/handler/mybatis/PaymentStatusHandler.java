package com.jiezipoi.mall.handler.mybatis;

import com.jiezipoi.mall.enums.PaymentStatus;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PaymentStatusHandler implements TypeHandler<PaymentStatus> {

    @Override
    public void setParameter(PreparedStatement ps, int i, PaymentStatus parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            ps.setString(i, null);
        } else {
            ps.setString(i, parameter.name());
        }
    }

    @Override
    public PaymentStatus getResult(ResultSet rs, String columnName) throws SQLException {
        return PaymentStatus.valueOf(rs.getString(columnName));
    }

    @Override
    public PaymentStatus getResult(ResultSet rs, int columnIndex) throws SQLException {
        return PaymentStatus.valueOf(rs.getString(columnIndex));
    }

    @Override
    public PaymentStatus getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return PaymentStatus.valueOf(cs.getString(columnIndex));
    }
}
