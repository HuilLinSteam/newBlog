package com.newblog.huil.blog;

import com.newblog.huil.HuilApplication;
import com.newblog.huil.utils.RedisKeyUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = HuilApplication.class)
public class AddEnjoyCount {
    private static final Logger logger = LoggerFactory.getLogger(AddEnjoyCount.class);
    @Autowired
    RedisTemplate redisTemplate;
    private static final int BLOG_ID = 22;

    @Test
    public void addCount() {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String enjoyBlogKey = RedisKeyUtil.getEnjoyBlogKey(BLOG_ID);
                Boolean hasKey = redisTemplate.hasKey(enjoyBlogKey);
                redisOperations.multi();
                if(hasKey){
                    redisTemplate.opsForValue().increment(enjoyBlogKey);
                }else {
                    redisTemplate.opsForValue().set(enjoyBlogKey,0);
                }
                return redisOperations.exec();
            }
        });

    }

    @Test
    public void getCount() {
        String enjoyBlogKey = RedisKeyUtil.getEnjoyBlogKey(BLOG_ID);
        Integer count = (Integer) redisTemplate.opsForValue().get(enjoyBlogKey);
        int readCount = count == null ? 0 : count.intValue();
        logger.info("当前博客Id:" + BLOG_ID + "的浏览量为:" + readCount);
    }
}
