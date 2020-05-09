package com.newblog.huil.controller;

import com.newblog.huil.annotation.LoginRequired;
import com.newblog.huil.entity.Blog;
import com.newblog.huil.entity.Comment;
import com.newblog.huil.entity.Event;
import com.newblog.huil.event.EventConsumer;
import com.newblog.huil.event.EventProducer;
import com.newblog.huil.service.BlogService;
import com.newblog.huil.service.CommentService;
import com.newblog.huil.utils.HostHolder;
import com.newblog.huil.utils.JsonUtil;
import com.newblog.huil.utils.NewBlogConstant;
import com.newblog.huil.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author HuilLIN
 */
@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private BlogService blogService;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(path = "/add/{blogId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("blogId") int blogId, Comment comment) {
        String jsonString = "";
        if (hostHolder.getUser() == null) {
            jsonString = JsonUtil.getJSONString(2, "请登录后再评论哦!");
        } else {
            comment.setUserId(hostHolder.getUser().getId());
            comment.setStatus(0);
            comment.setCreateTime(new Date());
            commentService.addComment(comment);

            // 触发评论事件
            Event event = new Event()
                    .setTopic(NewBlogConstant.TOPIC_COMMENT)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(comment.getEntityType())
                    .setEntityId(comment.getEntityId())
                    .setData("blogId", blogId);
            if (comment.getEntityType() == NewBlogConstant.ENTITY_TYPE_POST) {
                Blog target = blogService.findBlogById(comment.getEntityId());
                event.setEntityUserId(target.getUserId());
            } else if (comment.getEntityType() == NewBlogConstant.ENTITY_TYPE_COMMENT) {
                Comment target = commentService.findCommentById(comment.getEntityId());
                event.setEntityUserId(target.getUserId());
            }
            eventProducer.fireEvent(event);

            if (comment.getEntityType() == NewBlogConstant.ENTITY_TYPE_POST) {
                // 触发发布博客事件
                event = new Event()
                        .setTopic(NewBlogConstant.TOPIC_PUBLISH)
                        .setUserId(comment.getUserId())
                        .setEntityType(NewBlogConstant.ENTITY_TYPE_POST)
                        .setEntityId(blogId);
                eventProducer.fireEvent(event);
                // 计算博客的分数
                String redisKey = RedisKeyUtil.getBlogScoreKey();
                redisTemplate.opsForSet().add(redisKey, blogId);
            }

            jsonString = JsonUtil.getJSONString(1, "评论成功");
        }
        return jsonString;
    }
}
