package com.newblog.huil.user;

import com.newblog.huil.HuilApplication;
import com.newblog.huil.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = HuilApplication.class)
public class RedisTemplateTest {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserService userService;
    @Test
    public void testString(){
        String redisKey = "phone-reg";
        redisTemplate.opsForValue().set(redisKey,"18127963856");
        String o = (String) redisTemplate.opsForValue().get(redisKey);
        System.out.println(o);
        String count = "human-count";
        redisTemplate.opsForValue().set(count,1);
        System.out.println(redisTemplate.opsForValue().get(count));
        System.out.println(redisTemplate.opsForValue().increment(count));
        System.out.println(redisTemplate.opsForValue().decrement(count));
    }

    @Test
    public void testSendSms(){
        String regSMS = userService.getRegSMS("1827963866");
        System.out.println(regSMS);
    }
}
