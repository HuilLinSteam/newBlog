package com.newblog.huil.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newblog.huil.annotation.LoginRequired;
import com.newblog.huil.entity.User;
import com.newblog.huil.entity.vo.ResultVo;
import com.newblog.huil.service.FollowService;
import com.newblog.huil.service.LikeService;
import com.newblog.huil.service.UserService;
import com.newblog.huil.utils.*;
import com.newblog.huil.utils.encode.AESEncode;
import com.sun.media.jfxmediaimpl.HostUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author HuilLIN
 */
@RestController
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;
    private ResultVo resultVo;

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private FollowService followService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private OSSClientUtil ossClientUtil;

    @RequestMapping(value = "/saveUser", method = RequestMethod.POST)
    public String saveUser(HttpServletRequest request) throws Exception {
        Map<String, String[]> map = request.getParameterMap();
        User user = new User();
        BeanUtils.populate(user, map);
        boolean res = userService.saveUser(user);
        //用户保存成功
        if (res) {
            resultVo = new ResultVo(1, "添加用户成功");
        } else {
            resultVo = new ResultVo(2, "添加用户失败");
        }
        String json = new ObjectMapper().writeValueAsString(resultVo);
        return json;
    }



    @RequestMapping(path = "/logout", method = RequestMethod.POST)
    public String logout(@CookieValue("loginTicket") String ticket) throws JsonProcessingException {
        userService.logout(ticket);
        logger.info("用户退出的Cookie:"+ticket);
        SecurityContextHolder.clearContext();
        resultVo = new ResultVo(1, "退出成功!");
        String json = new ObjectMapper().writeValueAsString(resultVo);
        return json;
    }

    @RequestMapping(path = "/myUserInfo", method = RequestMethod.GET)
    public String myUserInfo() throws JsonProcessingException {
        User tempUser = hostHolder.getUser();
        Map<String, Object> map = new HashMap<>();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        HttpSession session =request.getSession();
        if (tempUser == null) {
            resultVo = new ResultVo(2, "获取用户信息失败");
        } else {
            User user = userService.findByUserId(tempUser.getId());
            map.put("user", user);
            ///int unReadCount = (int) redisTemplate.opsForValue().get(RedisKeyUtil.getUnReadCountKey(user.getId()));
            map.put("allUnreadCount",session.getAttribute("allUnreadCount"));
            resultVo = new ResultVo(1, map, "获取用户信息成功");
        }
        String json = new ObjectMapper().writeValueAsString(resultVo);
        return json;
    }

    @RequestMapping(path = "/profile/{userId}",method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId) throws JsonProcessingException {
        User tempUser = userService.findByUserId(userId);
        if(tempUser == null){
            throw new RuntimeException("该用户不存在!");
        }
        Map<String, Object> map = new HashMap<>();
        // 用户
        User user = CheckUserUtil.getUser(tempUser);
        map.put("user",user);
        // 用户目前收到的赞
        int likeCount = likeService.findUserLikeCount(userId);
        map.put("likeCount",likeCount);

        //关注的数量
        long followeeCount = followService.findFolloweeCount(userId, NewBlogConstant.ENTITY_TYPE_USER);
        map.put("followeeCount",followeeCount);

        // 粉丝的数量
        long followerCount = followService.findFollowerCount(NewBlogConstant.ENTITY_TYPE_USER, userId);
        map.put("followerCount",followerCount);

        //是否已关注
        boolean hasFollowed = false;
        if(hostHolder.getUser() != null){
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), NewBlogConstant.ENTITY_TYPE_USER, userId);
        }
        map.put("hasFollowed",hasFollowed);

        resultVo = new ResultVo(1,map,"用户简介成功");
        String jsonString = new ObjectMapper().writeValueAsString(resultVo);
        return jsonString;
    }

    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadUserHeaderImg(MultipartFile headerImage) throws Exception {
        if (headerImage == null) {
            return JsonUtil.getJSONString(2,"你没有上传图片哦！");
        }
        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            return JsonUtil.getJSONString(2,"文件格式不正确哦！");
        }
        // 生成随机文件名
        String imgFileName = ossClientUtil.uploadImage2Oss(headerImage);
        String imgUrl = ossClientUtil.getImgUrl(imgFileName);
        resultVo = new ResultVo(1,imgUrl,"上传成功！");
        return new ObjectMapper().writeValueAsString(resultVo);
    }

    @RequestMapping(path = "/updateUserInfo",method = RequestMethod.POST)
    public String updateUserInfo(User user) throws Exception {
        User tempUser = userService.findByUserId(user.getId());
        // 姓名
        if(user.getName() != null || user.getName().equals("")){
            tempUser.setName(user.getName());
        }
        // 邮箱
        if(user.getEmail() != null || !user.getEmail().equals("")){
            tempUser.setEmail(user.getEmail());
        }
        // 密码
        if(user.getPassword() != null || !user.getPassword().equals("")){
            tempUser.setPassword(AESEncode.Encrypt(user.getPassword(),tempUser.getSalt()));
        }
        // 修改头像
        if(user.getHeaderUrl() != null){
            tempUser.setHeaderUrl(user.getHeaderUrl());
        }
        int res = userService.updateUser(tempUser);
        if(res > 0){
            resultVo = new ResultVo(1,"用户信息修改成功!");
        }else{
            resultVo = new ResultVo(2,"用户信息修改失败!");
        }
        return new ObjectMapper().writeValueAsString(resultVo);
    }

}
