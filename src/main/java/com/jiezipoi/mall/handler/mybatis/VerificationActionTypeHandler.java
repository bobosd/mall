package com.jiezipoi.mall.handler.mybatis;

import com.jiezipoi.mall.enums.VerificationActionType;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(VerificationActionTypeHandler.class)
public class VerificationActionTypeHandler implements TypeHandler<VerificationActionType> {
    @Override
    public void setParameter(PreparedStatement ps, int i, VerificationActionType parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            ps.setNull(i, jdbcType.TYPE_CODE);
        } else {
            ps.setString(i, parameter.name());
        }
    }

    @Override
    public VerificationActionType getResult(ResultSet rs, String columnName) throws SQLException {
        return VerificationActionType.valueOf(rs.getString(columnName));
    }

    @Override
    public VerificationActionType getResult(ResultSet rs, int columnIndex) throws SQLException {
        return VerificationActionType.valueOf(rs.getString(columnIndex));
    }

    @Override
    public VerificationActionType getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return VerificationActionType.valueOf(cs.getString(columnIndex));
    }
}
