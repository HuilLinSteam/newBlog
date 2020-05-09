package com.newblog.huil.controller;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newblog.huil.entity.Message;
import com.newblog.huil.entity.Page;
import com.newblog.huil.entity.User;
import com.newblog.huil.entity.vo.ResultVo;
import com.newblog.huil.service.MessageService;
import com.newblog.huil.service.UserService;
import com.newblog.huil.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

/**
 * @author HuilLIN
 */
@RestController
public class MessageController {
    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    ResultVo resultVo = null;

    @RequestMapping(path = "/letter/list", method = RequestMethod.GET)
    public String getLetterList(Page page) throws JsonProcessingException {
        User user = hostHolder.getUser();
        if (user == null) {
            resultVo = new ResultVo(2, "请登录哦！");
        } else {
            Map<String, Object> resMap = new HashMap<>();
            //分页信息
            page.setLimit(5);
            page.setPath("/letter/list");
            page.setRows(messageService.findConversationCount(user.getId()));

            //会话列表
            List<Message> conversationList = messageService.findConversations(
                    user.getId(), page.getOffset(), page.getLimit());
            List<Map<String, Object>> conversations = new ArrayList<>();
            if (conversationList != null) {
                for (Message message : conversationList) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("conversation", message);
                    map.put("letterCount", messageService.findLetterCount(message.getConversationId()));
                    map.put("unreadCount", messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));
                    int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                    User targetUser = userService.findByUserId(targetId);
                    targetUser.setUid("");
                    targetUser.setPassword("");
                    targetUser.setSalt("");
                    map.put("target", targetUser);
                    conversations.add(map);
                }
            }
            resMap.put("conversations", conversations);
            int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
            resMap.put("letterUnreadCount", letterUnreadCount);
            int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
            resMap.put("noticeUnreadCount", noticeUnreadCount);
            resMap.put("page", page);
            resultVo = new ResultVo(1, resMap, "获取私信成功！");
        }
        String jsonString = new ObjectMapper().writeValueAsString(resultVo);
        return jsonString;
    }

    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Page page) throws JsonProcessingException {
        User user = hostHolder.getUser();
        // 得到对话的id
        String[] userIds = conversationId.split("_");
        if (user == null) {
            resultVo = new ResultVo(2, "请登录哦！");
        }
//        else if(user.getId() != Integer.parseInt(userIds[0]) || user.getId() != Integer.parseInt(userIds[1]) ){
//            resultVo = new ResultVo(2,"非对话用户，禁止访问哦！");
//        }
        else if(user.getId() == Integer.parseInt(userIds[0]) || user.getId() == Integer.parseInt(userIds[1]) ){
            Map<String, Object> resMap = new HashMap<>();
            //分页信息
            page.setLimit(5);
            page.setPath("/letter/detail/" + conversationId);
            page.setRows(messageService.findLetterCount(conversationId));

            //私信列表
            List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
            List<Map<String, Object>> letters = new ArrayList<>();
            if (letterList != null) {
                for (Message message : letterList) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("letter", message);
                    map.put("fromUser", userService.findByUserId(message.getFromId()));
                    letters.add(map);
                }
            }
            resMap.put("letters", letters);
            //私信目标
            resMap.put("target", getLetterTarget(conversationId));
            resMap.put("page", page);
            //私信已读
            List<Integer> ids = getLetterIds(letterList);
            if (!ids.isEmpty()) {
                messageService.readMessage(ids);
            }
            resultVo = new ResultVo(1, resMap, "获取会话详情成功！");

        }
        else{
            resultVo = new ResultVo(2,"非对话用户，禁止访问哦！");
        }
        String jsonString = new ObjectMapper().writeValueAsString(resultVo);
        return jsonString;
    }

    private List<Integer> getLetterIds(List<Message> letterList) {
        List<Integer> ids = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                if (hostHolder.getUser().getId() == message.getToId() && message.getStatus() == 0) {
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }

    private User getLetterTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);
        if (hostHolder.getUser().getId() == id0) {
            User id1User = CheckUserUtil.getUser(userService.findByUserId(id1));
            return id1User;
        } else {
            User id0User = CheckUserUtil.getUser(userService.findByUserId(id0));
            return id0User;
        }
    }

    @RequestMapping(path = "/letter/send", method = RequestMethod.POST)
    public String sendLetter(String toName, String content) {
        if (CheckUserUtil.UserIsNull(hostHolder.getUser())) {
            return JsonUtil.getJSONString(2, "请登录后操作哦！");
        } else {
            User user = userService.findUserByUsernameOrPhone(toName);
            if (user == null) {
                return JsonUtil.getJSONString(2, "目标用户不存在");
            }
            User filterUserInfo = CheckUserUtil.getUser(user);
            Message message = new Message();
            message.setFromId(hostHolder.getUser().getId());
            message.setToId(filterUserInfo.getId());
            if (message.getFromId() < message.getToId()) {
                message.setConversationId(message.getFromId() + "_" + message.getToId());
            } else {
                message.setConversationId(message.getToId() + "_" + message.getFromId());
            }
            message.setContent(sensitiveFilter.filter(content));
            message.setCreateTime(new Date());
            messageService.addMessage(message);
        }
        return JsonUtil.getJSONString(1, "发送成功了哦！");
    }

    @RequestMapping(path = "/notice/list", method = RequestMethod.GET)
    public String getNoticeList() throws JsonProcessingException {
        User loginUser = hostHolder.getUser();
        Map<String, Object> resMap = new HashMap<>();
        // 查询评论类通知
        Message message = messageService.findLatestNotice(loginUser.getId(), NewBlogConstant.TOPIC_COMMENT);
        if (message != null) {
            Map<String, Object> messageVO = new HashMap<>();
            messageVO.put("message", message);
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
            User tempUser = userService.findByUserId((Integer) data.get("userId"));
            User user = CheckUserUtil.getUser(tempUser);
            messageVO.put("user", user);
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            messageVO.put("blogId", data.get("blogId"));

            int count = messageService.findNoticeCount(loginUser.getId(), NewBlogConstant.TOPIC_COMMENT);
            messageVO.put("count", count);

            int unread = messageService.findNoticeUnreadCount(loginUser.getId(), NewBlogConstant.TOPIC_COMMENT);
            messageVO.put("unread", unread);
            resMap.put("commentNotice", messageVO);
        }

        // 查询点赞类通知
        message = messageService.findLatestNotice(loginUser.getId(), NewBlogConstant.TOPIC_LIKE);
        if (message != null) {

            Map<String, Object> messageVO = new HashMap<>();
            messageVO.put("message", message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
            User tempUser = userService.findByUserId((Integer) data.get("userId"));
            User user = CheckUserUtil.getUser(tempUser);
            messageVO.put("user", user);
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            messageVO.put("blogId", data.get("blogId"));

            int count = messageService.findNoticeCount(loginUser.getId(), NewBlogConstant.TOPIC_LIKE);
            messageVO.put("count", count);

            int unread = messageService.findNoticeUnreadCount(loginUser.getId(), NewBlogConstant.TOPIC_LIKE);
            messageVO.put("unread", unread);
            resMap.put("likeNotice", messageVO);
        }

        // 查询关注类通知
        message = messageService.findLatestNotice(loginUser.getId(), NewBlogConstant.TOPIC_FOLLOW);
        if (message != null) {
            Map<String, Object> messageVO = new HashMap<>();
            messageVO.put("message", message);
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
            User tempUser = userService.findByUserId((Integer) data.get("userId"));
            User user = CheckUserUtil.getUser(tempUser);
            messageVO.put("user", user);
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));

            int count = messageService.findNoticeCount(loginUser.getId(), NewBlogConstant.TOPIC_FOLLOW);
            messageVO.put("count", count);

            int unread = messageService.findNoticeUnreadCount(loginUser.getId(), NewBlogConstant.TOPIC_FOLLOW);
            messageVO.put("unread", unread);
            resMap.put("followNotice", messageVO);
        }

        // 私信未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(loginUser.getId(), null);
        resMap.put("letterUnreadCount", letterUnreadCount);
        // 系统未读消息数量
        int noticeUnreadCount = messageService.findNoticeUnreadCount(loginUser.getId(), null);
        resMap.put("noticeUnreadCount", noticeUnreadCount);

        resultVo = new ResultVo(1, resMap, "获取未读消息成功!");
        String jsonString = new ObjectMapper().writeValueAsString(resultVo);
        return jsonString;

    }

    @RequestMapping(path = "/notice/detail/{topic}", method = RequestMethod.GET)
    public String getNoticeDetail(@PathVariable("topic") String topic, Page page) throws JsonProcessingException {
        User user = hostHolder.getUser();
        page.setLimit(5);
        page.setPath("/notice/detail/" + topic);
        page.setRows(messageService.findNoticeCount(user.getId(), topic));

        List<Message> noticeList = messageService.findNotices(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String, Object>> noticeVoList = new ArrayList<>();
        Map<String, Object> resMap = new HashMap<>();
        if (noticeList != null) {
            for (Message notice : noticeList) {
                Map<String, Object> map = new HashMap<>();
                // 通知
                map.put("notice", notice);
                // 内容
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                HashMap<String, Object> data = JSONObject.parseObject(content, HashMap.class);
                User tempUser = userService.findByUserId((Integer) data.get("userId"));
                map.put("user", CheckUserUtil.getUser(tempUser));
                map.put("entityType", data.get("entityType"));
                map.put("entityId", data.get("entityId"));
                map.put("blogId", data.get("blogId"));
                // 通知作者
                User authorUser = userService.findByUserId(notice.getFromId());
                map.put("fromUser",CheckUserUtil.getUser(authorUser));
                noticeVoList.add(map);
            }
        }
        resMap.put("notices",noticeVoList);
        resMap.put("page",page);
        // 设置已读
        List<Integer> ids = getLetterIds(noticeList);
        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }
        resultVo = new ResultVo(1,resMap,"获取通知详情页成功");
        String jsonString = new ObjectMapper().writeValueAsString(resultVo);
        return jsonString;
    }

}

