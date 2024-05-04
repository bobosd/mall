package com.jiezipoi.mall.dao;

import com.jiezipoi.mall.entity.UserVerificationCode;
import com.jiezipoi.mall.enums.VerificationActionType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface VerificationCodeDao {
    int insertSelective(UserVerificationCode verificationCode);

    List<UserVerificationCode> selectVerificationCodeByEmail(@Param("email") String email);

    UserVerificationCode selectLastVerificationCodeByEmailAndType(@Param("email") String email,
                                                                  @Param("actionType") VerificationActionType actionType);

    UserVerificationCode selectVerificationCodeByCode(@Param("code") String code);

    int deleteVerificationCodeByCode(@Param("code") String code);
}
