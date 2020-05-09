package com.newblog.huil.service;

/**
 * @author HuilLIN
 */
public interface LikeService {

    /**
     * 点赞
     * @param userId
     * @param entityType
     * @param entityId
     * @param entityUserId
     */
    public void like(int userId, int entityType, int entityId,int entityUserId);

    /**
     * 查询某实体的点赞数量
     * @param entityType
     * @param entityId
     * @return
     */
    public long findEntityLikeCount(int entityType, int entityId);

    /**
     * 查询某个用户对某个实体的点赞状态
     * @param userId
     * @param entityType
     * @param entityId
     * @return 返回1则表示点过赞，返回0则表示未点过赞，还可返回-1表示另一种状态
     */
    public int findEntityLikeStatus(int userId, int entityType, int entityId);

    /**
     * 查询某个用户获得的赞
     * @param userId
     * @return
     */
    public int findUserLikeCount(int userId);
}
