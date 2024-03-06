package com.jiezipoi.mall.handler;

import com.jiezipoi.mall.enums.UserStatus;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//guide: https://stackoverflow.com/questions/71171602/mybatis-how-mapping-integer-to-enum
@MappedTypes(UserStatus.class)
public class UserStatusTypeHandler implements TypeHandler<UserStatus> {

    @Override
    public void setParameter(PreparedStatement ps, int i, UserStatus parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            ps.setInt(i, UserStatus.UNACTIVATED.getCode());
        } else {
            ps.setInt(i, parameter.getCode());
        }

    }

    @Override
    public UserStatus getResult(ResultSet rs, String columnName) throws SQLException {
        return getUserStatus(rs.getInt(columnName));
    }

    @Override
    public UserStatus getResult(ResultSet rs, int columnIndex) throws SQLException {
        return getUserStatus(rs.getInt(columnIndex));
    }

    @Override
    public UserStatus getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return getUserStatus(cs.getInt(columnIndex));
    }

    private UserStatus getUserStatus(int statusCode) {
        for (UserStatus status : UserStatus.values()) {
            if (status.getCode() == statusCode) {
                return status;
            }
        }
        throw new IllegalArgumentException("Cannot convert " + statusCode + " to Status");
    }
}
