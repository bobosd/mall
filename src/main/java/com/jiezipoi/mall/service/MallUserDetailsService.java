package com.jiezipoi.mall.service;

import com.jiezipoi.mall.dao.MallUserDao;
import com.jiezipoi.mall.entity.MallUser;
import com.jiezipoi.mall.security.MallUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MallUserDetailsService implements UserDetailsService {
    private final MallUserDao mallUserDao;

    public MallUserDetailsService(MallUserDao mallUserDao) {
        this.mallUserDao = mallUserDao;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        MallUser mallUser = mallUserDao.selectByEmail(email);
        if (mallUser == null) {
            throw new UsernameNotFoundException(email);
        }
        return new MallUserDetails(mallUser);
    }
}
