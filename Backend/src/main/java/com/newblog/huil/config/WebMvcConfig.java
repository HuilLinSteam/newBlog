package com.newblog.huil.config;

import com.newblog.huil.controller.interceptor.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author HuilLIN
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    @Autowired
    private UserNotLoginInterceptor userNotLoginInterceptor;

    @Autowired
    private MessageInterceptor messageInterceptor;

    @Autowired
    private DataInterceptor dataInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 拦截所有的请求
        registry.addInterceptor(loginTicketInterceptor);
        registry.addInterceptor(userNotLoginInterceptor).addPathPatterns("/user/myUserInfo","/follow/**","/unFollow/**");
        registry.addInterceptor(messageInterceptor);
        registry.addInterceptor(dataInterceptor);

    }


}
