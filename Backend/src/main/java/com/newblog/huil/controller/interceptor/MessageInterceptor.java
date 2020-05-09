package com.newblog.huil.controller.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newblog.huil.entity.User;
import com.newblog.huil.entity.vo.ResultVo;
import com.newblog.huil.service.MessageService;
import com.newblog.huil.utils.HostHolder;
import com.newblog.huil.utils.JsonUtil;
import com.newblog.huil.utils.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.jws.Oneway;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author HuilLIN
 */
@Component
public class MessageInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(MessageInterceptor.class);
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private MessageService messageService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        User user = hostHolder.getUser();
        if (user != null) {
            int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
            int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
            ///String unReadCountKey = RedisKeyUtil.getUnReadCountKey(user.getId());
            ///redisTemplate.opsForValue().set(unReadCountKey,letterUnreadCount+noticeUnreadCount);
            request.getSession().setAttribute("allUnreadCount",letterUnreadCount+noticeUnreadCount);
        }
        return true;
    }

}
