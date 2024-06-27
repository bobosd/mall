package com.jiezipoi.mall.service;

import com.jiezipoi.mall.dao.UserPaymentDao;
import com.jiezipoi.mall.entity.User;
import com.jiezipoi.mall.entity.UserPayment;
import com.jiezipoi.mall.exception.LimitExceededException;
import com.jiezipoi.mall.exception.NotFoundException;
import com.jiezipoi.mall.utils.CardDetailGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserPaymentService {
    private final UserPaymentDao userPaymentDao;

    private final int MAX_PAYMENT_METHOD = 5;

    public UserPaymentService(UserPaymentDao userPaymentDao) {
        this.userPaymentDao = userPaymentDao;
    }

    public List<UserPayment> getUserPaymentList(Long userId) {
        return userPaymentDao.findByUserId(userId);
    }

    public UserPayment getUserPayment(long userId, long paymentId) {
        return userPaymentDao.selectByPaymentIdAndUserId(paymentId, userId);
    }

    public UserPayment createUserPayment(User user) throws LimitExceededException{
        List<UserPayment> existingPayment = getUserPaymentList(user.getUserId());
        if (existingPayment.size() >= MAX_PAYMENT_METHOD) {
            throw new LimitExceededException();
        }
        boolean isFirstAddress = existingPayment.isEmpty();
        UserPayment userPayment = generateRandomPayment(user, isFirstAddress);
        userPaymentDao.insert(userPayment);
        return userPayment;
    }

    private UserPayment generateRandomPayment(User user, boolean defaultFlag) {
        UserPayment payment = new UserPayment();
        payment.setDefaultPayment(defaultFlag);
        payment.setUserId(user.getUserId());
        payment.setPaymentType(CardDetailGenerator.generateRandomPaymentType());
        payment.setCardNumber(CardDetailGenerator.generateRandomCardNumber());
        payment.setCardHolderName(user.getNickName());
        payment.setExpirationDate(CardDetailGenerator.generateRandomCardExpireDate());
        payment.setCVV(CardDetailGenerator.generateRandomCVV());
        payment.setCreateTime(LocalDateTime.now());
        return payment;
    }

    /**
     * 删除用户的支付方法，如果该支付方法是默认方法，则随机从已有的支付方法中选取一个作为新的默认支付方法。
     * @param userId 用户ID
     * @param paymentId 支付方法ID
     * @return 如果没有分配的新的默认支付是null，否则返回新分配的默认支付方法
     * @throws NotFoundException 当给予的用户ID和支付方法ID
     */
    public UserPayment deletePayment(Long userId, Long paymentId) throws NotFoundException {
        UserPayment payment = getUserPayment(userId, paymentId);
        if (payment == null) {
            throw new NotFoundException();
        }
        userPaymentDao.deleteByPrimaryKey(userId, paymentId);
        if (payment.isDefaultPayment()) {
            return setAnyAsDefaultPayment(userId);
        } else {
            return null;
        }
    }

    @Transactional
    public void setAsDefaultPayment(long userId, long paymentId) throws NotFoundException {
        UserPayment payment = getUserPayment(userId, paymentId);

        if (payment == null || !payment.getUserId().equals(userId)) {
            throw new NotFoundException();
        }
        userPaymentDao.unsetDefaultPaymentByUserId(userId);
        userPaymentDao.setAsDefaultPayment(userId, paymentId);
    }

    /**
     * 从用户里的所用地址中，选取一个作为默认地址。
     * @param userId 用户的ID
     * @return null或者被设置为默认的UserPayment
     */
    public UserPayment setAnyAsDefaultPayment(Long userId) {
        List<UserPayment> payments = getUserPaymentList(userId);
        if (payments.isEmpty()) {
            return null;
        }
        UserPayment payment = payments.get(0);
        payment.setDefaultPayment(true);
        userPaymentDao.updateByPrimaryKeySelective(payment);
        return payment;
    }
}
