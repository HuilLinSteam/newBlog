package com.newblog.huil.user;

import com.newblog.huil.HuilApplication;
import com.newblog.huil.dao.UserMapper;
import com.newblog.huil.entity.User;
import com.newblog.huil.service.UserService;
import com.newblog.huil.service.impl.UserServiceImpl;
import com.newblog.huil.utils.UUIDUtil;
import com.newblog.huil.utils.encode.AESEncode;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = HuilApplication.class)
public class CheckUserRegisterTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void testSaveUser() throws Exception {
        User user = new User();
        String salt = AESEncode.getCKey(16);
        user.setUsername("HuilLIN");
        user.setPassword(AESEncode.Encrypt("123456",salt));
        user.setUid(UUIDUtil.randomUUID32());
        user.setPhone("18127963856");
        user.setSalt(salt);
        int i = userMapper.saveUser(user);
        System.out.println(i);
    }

    @Test
    public void testGetUser() throws Exception {
        User user = new User();
        String salt = AESEncode.getCKey(16);
        user.setUsername("HuilLIN");
        user.setPassword(AESEncode.Encrypt("123456",salt));
        user.setUid(UUIDUtil.randomUUID32());
        user.setPhone("18127963856");
        user.setSalt(salt);
        int i = userMapper.saveUser(user);
        System.out.println(i);
    }

}
