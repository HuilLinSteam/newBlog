package com.newblog.huil.service;

import com.newblog.huil.entity.Message;

import java.util.List;

/**
 * @author HuilLIN
 */
public interface MessageService {
    /**
     * 查询当前用户的会话列表,针对每个会话只返回一条最新的私信.
     * @param userId 当前用户的id
     * @param offset
     * @param limit
     * @return
     */
    public List<Message> findConversations(int userId, int offset, int limit);

    /**
     * 查询当前用户的会话数量.
     * @param userId
     * @return
     */
    public int findConversationCount(int userId);

    /**
     * 查询某个会话所包含的私信列表.
     * @param conversationId
     * @param offset
     * @param limit
     * @return
     */
    public List<Message> findLetters(String conversationId, int offset, int limit);

    /**
     * 查询某个会话所包含的私信数量.
     * @param conversationId
     * @return
     */
    public int findLetterCount(String conversationId);

    /**
     * 查询未读私信的数量
     * @param userId
     * @param conversationId
     * @return
     */
    public int findLetterUnreadCount(int userId, String conversationId);

    /**
     * 新增消息
     * @param message
     * @return
     */
    public int addMessage(Message message);

    /**
     * 查看私信后修改消息的状态
     * @param ids
     * @return
     */
    public int readMessage(List<Integer> ids);

    /**
     * 查询某个主题下最新的通知
     * @param userId
     * @param topic
     * @return
     */
    public Message findLatestNotice(int userId, String topic);

    /**
     * 查询某个主题所包含的通知数量
     * @param userId
     * @param topic
     * @return
     */
    public int findNoticeCount(int userId, String topic);

    /**
     * 查询未读的通知的数量
     * @param userId
     * @param topic
     * @return
     */
    public int findNoticeUnreadCount(int userId, String topic);

    /**
     * 用户查询某个主题所包含的通知列表
     * @param userId
     * @param topic
     * @param offset
     * @param limit
     * @return
     */
    public List<Message> findNotices(int userId, String topic, int offset, int limit);
}
