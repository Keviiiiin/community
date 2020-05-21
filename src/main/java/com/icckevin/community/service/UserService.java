package com.icckevin.community.service;

import com.icckevin.community.dao.LoginTicketMapper;
import com.icckevin.community.dao.UserMapper;
import com.icckevin.community.entity.LoginTicket;
import com.icckevin.community.entity.User;
import com.icckevin.community.utils.ActivationConstant;
import com.icckevin.community.utils.CommunityUtil;
import com.icckevin.community.utils.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.security.auth.spi.LoginModule;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
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

    @Autowired
    private LoginTicketMapper loginTicketMapper;

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

    public Map<String,Object> login(String username, String password, int expiredSeconds){
        Map<String,Object> map = new HashMap<>();
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }
        // 验证码的判断在controller里
//        if (StringUtils.isBlank(code)) {
//            map.put("codeMsg", "验证码不能为空!");
//            return map;
//        }
        User user = userMapper.selectByName(username);
        if(user == null){
            map.put("usernameMsg", "用户不存在！");
            return map;
        }
        // 验证状态
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "该账号未激活!");
            return map;
        }

        password = CommunityUtil.md5(password + user.getSalt());
        if(!user.getPassword().equals(password)){
            map.put("passwordMsg", "密码错误！");
            return map;
        }

        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setStatus(0);
        loginTicket.setTicket(CommunityUtil.generateUUID());
        // 过期的时间
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicketMapper.insertTicket(loginTicket);

        map.put("ticket",loginTicket.getTicket());
        return map;
    }

    public void logout(String ticket) {
        loginTicketMapper.updateStatus(ticket, 1);
    }

    public LoginTicket findByTicket(String ticket){
        return loginTicketMapper.selectByTicker(ticket);
    }

    /**
     * 更新头像地址
     * @param userId
     * @param headerUrl
     * @return
     */
    public int updateHeader(int userId, String headerUrl) {
        return userMapper.updateHeader(userId, headerUrl);
    }

    public int updatePassword(int userId,String password){
        return userMapper.updatePassword(userId,password);
    }
}