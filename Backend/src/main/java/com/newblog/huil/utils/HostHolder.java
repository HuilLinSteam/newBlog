package com.newblog.huil.utils;

import com.newblog.huil.entity.User;
import org.springframework.stereotype.Component;

/**
 * @author HuilLIN
 */
@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    public void clear(){
        users.remove();
    }
}
