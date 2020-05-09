package com.newblog.huil.user;

import com.newblog.huil.HuilApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = HuilApplication.class)
public class LoginTest {
    @Test
    public void getTime() {
        System.out.println(new Date(System.currentTimeMillis() + 5 * 60 * 1000));
        System.out.println(new Date(1587886830646L));
    }
}
