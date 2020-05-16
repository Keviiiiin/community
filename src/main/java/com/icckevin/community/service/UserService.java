package com.icckevin.community.service;

import com.icckevin.community.dao.UserMapper;
import com.icckevin.community.entity.User;
import com.icckevin.community.utils.ActivationConstant;
import com.icckevin.community.utils.CommunityUtil;
import com.icckevin.community.utils.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @description: 用户业务
 * @author: iccKevin
 * @create: 2020-05-13 21:11
 **/
@Service
public class UserService implements ActivationConstant {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private TemplateEngine templateEngine;

    public User selectById(int id){
        return userMapper.selectById(id);
    }

    public Map<String,Object> register(User user){
        Map<String,Object> map = new HashMap<>();
        if(user == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空!");
            return map;
        }

        //检查是否已存在用户和邮箱
        if(userMapper.selectByName(user.getUsername()) != null){
            map.put("usernameMsg", "该账号已存在!");
            return map;
        }
        if(userMapper.selectByEmail(user.getEmail()) != null){
            map.put("emailMsg", "该邮箱已被注册!");
            return map;
        }

        //对密码进行处理
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));

        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        Context context = new Context();
        // context里存的值会最终传给页面
        context.setVariable("username",user.getUsername());
        // http://localhost:8080/community/activation/101/code
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url",url);
        mailClient.sendMail(user.getEmail(),"牛客网激活账号",templateEngine.process("/mail/activation",context));

        return map;
    }

    public int activation(int userId,String code){
        User user = userMapper.selectById(userId);
        String activationCode = user.getActivationCode();
        if(user.getStatus() == 1)
            return ACTIVATION_REPEAT;
        else if(activationCode.equals(code)){
            userMapper.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;
        }
        else
            return ACTIVATION_FAILURE;
    }
}