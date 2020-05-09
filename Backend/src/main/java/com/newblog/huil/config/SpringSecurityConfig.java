package com.newblog.huil.config;

import com.newblog.huil.controller.interceptor.LoginTicketInterceptor;
import com.newblog.huil.utils.JsonUtil;
import com.newblog.huil.utils.NewBlogConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author HuilLIN
 */
@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    LoginTicketInterceptor loginTicketInterceptor;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 授权
        http.authorizeRequests()
                .antMatchers(
                        "user/setting",
                        "/user/upload",
                        "/blog/add",
                        "/comment/add/**",
                        "/letter/**",
                        "/notice/**",
                        "/like",
                        "/follow",
                        "/unFollow"
                )
                .hasAnyAuthority(
                        NewBlogConstant.AUTHORITY_USER,
                        NewBlogConstant.AUTHORITY_ADMIN,
                        NewBlogConstant.AUTHORITY_MODERATOR
                )
                .antMatchers(
                        "/blog/top",
                        "/blog/wonderful"
                )
                .hasAnyAuthority(
                        NewBlogConstant.AUTHORITY_MODERATOR
                )
                .antMatchers(
                        "/blog/delete",
                        "/data/**",
                        "/actuator/**"
                )
                .hasAnyAuthority(
                        NewBlogConstant.AUTHORITY_ADMIN
                )
                .anyRequest().permitAll()
                .and().csrf().disable();
        // 权限不够时的处理
        http.exceptionHandling()
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
                        response.setContentType("application/plain;charset=utf-8");
                        PrintWriter writer = response.getWriter();
                        writer.write(JsonUtil.getJSONString(403, "你还没有登录哦!"));
                    }
                })
                .accessDeniedHandler(new AccessDeniedHandler() {
                    //权限不足
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
                        response.setContentType("application/plain;charset=utf-8");
                        PrintWriter writer = response.getWriter();
                        writer.write(JsonUtil.getJSONString(403, "你没有访问此功能的权限!"));
                    }
                });
        // Security底层默认会拦截/logout请求,进行退出处理.
        // 覆盖它默认的逻辑,才能执行我们自己的退出代码.
        // 因为它默认的退出就是logout与当前项目一样，如果路径不修改那么退出时将无法执行我们的退出逻辑。
        // "/securitylogout"是不存在的
        http.logout().logoutUrl("/securitylogout");
    }
}
