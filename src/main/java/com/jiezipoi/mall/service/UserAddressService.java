package com.jiezipoi.mall.service;

import com.jiezipoi.mall.dao.UserAddressDao;
import com.jiezipoi.mall.entity.UserAddress;
import com.jiezipoi.mall.exception.LimitExceededException;
import com.jiezipoi.mall.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserAddressService {
    private final UserAddressDao userAddressDao;

    public UserAddressService(UserAddressDao userAddressDao) {
        this.userAddressDao = userAddressDao;
    }

    public List<UserAddress> getUserAddresList(Long userId) {
        return userAddressDao.findByUserId(userId);
    }

    public UserAddress getUserAddress(Long addressId) {
        return userAddressDao.selectByPrimaryKey(addressId);
    }

    @Transactional
    public void createUserAddress(UserAddress userAddress) throws NotFoundException, LimitExceededException {
        if (getUserAddressCount(userAddress.getUserId()) >= 5) {
            throw new LimitExceededException();
        }
        if (userAddress.isDefaultAddress()) {
            userAddressDao.unsetDefaultAddressByUserId(userAddress.getUserId());
        }
        userAddress.setCreateTime(LocalDateTime.now());
        int insertedRow = userAddressDao.insert(userAddress);
        if (insertedRow < 1) {
            throw new NotFoundException();
        }
    }

    @Transactional
    public void updateUserAddress(UserAddress userAddress) throws NotFoundException {
        if (userAddress.isDefaultAddress()) {
            int affectedRow = userAddressDao.unsetDefaultAddressByUserId(userAddress.getUserId());
            if (affectedRow < 1) {
                throw new NotFoundException();
            }
        }
        userAddressDao.updateByPrimaryKeySelective(userAddress);
    }

    @Transactional
    public UserAddress deleteUserAddress(Long userId, Long addressId) throws NotFoundException {
        UserAddress userAddressToRemove = getUserAddress(addressId);
        if (userAddressToRemove == null || !userAddressToRemove.getUserId().equals(userId)) {
            throw new NotFoundException();
        }
        userAddressDao.deleteByPrimaryKeyAndUserId(userId, addressId);
        return userAddressToRemove;
    }

    public int getUserAddressCount(Long userId) {
        return userAddressDao.countByUserId(userId);
    }

    public UserAddress setAnyAsDefaultAddress(Long userId) {
        List<UserAddress> list = getUserAddresList(userId);
        if (list.isEmpty()) {
            return null;
        }

        UserAddress userAddress = list.get(0);
        userAddress.setDefaultAddress(true);
        userAddressDao.updateByPrimaryKeySelective(userAddress);
        return userAddress;
    }
}