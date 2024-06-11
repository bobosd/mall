package com.jiezipoi.mall.handler;

import com.jiezipoi.mall.enums.PaymentType;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PaymentTypeHandler implements TypeHandler<PaymentType> {

    @Override
    public void setParameter(PreparedStatement ps, int i, PaymentType parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            ps.setString(i, null);
        } else {
            ps.setString(i, parameter.name());
        }
    }

    @Override
    public PaymentType getResult(ResultSet rs, String columnName) throws SQLException {
        return PaymentType.valueOf(rs.getString(columnName));
    }

    @Override
    public PaymentType getResult(ResultSet rs, int columnIndex) throws SQLException {
        return PaymentType.valueOf(rs.getString(columnIndex));
    }

    @Override
    public PaymentType getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return PaymentType.valueOf(cs.getString(columnIndex));
    }
}
