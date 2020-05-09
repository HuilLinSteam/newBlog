package com.newblog.huil.controller.interceptor;

import com.newblog.huil.entity.LoginTicket;
import com.newblog.huil.entity.User;
import com.newblog.huil.service.UserService;
import com.newblog.huil.utils.CookieUtil;
import com.newblog.huil.utils.HostHolder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @author HuilLIN
 */
@Configuration
public class LoginTicketInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(LoginTicketInterceptor.class);

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 在Controller之前执行
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        //从cookie中获取登录凭证
        String ticket = CookieUtil.getValue(request, "loginTicket");
        if(ticket != null){
            //查询凭证
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            //查询凭证是否有效
            if(loginTicket != null && loginTicket.getStatus() == 0 &&
                    loginTicket.getExpired().after(new Date())){
                //根据凭证查询用户
                User user = userService.findByUserId(loginTicket.getUserId());
                //在本次请求中持有用户
                hostHolder.setUser(user);
                // SecurityContext存储的是认证的结果
                // 现在开始构建用户认证的结果，并存入SecurityContext,以便获取Security授权
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        user,user.getPassword(),userService.getAuthorities(user.getId())
                );
                // 存放SecurityContext
                SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
            }
        }
        return true;
    }

    /**
     * 在Controller之后执行
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        logger.info("loginUser的信息:"+user);
    }

    /**
     * 在TemplateEngine之后执行
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
        // 清理认证结果
        SecurityContextHolder.clearContext();
    }
}
