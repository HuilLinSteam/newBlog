package com.newblog.huil.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newblog.huil.entity.Event;
import com.newblog.huil.entity.User;
import com.newblog.huil.entity.vo.ResultVo;
import com.newblog.huil.event.EventProducer;
import com.newblog.huil.service.LikeService;
import com.newblog.huil.utils.CheckUserUtil;
import com.newblog.huil.utils.HostHolder;
import com.newblog.huil.utils.NewBlogConstant;
import com.newblog.huil.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author HuilLIN
 */
@RestController
public class LikeController {
    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    private ResultVo resultVo;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private RedisTemplate redisTemplate;


    @RequestMapping(path = "/like", method = RequestMethod.GET)
    public String like(int entityType, int entityId, int entityUserId, int blogId) throws JsonProcessingException {
        User user = hostHolder.getUser();
        /**这个可以通过拦截器来判断用户是否登录，比如登录才给点赞。但可以用Security*/
        if (CheckUserUtil.UserIsNull(user)) {
            resultVo = new ResultVo(2, "请登录哦！");
        } else {
            //点赞
            likeService.like(user.getId(), entityType, entityId, entityUserId);
            //数量的获取
            long likeCount = likeService.findEntityLikeCount(entityType, entityId);
            //状态
            int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);
            Map<String, Object> map = new HashMap<>();
            map.put("likeCount", likeCount);
            map.put("likeStatus", likeStatus);

            // 触发点赞事件(点赞也是属于修改博客的熟悉，所以要触发并修改)
            if (likeStatus == 1) {
                Event event = new Event()
                        .setTopic(NewBlogConstant.TOPIC_LIKE)
                        .setUserId(hostHolder.getUser().getId())
                        .setEntityType(entityType)
                        .setEntityId(entityId)
                        .setEntityUserId(entityUserId)
                        .setData("blogId", blogId);
                eventProducer.fireEvent(event);
            }

            // 对博客点赞才计算分数
            if (entityType == NewBlogConstant.ENTITY_TYPE_POST) {
                // 计算博客的分数
                String redisKey = RedisKeyUtil.getBlogScoreKey();
                redisTemplate.opsForSet().add(redisKey, blogId);
            }

            resultVo = new ResultVo(1, map, likeStatus == 1 ? "点赞成功" : "取消点赞");
        }
        String json = new ObjectMapper().writeValueAsString(resultVo);
        return json;
    }

}
