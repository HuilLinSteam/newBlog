package com.newblog.huil.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newblog.huil.entity.Blog;
import com.newblog.huil.entity.Page;
import com.newblog.huil.entity.vo.ResultVo;
import com.newblog.huil.service.BlogService;
import com.newblog.huil.service.ElasticSearchService;
import com.newblog.huil.service.LikeService;
import com.newblog.huil.service.UserService;
import com.newblog.huil.utils.CheckUserUtil;
import com.newblog.huil.utils.NewBlogConstant;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author HuilLIN
 */
@RestController
public class SearchController {
    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);
    @Autowired
    private ElasticSearchService elasticSearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private BlogService blogService;

    private ResultVo resultVo = null;
    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public String search(String keyword, Page page) throws JsonProcessingException {
        Map<String, Object> resMap = new HashMap<>();
        // 搜索博客
        org.springframework.data.domain.Page<Blog> searchResults =
                elasticSearchService.searchBlog(keyword, page.getCurrent() -1 , page.getLimit());
        // 聚合数据
        List<Map<String, Object>> blogs = new ArrayList<>();
        if (searchResults != null) {
            for (Blog blog : searchResults) {
                Map<String, Object> map = new HashMap<>();
                //帖子
                map.put("blog", blog);
                //作者
                map.put("user", CheckUserUtil.getUser(userService.findByUserId(blog.getUserId())));
                // 点赞数量
                map.put("likeCount", likeService.findEntityLikeCount(NewBlogConstant.ENTITY_TYPE_POST,
                        blog.getId()));
                int enjoyCount = blogService.getEnjoyCount(blog.getId());
                map.put("enjoyCount",enjoyCount);
                blogs.add(map);

            }
        }

        resMap.put("blogMap", blogs);
        resMap.put("keyword", keyword);

        // 分页
        page.setPath("/search?keyword=" + keyword);
        page.setRows(searchResults == null ? 0 : (int) searchResults.getTotalElements());
        resMap.put("page",page);
        resultVo = new ResultVo(1,resMap,"获取搜索内容成功!");
        String jsonString = new ObjectMapper().writeValueAsString(resultVo);
        return jsonString;
    }
}
