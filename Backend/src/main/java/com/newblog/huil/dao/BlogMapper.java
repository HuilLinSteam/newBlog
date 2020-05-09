package com.newblog.huil.dao;

import com.newblog.huil.entity.Blog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author HuilLIN
 */
@Mapper
@Repository
public interface BlogMapper {
    /**
     * userId可以用来查询“我发过的博客”
     * 现在是查询所有的博客
     * @param userId
     * @param offset 起始行行号
     * @param limit  每页的显示数量
     * @return
     */
    List<Blog> selectBlog(int userId,int offset,int limit, int orderMode);

    /**
     * 查询所有的博客数量
     * @param userId
     * @return
     */
    int selectAllBlogRows(@Param("userId") int userId);

    /**
     * 新增博客
    * @param blog
     * @return
     */
    int insertBlog(Blog blog);

    /**
     * 根据博客id获取博客
     * @param id
     * @return
     */
    Blog selectBlogById(int id);

    /**
     * 更新评论的数量
     * @param id
     * @param commentCount
     * @return
     */
    int updateCommentCount(int id, int commentCount);

    /**
     * 更新博客的类型
     * type:1置顶，0普通
     * @param id
     * @param type
     * @return
     */
    int updateType(int id,int type);

    /**
     * 更新博客的状态
     * 0-正常; 1-精华; 2-拉黑;
     * @param id
     * @param status
     * @return
     */
    int updateStatus(int id,int status);

    /**
     * 更新博客的分数
     * @param id
     * @param score
     * @return
     */
    int updateScore(int id,double score);

    /**
     * 修改博客
     * @param blog
     * @return
     */
    int updateBlog(Blog blog);
}
