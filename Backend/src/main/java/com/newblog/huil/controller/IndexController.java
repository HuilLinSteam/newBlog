package com.newblog.huil.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newblog.huil.entity.Blog;
import com.newblog.huil.entity.Page;
import com.newblog.huil.entity.User;
import com.newblog.huil.entity.vo.ResultVo;
import com.newblog.huil.service.BlogService;
import com.newblog.huil.service.LikeService;
import com.newblog.huil.service.UserService;
import com.newblog.huil.utils.CheckUserUtil;
import com.newblog.huil.utils.JsonUtil;
import com.newblog.huil.utils.NewBlogConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author HuilLIN
 */
@RestController
@Slf4j
public class IndexController {


    @Autowired
    private BlogService blogService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    ResultVo resultVo = null;

    @RequestMapping(path = "/",method = RequestMethod.GET)
    public String root(){
        return "forward:/index";
    }

    @RequestMapping(path = "/index",method = RequestMethod.GET)
    public String getIndexPage(Page page, @RequestParam(name = "orderMode",defaultValue = "0")int orderMode) throws JsonProcessingException {
        //设置行数,因为是首页所以是0
        page.setRows(blogService.findBlogRows(0));
        page.setPath("/index?orderMode="+orderMode);
        List<Blog> list = blogService.findBlog(0,page.getOffset(),page.getLimit(),orderMode);
        List<Map<String,Object>> blogMap = new ArrayList<>();
        if(list!=null){
            for (Blog blog : list) {
                Map<String,Object> map = new HashMap<>();
                map.put("blog",blog);
                User tempUser = userService.findByUserId(blog.getUserId());
                User user = CheckUserUtil.getUser(tempUser);
                map.put("user",user);
                long likeCount = likeService.findEntityLikeCount(NewBlogConstant.ENTITY_TYPE_POST, blog.getId());
                map.put("likeCount",likeCount);
                int enjoyCount = blogService.getEnjoyCount(blog.getId());
                map.put("enjoyCount",enjoyCount);
                blogMap.add(map);
            }
        }

        Map<String,Object> resMap = new HashMap<>();
        resMap.put("blogMap",blogMap);
        resMap.put("orderMode",orderMode);
        resMap.put("page",page);
        String jsonString = new ObjectMapper().writeValueAsString(resMap);
        return jsonString;
    }




}
