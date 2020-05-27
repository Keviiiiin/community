package com.icckevin.community;

import com.icckevin.community.dao.DiscussPostMapper;
import com.icckevin.community.dao.LoginTicketMapper;
import com.icckevin.community.dao.UserMapper;
import com.icckevin.community.entity.DiscussPost;
import com.icckevin.community.entity.LoginTicket;
import com.icckevin.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

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

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Test
    public void testSelectById(){

        User user = userMapper.selectById(101);
        System.out.println(user);
    }

    @Test
    public void testSelectPosts() {
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(149, 0, 10);
        for(DiscussPost post : list) {
            System.out.println(post);
        }

        int rows = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);
    }

    @Test
    public void testInsertLoginTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));

        loginTicketMapper.insertTicket(loginTicket);
    }

    @Test
    public void testSelectLoginTicket() {
        LoginTicket loginTicket = loginTicketMapper.selectByTicker("abc");
        System.out.println(loginTicket);

        loginTicketMapper.updateStatus("abc", 1);
        loginTicket = loginTicketMapper.selectByTicker("abc");
        System.out.println(loginTicket);
    }

    @Test
    public void name() {
//        DiscussPost discussPost = new DiscussPost();
//        discussPost.setUserId(100);
//        discussPost.setCreateTime(new Date());
//        discussPost.setTitle("xxxxx");
//        discussPost.setContent("xxxxxxxx");
//
//        discussPostMapper.insertDiscussPost(discussPost);
//
//        System.out.println(discussPost.getId());

        User user = new User();
        user.setUsername("dddd");
        user.setPassword("123");
        user.setCreateTime(new Date());

        userMapper.insertUser(user);
        System.out.println(user.getId());
    }
}