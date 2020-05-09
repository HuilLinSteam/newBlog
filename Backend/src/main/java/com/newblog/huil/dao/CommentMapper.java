package com.newblog.huil.dao;

import com.newblog.huil.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author HuilLIN
 */
@Mapper
@Repository
public interface CommentMapper {
    /**
     * 获取评论
     * @param entityType 实体类型
     * @param entityId   实体的id
     * @param offset     分页
     * @param limit      显示的行数
     * @return
     */
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    /**
     * 查询条数
     * @param entityType 实体类型
     * @param entityId   实体的id
     * @return
     */
    int selectCountByEntity(int entityType,int entityId);

    /**
     * 插入评论
     * @param comment
     * @return
     */
    int insertComment(Comment comment);

    /**
     * 根据评论的id获取评论
     * @param id
     * @return
     */
    Comment selectCommentById(int id);
}
