package com.icckevin.community;

import com.icckevin.community.dao.DiscussPostMapper;
import com.icckevin.community.dao.UserMapper;
import com.icckevin.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @description:
 * @author: iccKevin
 * @create: 2020-05-13 22:27
 **/

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void testSelectById(){

        User user = userMapper.selectById(101);
        System.out.println(user);
    }
}