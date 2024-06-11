package com.jiezipoi.mall.service;

import com.jiezipoi.mall.dao.UserPaymentDao;
import com.jiezipoi.mall.entity.User;
import com.jiezipoi.mall.entity.UserPayment;
import com.jiezipoi.mall.exception.NotFoundException;
import com.jiezipoi.mall.utils.CardDetailGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserPaymentService {
    private final UserPaymentDao userPaymentDao;

    public UserPaymentService(UserPaymentDao userPaymentDao) {
        this.userPaymentDao = userPaymentDao;
    }

    public List<UserPayment> getUserPaymentList(Long userId) {
        return userPaymentDao.findByUserId(userId);
    }

    public UserPayment createUserPayment(User user) {
        UserPayment userPayment = generateRandomPayment(user);
        userPaymentDao.insert(userPayment);
        return userPayment;
    }

    private UserPayment generateRandomPayment(User user) {
        UserPayment payment = new UserPayment();
        payment.setUserId(user.getUserId());
        payment.setPaymentType(CardDetailGenerator.generateRandomPaymentType());
        payment.setCardNumber(CardDetailGenerator.generateRandomCardNumber());
        payment.setCardHolderName(user.getNickName());
        payment.setExpirationDate(CardDetailGenerator.generateRandomCardExpireDate());
        payment.setCVV(CardDetailGenerator.generateRandomCVV());
        payment.setCreateTime(LocalDateTime.now());
        return payment;
    }

    public void deletePayment(long userId, long paymentId) throws NotFoundException {
        int removedRowCount = userPaymentDao.deleteByPrimaryKey(userId, paymentId);
        if (removedRowCount == 0) {
            throw new NotFoundException();
        }
    }
}
