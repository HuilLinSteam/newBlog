package com.newblog.huil.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newblog.huil.entity.Event;
import com.newblog.huil.entity.Page;
import com.newblog.huil.entity.User;
import com.newblog.huil.entity.vo.ResultVo;
import com.newblog.huil.event.EventProducer;
import com.newblog.huil.service.FollowService;
import com.newblog.huil.service.UserService;
import com.newblog.huil.utils.CheckUserUtil;
import com.newblog.huil.utils.HostHolder;
import com.newblog.huil.utils.JsonUtil;
import com.newblog.huil.utils.NewBlogConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author HuilLIN
 */
@RestController
public class FollowController {
    @Autowired
    private FollowService followService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    private ResultVo resultVo;

    @RequestMapping(path = "/follow",method = RequestMethod.POST)
    public String follow(int entityType,int entityId){
        User user = hostHolder.getUser();

        followService.follow(user.getId(),entityType,entityId);

        // 触发关注事件
        Event event = new Event()
                .setTopic(NewBlogConstant.TOPIC_FOLLOW)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId);
        eventProducer.fireEvent(event);

        return JsonUtil.getJSONString(1,"关注成功!");
    }


    @RequestMapping(path = "/unFollow",method = RequestMethod.POST)
    public String unFollow(int entityType,int entityId){
        User user = hostHolder.getUser();
        followService.unFollow(user.getId(),entityType,entityId);
        return JsonUtil.getJSONString(1,"取消关注!");
    }

    /**
     * 查询关注的人
     * @param userId
     * @param page
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(path = "/followees/{userId}",method = RequestMethod.GET)
    public String getFollowees(@PathVariable("userId")int userId, Page page) throws JsonProcessingException {
        User tempUser = userService.findByUserId(userId);
        boolean userIsNull = CheckUserUtil.UserIsNull(tempUser);
        if(userIsNull){
            return JsonUtil.getJSONString(2,"用户不存在");
        }
        Map<String,Object> resMap = new HashMap<>();
        User user = CheckUserUtil.getUser(tempUser);
        resMap.put("user",user);
        page.setLimit(5);
        page.setPath("/followees/"+userId);
        page.setRows((int)followService.findFolloweeCount(userId,NewBlogConstant.ENTITY_TYPE_USER));
        List<Map<String, Object>> userList = followService.findFollowees(userId, page.getOffset(), page.getLimit());
        if(userList != null){
            for (Map<String, Object> map : userList) {
                User u = (User)map.get("user");
                map.put("hasFollowed",hasFollowed(u.getId()));
            }
        }
        resMap.put("users",userList);
        resMap.put("page",page);
        resultVo = new ResultVo(1,resMap,"获取用户关注信息成功！");
        String jsonString = new ObjectMapper().writeValueAsString(resultVo);
        return jsonString;
    }

    @RequestMapping(path = "/followers/{userId}",method = RequestMethod.GET)
    public String getFollowers(@PathVariable("userId") int userId,Page page) throws JsonProcessingException {
        User tempUser = userService.findByUserId(userId);
        boolean userIsNull = CheckUserUtil.UserIsNull(tempUser);
        if(userIsNull){
            return JsonUtil.getJSONString(2,"用户不存在");
        }
        Map<String, Object> resMap = new HashMap<>();
        User user = CheckUserUtil.getUser(tempUser);
        resMap.put("user",user);
        page.setLimit(5);
        page.setPath("/followers/"+userId);
        page.setRows((int)followService.findFollowerCount(NewBlogConstant.ENTITY_TYPE_USER,userId));
        List<Map<String, Object>> userList = followService.findFollowers(userId, page.getOffset(), page.getLimit());
        if(userList != null){
            for (Map<String, Object> map : userList) {
                User u = (User)map.get("user");
                map.put("hasFollowed",hasFollowed(u.getId()));
            }
        }
        resMap.put("users",userList);
        resMap.put("page",page);
        resultVo = new ResultVo(1,resMap,"获取关注人数成功!");
        String jsonString = new ObjectMapper().writeValueAsString(resultVo);
        return jsonString;
    }

    private boolean hasFollowed(int userId) {
        if(hostHolder.getUser() == null){
            return false;
        }
        return followService.hasFollowed(hostHolder.getUser().getId(),NewBlogConstant.ENTITY_TYPE_USER,userId);
    }
}
