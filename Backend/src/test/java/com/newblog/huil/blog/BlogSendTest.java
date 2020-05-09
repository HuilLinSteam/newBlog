package com.newblog.huil.blog;

import com.newblog.huil.HuilApplication;
import com.newblog.huil.dao.BlogMapper;
import com.newblog.huil.entity.Blog;
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
public class BlogSendTest {
    @Autowired
    private BlogMapper blogMapper;

    @Test
    public void saveBlog(){
        Blog blog = new Blog();
        blog.setUserId(112);
        blog.setTitle("测试");
        blog.setContent("这是内容!");
        blog.setSummary("这是简介");
        blog.setType(1);
        blog.setStatus(0);
        blog.setCreateTime(new Date());
        blog.setCommentCount(10);
        blog.setScore(100);
        blog.setTags("测试");
        blog.setCatalogId(1);
        int res = blogMapper.insertBlog(blog);
        System.out.println(res);
    }
}
