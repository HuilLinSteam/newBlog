package com.newblog.huil.service;

import com.newblog.huil.entity.Blog;
import org.springframework.data.domain.Page;

/**
 * @author HuilLIN
 */
public interface ElasticSearchService {

    /**
     * 新增或者修改博客用的保存
     * @param blog
     */
    public void saveBlog(Blog blog);

    /**
     * 删除博客
     * @param id
     */
    public void deleteBlog(int id);

    /**
     * 搜索博客
     * @param keyword
     * @param current
     * @param limit
     * @return
     */
    public Page<Blog> searchBlog(String keyword,int current,int limit);


}
