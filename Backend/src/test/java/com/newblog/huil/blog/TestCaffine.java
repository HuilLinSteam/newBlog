package com.newblog.huil.blog;

import com.newblog.huil.HuilApplication;
import com.newblog.huil.entity.Blog;
import com.newblog.huil.service.BlogService;
import org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = HuilApplication.class)
public class TestCaffine {
    @Autowired
    private BlogService blogService;
    @Test
    public void initDataForTest(){
        for (int i = 0; i < 100; i++) {
            Blog blog = new Blog();
            blog.setUserId(112);
            blog.setTitle("分布式锁");
            blog.setSummary("分布式锁如何使用呢?");
            blog.setContent("众所周知分布式是....");
            blog.setHtmlContent("众所周知分布式是....");
            blog.setCreateTime(new Date());
            blog.setScore(Math.random() * 2000);
            blogService.saveBlog(blog);
        }
    }
    @Test
    public void testCafin(){
        System.out.println(blogService.findBlog(0,0,10,1));
        System.out.println(blogService.findBlog(0,0,10,1));
        System.out.println(blogService.findBlog(0,0,10,1));
        System.out.println(blogService.findBlog(0,0,10,0));

    }


}
