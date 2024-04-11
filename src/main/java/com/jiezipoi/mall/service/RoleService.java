package com.jiezipoi.mall.service;

import com.jiezipoi.mall.dao.RoleDao;
import com.jiezipoi.mall.enums.Role;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    private final RoleDao roleDao;

    public RoleService(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    public void assignRoleToUser(Role role, Long userId) {
        if (userId == null) {
            throw new NullPointerException();
        }
        roleDao.insertUserHasRole(role.getValue(), userId);
    }
}
