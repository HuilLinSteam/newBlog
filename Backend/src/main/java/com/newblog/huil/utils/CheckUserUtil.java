package com.newblog.huil.utils;

import com.newblog.huil.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 1.用于屏蔽用户的敏感信息
 * 2.判断用户是否登录
 * @author HuilLIN
 */
public class CheckUserUtil {

    private static final Logger logger = LoggerFactory.getLogger(CheckUserUtil.class);
    /**
     * 判读用户是否为空
     * @param o
     * @return
     */
    public static boolean UserIsNull(User o){
        if(o == null){
            return true;
        }
        return false;
    }


    /**
     * 主要用于屏蔽用户的密码、加盐、UID
     * @param user
     * @return
     */
    public static User getUser(User user){
        if (UserIsNull(user)) {
            logger.error("传入的用户为null");
            throw new RuntimeException();
        }
        user.setUid("");
        user.setPassword("");
        user.setSalt("");
        return user;
    }
}
