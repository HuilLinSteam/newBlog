package com.newblog.huil.service.impl;

import com.newblog.huil.dao.UserMapper;
import com.newblog.huil.entity.LoginTicket;
import com.newblog.huil.entity.User;
import com.newblog.huil.service.UserService;
import com.newblog.huil.utils.NewBlogConstant;
import com.newblog.huil.utils.RedisKeyUtil;
import com.newblog.huil.utils.SmsUtils;
import com.newblog.huil.utils.UUIDUtil;
import com.newblog.huil.utils.encode.AESEncode;
import io.lettuce.core.RedisURI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author HuilLIN
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SmsUtils smsUtils;

    @Override
    public boolean checkUserName(String username) {
        User user = userMapper.getUserName(username);
        if(user !=null) {
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean checkUserPhone(String phone) {
        User user = userMapper.getUserPhone(phone);
        if(user != null) {
            return true;
        }else {
            return false;
        }
    }

    /**
     * 获取注册发送短信码
     * @param phone
     * @return
     */
    @Override
    public String getRegSMS(String phone) {
        String code = smsUtils.getCode();
        String regKey = RedisKeyUtil.getREGKey(phone);
        //判断key值是否存在
        if(redisTemplate.hasKey(regKey)){
            //存在则获取之前存在redis的值
            System.out.println("再次发送验证码:"+redisTemplate.opsForValue().get(regKey));
        }else {
            //5分钟过期时间
            redisTemplate.opsForValue().set(regKey,code,60*5, TimeUnit.SECONDS);
            //发送的返回结果
            //String s = smsUtils.regSendSMS(phone, (String) redisTemplate.opsForValue().get(regKey));
            System.out.println("第一次发送验证码:"+code);
        }
        return (String) redisTemplate.opsForValue().get(regKey);
    }

    /**
     * 获取登录发送短信码
     * @param phone
     * @return
     */
    @Override
    public String getLogSMS(String phone) {
        String code = smsUtils.getCode();
        String logKey = RedisKeyUtil.getLOGKey(phone);
        //判断key值是否存在
        if(redisTemplate.hasKey(logKey)){
            //存在则获取之前存在redis的值
            System.out.println("再次发送验证码:"+redisTemplate.opsForValue().get(logKey));
        }else {
            //5分钟过期时间
            redisTemplate.opsForValue().set(logKey,code,60*5, TimeUnit.SECONDS);
            //发送的返回结果
            //String s = smsUtils.regSendSMS(phone, (String) redisTemplate.opsForValue().get(logKey));
            System.out.println("第一次发送验证码:"+code);
        }
        return (String) redisTemplate.opsForValue().get(logKey);
    }

    /**
     * 校验的时候不发送验证码
     * @param code
     * @param phone
     * @return
     */
    @Override
    public boolean checkCode(String code, String phone) {
        String redisCode = (String) redisTemplate.opsForValue().get(phone);
        if(redisCode.equals(code)){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 保存用户是否成功
     * @param user
     * @return
     */
    @Override
    public boolean saveUser(User user) {
        String salt = AESEncode.getCKey(16);
        user.setSalt(salt);
        user.setUid(UUIDUtil.randomUUID32());
        //0表示普通用户
        user.setType(0);
        //1表激活
        user.setStatus(NewBlogConstant.DEFAULT_REGISTER_STATUS);
        user.setCreateTime(new Date());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        try {
            user.setPassword(AESEncode.Encrypt(user.getPassword(),salt));
        } catch (Exception e) {
            e.printStackTrace();
        }
        int count = userMapper.saveUser(user);
        if(count>0){
            return true;
        }
        return false;
    }

    @Override
    public User findUserByUsernameOrPhone(String str) {
        User user = userMapper.getUserName(str);
        if(user == null){
            return null;
        }else {
            //1、获取盐
            String userSalt = user.getSalt();
            //2、获取密码
            String saltPassword = user.getPassword();
            //3、解密
            try {
                String userPasswd = AESEncode.Decrypt(saltPassword, userSalt);
                user.setPassword(userPasswd);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return user;
        }

    }

    @Override
    public User findByUserId(int userId) {
        User user = getCache(userId);
        if(user == null){
            user = initCache(userId);
        }
        return user;
    }

    /**
     * @param username 登录的用户名或者是手机号码
     * @param password 用户登录的密码，若为null则表示手机登录
     * @param expiredSeconds 登录的有效时间
     * @return
     */
    @Override
    public Map<String, Object> login(String username, String password,long expiredSeconds) {
        Map<String,Object> map = new HashMap<>();
        User loginUser = this.findUserByUsernameOrPhone(username);

        /**判断用户是否存在*/
        if (loginUser == null) {
            map.put("errorLogin","账号或密码错误");
            return map;
        }

        /**判断用户是否可以登录*/
        if (loginUser.getStatus() == 0) {
            map.put("usernameMsg","该账号未激活");
            return map;
        }
        /**表示账号密码登录*/
        if(password != null){
            //如果登录的密码与从数据库获取的密码不一样则说明登录是错误的
            if (!password.equals(loginUser.getPassword())) {
                map.put("errorLogin","账号或密码错误");
                return map;
            }
        }
        /**手机登录的时候判断有没有该手机的登录验证码*/
        if(password == null && redisTemplate.opsForValue().get(RedisKeyUtil.getLOGKey(username)) == null){
            map.put("errorLogin","请重新登录");
            return map;
        }

        /**生成登录凭证*/
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(loginUser.getId());
        loginTicket.setTicket(loginUser.getUid());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        /**把生成的登录凭证放入redis中*/
        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey,loginTicket);
        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    /**
     * 从缓存中获取登录凭证
     * @param ticket
     * @return
     */
    @Override
    public LoginTicket findLoginTicket(String ticket) {
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }


    /**
     *
     * @param ticket
     */
    @Override
    public void logout(String ticket) {
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey,loginTicket);
    }

    @Override
    public int updateHeader(int userId, String headerUrl) {
        int rows = userMapper.updateHeader(userId, headerUrl);
        clearCache(userId);
        return rows;
    }

    /**
     * 1.优先从缓存中取值
     * @param userId
     * @return
     */
    @Override
    public User getCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }

    /**
     * 2.取不到时初始化缓存数据
     * @param userId
     * @return
     */
    @Override
    public User initCache(int userId) {
        User user = userMapper.findByUserId(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        //1个小时的有效时间
        redisTemplate.opsForValue().set(redisKey,user,100,TimeUnit.SECONDS);
        return user;
    }

    /**
     * 3.数据变更时清除缓存数据
     * @param userId
     */
    @Override
    public void clearCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(int userId) {
        User user = this.findByUserId(userId);
        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                switch (user.getType()){
                    case 1:
                        // 管理员
                        return NewBlogConstant.AUTHORITY_ADMIN;
                    case 2:
                        // 版主
                        return NewBlogConstant.AUTHORITY_MODERATOR;
                    default:
                        // 用户
                        return NewBlogConstant.AUTHORITY_USER;
                }
            }
        });
        return list;
    }

    @Override
    public int updateUser(User user) {

        int res = userMapper.updateUserInfo(user);
        clearCache(user.getId());
        return res;
    }

}
