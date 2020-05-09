package com.newblog.huil.controller.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newblog.huil.annotation.LoginRequired;
import com.newblog.huil.entity.vo.ResultVo;
import com.newblog.huil.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author HuilLIN
 */
@Deprecated
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {
    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            if(loginRequired != null && hostHolder.getUser() == null){
                ResultVo res = new ResultVo(2,"用户未登录");
                String json = new ObjectMapper().writeValueAsString(res);
                response.setContentType("text/html; charset=UTF-8");
                response.setCharacterEncoding("utf-8");
                response.getWriter().write(json);
                return false;
            }
        }
        return true;
    }
}
