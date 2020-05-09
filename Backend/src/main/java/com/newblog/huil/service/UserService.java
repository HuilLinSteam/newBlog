package com.newblog.huil.service;

import com.newblog.huil.entity.LoginTicket;
import com.newblog.huil.entity.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

/**
 * @author HuilLIN
 */
public interface UserService {
    /**
     * 根据用户账号判断用户是否存在
     * @param username
     * @return
     */
    public boolean checkUserName(String username);

    /**
     * 根据用户手机判断用户是否存在
     * @param phone
     * @return
     */
    public boolean checkUserPhone(String phone);

    /**
     * 发送注册短信
     * @param phone
     * @return
     */
    public String getRegSMS(String phone);

    /**
     * 发送短信
     * @param phone
     * @return
     */
    public String getLogSMS(String phone);

    /**
     * 检测用户填写的验证码和手机发送的验证码是否一致
     * @param code
     * @param phone
     * @return
     */
    public boolean checkCode(String code,String phone);

    /**
     * 存储用户是否成功
     * @param user
     * @return
     */
    public boolean saveUser(User user);


    /**
     * 可根据用户名、手机号码找到用户的所有信息
     * @param str
     * @return
     */
    public User findUserByUsernameOrPhone(String str);

    /**
     * 根据用户Id获取用户信息
     * @param userId
     * @return
     */
    public User findByUserId(int userId);

    /**
     * 登录判断
     * @param username 登录的用户名或者是手机号码
     * @param password 用户登录的密码，若为null则表示手机登录
     * @param expiredSeconds 登录的有效时间
     * @return
     */
    public Map<String, Object> login(String username,String password, long expiredSeconds);

    /**
     * 根据ticket找到登录凭证
     * @param ticket
     * @return
     */
    public LoginTicket findLoginTicket(String ticket);

    /**
     * 根据ticket退出登录
     * @param ticket
     */
    public void logout(String ticket);

    /**
     * 更新用户的头像
     * @param userId
     * @param headerUrl
     * @return
     */
    public int updateHeader(int userId, String headerUrl);

    /**
     * 1.优先从缓存中取值
     * @param userId
     * @return
     */
    public User getCache(int userId);

    /**
     * 2.取不到值时初始化缓存数据
     * @param userId
     * @return
     */
    public User initCache(int userId);

    /**
     * 3.数据变更时清除缓存数据
     * @param userId
     */
    public void clearCache(int userId);

    /**
     * 获取用户权限
     * @param userId
     * @return
     */
    public Collection<? extends GrantedAuthority> getAuthorities(int userId);

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    public int updateUser(User user);
}
