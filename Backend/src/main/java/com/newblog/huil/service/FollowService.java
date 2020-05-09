package com.newblog.huil.service;

import java.util.List;
import java.util.Map;

/**
 * @author HuilLIN
 */
public interface FollowService {
    /**
     * 关注某个实体
     * @param userId
     * @param entityType
     * @param entityId
     */
    public void follow(int userId,int entityType,int entityId);

    /**
     * 对某个实体取关
     * @param userId
     * @param entityType
     * @param entityId
     */
    public void unFollow(int userId,int entityType,int entityId);

    /**
     * 查询关注的实体的数量
     * @param userId
     * @param entityType
     * @return
     */
    public long findFolloweeCount(int userId,int entityType);

    /**
     * 查询实体的粉丝的数量
     * @param entityType
     * @param entityId
     * @return
     */
    public long findFollowerCount(int entityType,int entityId);

    /**
     * 查询当前用户是否已关注该实体
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public boolean hasFollowed(int userId,int entityType,int entityId);

    /**
     * 查询某用户关注的人
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    public List<Map<String, Object>> findFollowees(int userId,int offset,int limit);

    /**
     * 查询某个用户的粉丝
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    public List<Map<String, Object>> findFollowers(int userId, int offset, int limit);
}
