package com.newblog.huil.controller;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newblog.huil.entity.*;
import com.newblog.huil.entity.vo.ResultVo;
import com.newblog.huil.event.EventProducer;
import com.newblog.huil.service.BlogService;
import com.newblog.huil.service.CommentService;
import com.newblog.huil.service.LikeService;
import com.newblog.huil.service.UserService;
import com.newblog.huil.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.sql.ResultSet;
import java.util.*;

/**
 * @author HuilLIN
 */
@RequestMapping("/blog")
@RestController
public class BlogController {
    private static final int BLOG_DELETE_STATUS = 2;
    private static final Logger logger = LoggerFactory.getLogger(BlogController.class);
    @Autowired
    private BlogService blogService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    private ResultVo resultVo = null;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    public String addBlog(Blog blog) {
        User tempUser = hostHolder.getUser();
        if (tempUser == null) {
            return JsonUtil.getJSONString(403, "你还没有登录哦！");
        }

        blog.setUserId(tempUser.getId());
        blog.setCreateTime(new Date());

        int blogId = blogService.saveBlog(blog);
        logger.info("发布的blogId："+blogId);

        // 触发事件
        Event event = new Event()
                .setTopic(NewBlogConstant.TOPIC_PUBLISH)
                .setUserId(tempUser.getId())
                .setEntityType(NewBlogConstant.ENTITY_TYPE_POST)
                .setEntityId(blog.getId());
        eventProducer.fireEvent(event);

        // 计算博客分数
        String redisKey = RedisKeyUtil.getBlogScoreKey();
        redisTemplate.opsForSet().add(redisKey,blog.getId());

        return JsonUtil.getJSONString(1, "发布成功!",blogId);
    }

    @RequestMapping(path = "/detail/{blogId}", method = RequestMethod.GET)
    public String getBlog(@PathVariable("blogId") int blogId, Page page) throws JsonProcessingException {
        //博客
        Blog blog = blogService.findBlogById(blogId);
        if(blog.getStatus() == BLOG_DELETE_STATUS ){
            return JsonUtil.getJSONString(2,"该博客不存在!");
        }
        if (blog == null) {
            resultVo = new ResultVo(2, "博客不存在");
        } else {
            Map<String, Object> map = new HashMap<>();
            //作者
            User tempUser = userService.findByUserId(blog.getUserId());
            User user = CheckUserUtil.getUser(tempUser);
            //点赞数量
            long likeCount = likeService.findEntityLikeCount(NewBlogConstant.ENTITY_TYPE_POST, blogId);
            map.put("likeCount", likeCount);
            //点赞状态
            int likeStatus = hostHolder.getUser() == null ? 0 :
                    likeService.findEntityLikeStatus(hostHolder.getUser().getId(), NewBlogConstant.ENTITY_TYPE_POST, blogId);
            map.put("likeStatus", likeStatus);
            // 评论分页信息
            page.setLimit(5);
            page.setPath("/blog/detail/" + blogId);
            page.setRows(blog.getCommentCount());

            // 评论:给博客的评论
            // 回复:给评论的评论
            // 评论列表
            List<Comment> commentList = commentService.findCommentsByEntity(NewBlogConstant.ENTITY_TYPE_POST,
                    blog.getId(), page.getOffset(), page.getLimit());
            // 评论VO列表
            List<Map<String, Object>> commentVoList = new ArrayList<>();
            if (commentList != null) {
                for (Comment comment : commentList) {
                    // 评论VO
                    Map<String, Object> commentVo = new HashMap<>();
                    // 评论
                    commentVo.put("comment", comment);
                    // 作者
                    User commentTempUser = userService.findByUserId(comment.getUserId());
                    User commentUser = CheckUserUtil.getUser(commentTempUser);
                    commentVo.put("commentUser", commentUser);

                    //评论点赞的数量
                    likeCount = likeService.findEntityLikeCount(NewBlogConstant.ENTITY_TYPE_COMMENT, comment.getId());
                    commentVo.put("likeCount", likeCount);
                    //点赞的状态
                    likeStatus = hostHolder.getUser() == null ? 0 :
                            likeService.findEntityLikeStatus(hostHolder.getUser().getId(),
                                    NewBlogConstant.ENTITY_TYPE_COMMENT, comment.getId());
                    commentVo.put("likeStatus", likeStatus);

                    // 回复列表
                    List<Comment> replyList = commentService.findCommentsByEntity(
                            NewBlogConstant.ENTITY_TYPE_COMMENT, comment.getId(), 0,
                            Integer.MAX_VALUE
                    );

                    // 回复VO列表
                    List<Map<String, Object>> replyVoList = new ArrayList<>();
                    if (replyList != null) {
                        for (Comment reply : replyList) {
                            Map<String, Object> replyVo = new HashMap<>();
                            // 回复
                            replyVo.put("reply", reply);
                            // 作者
                            User replyTempUser = userService.findByUserId(reply.getUserId());
                            User replyUser = CheckUserUtil.getUser(replyTempUser);
                            replyVo.put("replyUser", replyUser);
                            // 回复目标
                            User targetTempUser = userService.findByUserId(reply.getTargetId());
                            User targetUser = null;
                            if(CheckUserUtil.UserIsNull(targetTempUser)){}
                            else {
                                targetUser = CheckUserUtil.getUser(targetTempUser);
                            }
                            User target = reply.getTargetId() == 0 ? null : targetUser;
                            replyVo.put("target", target);
                            //点赞数量
                            likeCount = likeService.findEntityLikeCount(NewBlogConstant.ENTITY_TYPE_COMMENT,
                                    reply.getId());
                            replyVo.put("likeCount",likeCount);
                            //点赞数量
                            likeStatus = hostHolder.getUser() == null ? 0 :
                                    likeService.findEntityLikeStatus(hostHolder.getUser().getId(),
                                            NewBlogConstant.ENTITY_TYPE_COMMENT, reply.getId());
                            replyVo.put("likeStatus", likeStatus);
                            replyVoList.add(replyVo);
                        }
                    }
                    commentVo.put("replys", replyVoList);
                    //回复数量
                    int replyCount = commentService.findCommentCount(NewBlogConstant.ENTITY_TYPE_COMMENT, comment.getId());
                    commentVo.put("replyCount", replyCount);
                    commentVoList.add(commentVo);
                }
            }
            // 增加阅读量
            blogService.addEnjoyCount(blogId);
            // 获取阅读量
            int enjoyCount = blogService.getEnjoyCount(blogId);
            map.put("enjoyCount",enjoyCount);
            map.put("blog", blog);
            map.put("user", user);
            map.put("page", page);
            map.put("comments", commentVoList);
            resultVo = new ResultVo(1, map, "获取博客内容");
        }
        String json = new ObjectMapper().writeValueAsString(resultVo);
        return json;
    }

