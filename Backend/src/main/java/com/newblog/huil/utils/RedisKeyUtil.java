package com.newblog.huil.utils;

/**
 * @author HuilLIN
 */
public class RedisKeyUtil {
    private static final String SPLIT = ":";
    /**
     * 注册发送的key
     */
    private static final String SUFFIX_REG = "reg";
    /**
     * 登录时发送的key
     */
    private static final String SUFFIX_LOG = "log";
    /**
     * 某个实体的赞前缀
     */
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    /**
     * 用户登录的凭证前缀
     */
    private static final String PREFIX_TICKET = "ticket";
    /**
     * 用户登录的redis前缀
     */
    private static final String PREFIX_USER = "user";

    /**
     * 查看某个博客
     */
    private static final String PREFIX_ENJOY_BLOG = "enjoy:blog";


    /**
     * 某个用户的赞前缀
     */
    private static final String PREFIX_USER_LIKE = "like:user";

    /**
     * 关注的目标
     */
    private static String PREFIX_FOLLOWEE = "followee";

    /**
     * 粉丝
     */
    private static String PREFIX_FOLLOWER = "follower";

    private static final String PREFIX_POST = "post";

    /**
     * 系统消息未读的前缀
     */
    private static final String PREFIX_UNREAD_COUNT = "unread";

    private static final String PREFIX_UV = "uv";
    private static final String PREFIX_DAU = "dau";

    /**
     * 某个实体的赞key值
     *
     * @param entityType
     * @param entityId
     * @return
     */
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 获取注册时短信的key
     *
     * @param phone
     * @return
     */
    public static String getREGKey(String phone) {
        return phone + SPLIT + SUFFIX_REG;
    }

    /**
     * 获取登录时短信的key
     *
     * @param phone
     * @return
     */
    public static String getLOGKey(String phone) {
        return phone + SPLIT + SUFFIX_LOG;
    }

    /**
     * 登录的凭证
     *
     * @param ticket
     * @return
     */
    public static String getTicketKey(String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }

    /**
     * 获取用户
     *
     * @param userId
     * @return
     */
    public static String getUserKey(int userId) {
        return PREFIX_USER + SPLIT + userId;
    }

    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    /**
     * 获取阅读博客的key
     *
     * @param blogId
     * @return
     */
    public static String getEnjoyBlogKey(int blogId) {
        return PREFIX_ENJOY_BLOG + SPLIT + blogId;
    }


    /**
     * 某个用户关注的实体
     * followee:userId:entityType -> zset(entityId,now)
     *
     * @param userId
     * @param entityType
     * @return
     */
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    /**
     * 某个实体拥有的粉丝
     * follower:entityType:entityId -> zset(userId,now)
     *
     * @param entityType
     * @param entityId
     * @return
     */
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 博客分数
     *
     * @return
     */
    public static String getBlogScoreKey() {
        return PREFIX_POST + SPLIT + "score";
    }

    /**
     * 获取用户未读系统消息的数量
     *
     * @param userId
     * @return
     */
    public static String getUnReadCountKey(int userId) {
        return PREFIX_UNREAD_COUNT + SPLIT + userId;
    }

    /**
     * 单日UV
     *
     * @param date
     * @return
     */
    public static String getUVKey(String date) {
        return PREFIX_UV + SPLIT + date;
    }

    /**
     * 区间UV
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static String getUVKey(String startDate, String endDate) {
        return PREFIX_UV + SPLIT + startDate + SPLIT + endDate;
    }

    /**
     * 单日活跃用户
     *
     * @param date
     * @return
     */
    public static String getDAUKey(String date) {
        return PREFIX_DAU + SPLIT + date;
    }

    /**
     * 区间活跃用户
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static String getDAUKey(String startDate, String endDate) {
        return PREFIX_DAU + SPLIT + startDate + SPLIT + endDate;
    }
}