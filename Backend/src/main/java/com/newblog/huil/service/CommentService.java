package com.newblog.huil.service;

import com.newblog.huil.entity.Comment;

import java.util.List;

/**
 * @author HuilLIN
 */
public interface CommentService {
    /**
     * 获取实体
     * @param entityType  实体的类型
     * @param entityId    实体的id
     * @param offset      页数
     * @param limit       页数显示的行
     * @return
     */
    public List<Comment> findCommentsByEntity(int entityType,int entityId,int offset,int limit);

    /**
     * 获取实体的数量
     * @param entityType  实体的类型
     * @param entityId    实体的id
     * @return
     */
    public int findCommentCount(int entityType,int entityId);

    /**
     * 添加实体
     * @param comment
     * @return
     */
    public int addComment(Comment comment);

    /**
     * 根据实体的id获取实体
     * @param id
     * @return
     */
    public Comment findCommentById(int id);
}
