package com.icckevin.community.utils;

import com.icckevin.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: iccKevin
 * @create: 2020-05-19 11:32
 **/
@Component
public class HostHolder {

    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }

}
