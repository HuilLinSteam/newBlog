package com.newblog.huil.controller.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newblog.huil.entity.User;
import com.newblog.huil.entity.vo.ResultVo;
import com.newblog.huil.utils.HostHolder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author HuilLIN
 */
@Slf4j
@Component
public class UserNotLoginInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(UserNotLoginInterceptor.class);
    @Autowired
    private HostHolder hostHolder;

    /**
     * Controller执行前的拦截
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        User tempUser = hostHolder.getUser();
        if(tempUser == null){
            ResultVo res = new ResultVo(403,"用户未登录");
            String json = new ObjectMapper().writeValueAsString(res);
            response.setContentType("text/html; charset=UTF-8");
            response.setCharacterEncoding("utf-8");
            response.getWriter().write(json);
            return false;
        }
        return true;
    }
}
