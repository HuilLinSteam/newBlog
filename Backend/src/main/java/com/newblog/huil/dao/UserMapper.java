package com.newblog.huil.dao;

import com.newblog.huil.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author HuilLIN
 */
@Mapper
@Repository
public interface UserMapper {
    /**
     * 根据用户id查询用户所有信息
     * @param userId
     * @return
     */
    User findByUserId(int userId);

    /**
     * 根据用户名获取用户信息
     * @param username
     * @return
     */
    User getUserName(@Param("username") String username);

    /**
     * 根据手机获取用户信息
     * @param phone
     * @return
     */
    User getUserPhone(@Param("phone") String phone);

    /**
     * 增加一个用户
     * @param user
     * @return
     */
    int saveUser(@Param("user") User user);

    /**
     * 更新用户的登录状态
     * @param id
     * @param status
     * @return
     */
    int updateStatus(int id, int status);

    /**
     * 更新用户的头像
     * @param id
     * @param headerUrl
     * @return
     */
    int updateHeader(int id, String headerUrl);

    /**
     * 修改用户的密码
     * @param id
     * @param password
     * @return
     */
    int updatePassword(int id, String password);

    /**
     * 修改用户信息
     * @param user
     * @return
     */
    int updateUserInfo(User user);
}