    /**
     * 置顶
     * @param id 博客的id
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(path = "/top",method = RequestMethod.POST)
    public String setTop(int id) throws JsonProcessingException {
        blogService.updateType(id,1);

        // 触发发帖事件
        Event event = new Event()
                .setTopic(NewBlogConstant.TOPIC_PUBLISH)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(NewBlogConstant.ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        resultVo = new ResultVo(1,"置顶成功！");
        String jsonString = new ObjectMapper().writeValueAsString(resultVo);
        return jsonString;
    }

    /**
     * 加精
     * @param blogId 博客的id
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(path = "/wonderful",method = RequestMethod.POST)
    public String setWonderful(int blogId) throws JsonProcessingException {
        blogService.updateStatus(blogId,1);
        // 触发发帖事件
        Event event = new Event()
                .setTopic(NewBlogConstant.TOPIC_PUBLISH)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(NewBlogConstant.ENTITY_TYPE_POST)
                .setEntityId(blogId);
        eventProducer.fireEvent(event);
        String redisKey = RedisKeyUtil.getBlogScoreKey();
        redisTemplate.opsForSet().add(redisKey,blogId);

        resultVo = new ResultVo(1,"加精成功！");
        String jsonString = new ObjectMapper().writeValueAsString(resultVo);
        return jsonString;
    }

    /**
     * 删除博客
     * @param id 博客的id
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(path = "/delete",method = RequestMethod.POST)
    public String setDelete(int id) throws JsonProcessingException {
        // 2表示这篇博客被拉黑
        blogService.updateStatus(id,2);
        // 触发删帖事件
        Event event = new Event()
                .setTopic(NewBlogConstant.TOPIC_DELETE)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(NewBlogConstant.ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);
        resultVo = new ResultVo(1,"删除成功！");
        String jsonString = new ObjectMapper().writeValueAsString(resultVo);
        return jsonString;
    }

    @RequestMapping(path = "/updateBlog",method = RequestMethod.POST)
    public String updateBlog(Blog blog) throws JsonProcessingException {
        Blog tempBlog = blogService.findBlogById(blog.getId());
        if(tempBlog.getUserId() != hostHolder.getUser().getId()){
            return JsonUtil.getJSONString(2,"这不是你的博客！请不要修改哦！");
        }
        int result = blogService.updateBlog(blog);

        if(result > 0){
            resultVo = new ResultVo(1,"更新成功!");
        }else{
            resultVo = new ResultVo(1,"更新失败!");
        }
        return new ObjectMapper().writeValueAsString(resultVo);
    }
}
