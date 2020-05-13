package com.icckevin.community.service;

import com.icckevin.community.dao.UserMapper;
import com.icckevin.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description: 用户业务
 * @author: iccKevin
 * @create: 2020-05-13 21:11
 **/
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    User selectById(int id){
        return userMapper.selectById(id);
    }
}