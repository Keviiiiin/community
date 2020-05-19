package com.icckevin.community.controller.interceptor;

import com.icckevin.community.entity.LoginTicket;
import com.icckevin.community.entity.User;
import com.icckevin.community.service.UserService;
import com.icckevin.community.utils.CookieUtil;
import com.icckevin.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;


/**
 * @description: 判断是否有登陆凭证的拦截器
 * @author: iccKevin
 * @create: 2020-05-19 11:12
 **/
@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 判断一下请求中是否包含ticket的cookie
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String ticket = CookieUtil.getCookie(request, "ticket");

        if(ticket != null){
            LoginTicket loginTicket = userService.findByTicket(ticket);
            if(loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())){
                User user = userService.selectById(loginTicket.getUserId());
                // 考虑多线程环境，将user存入当前线程，只要请求未处理完，线程就一直存在
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    /**
     * 在controller处理完请求之后，往model中添加user的信息
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    /**
     * 在模板引擎之后执行
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}