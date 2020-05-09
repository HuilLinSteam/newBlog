package com.newblog.huil.service;

import com.newblog.huil.entity.Blog;

import java.util.List;

/**
 * @author HuilLIN
 */
public interface BlogService {
    /**
     * 获取博客
     * @param userId 若填写，则根据userId获取对应的博客，不填写则默认为0
     * @param offset
     * @param limit
     * @param orderMode
     * @return
     */
    public List<Blog> findBlog(int userId,int offset,int limit,int orderMode);

    /**
     * 获取博客的数量
     * @param userId 填写则获取某个user的博客数量
     * @return
     */
    public int findBlogRows(int userId);

    /**
     * 用于新增博客或者保存博客
     * @param blog
     * @return
     */
    public int saveBlog(Blog blog);

    /**
     * 根据id找到博客
     * @param id
     * @return
     */
    public Blog findBlogById(int id);

    /**
     * 更新博客评论的数量
     * @param id
     * @param commentCount
     * @return
     */
    public int updateCommentCount(int id,int commentCount);

    /**
     * 返回博客的阅读数
     * @param blogId
     * @return
     */
    public int getEnjoyCount(int blogId);

    /**
     * 增加博客的阅读量
     * @param blogId
     * @return
     */
    public void addEnjoyCount(int blogId);

    /**
     * 更新博客的类型
     * 0-普通; 1-置顶;
     * @param id
     * @param type
     * @return
     */
    public int updateType(int id,int type);

    /**
     * 更新博客的状态
     * 0-正常; 1-精华; 2-拉黑;
     * @param id
     * @param status
     * @return
     */
    public int updateStatus(int id,int status);

    /**
     * 更新博客的分数
     * @param id
     * @param score
     * @return
     */
    public int updateScore(int id,double score);

    /**
     * 修改博客信息
     * @param blog
     * @return
     */
    public int updateBlog(Blog blog);
}
