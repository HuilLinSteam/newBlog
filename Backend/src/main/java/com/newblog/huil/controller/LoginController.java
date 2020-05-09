package com.newblog.huil.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newblog.huil.entity.User;
import com.newblog.huil.entity.vo.ResultVo;
import com.newblog.huil.service.UserService;
import com.newblog.huil.utils.NewBlogConstant;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Set;

/**
 * @author HuilLIN
 */
@RestController
public class LoginController {
    @Value("${server.servlet.context-path}")
    private String contextPath;
    private ResultVo resultVo;
    @Autowired
    private UserService userService;
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, String[]> map = request.getParameterMap();
        User user = new User();
        BeanUtils.populate(user, map);
        //可以扩展“记住我”的功能,true为记住，false为不记住
        boolean rememberme = Boolean.parseBoolean(request.getParameter("rememberme"));
        int expiredSeconds = rememberme ? NewBlogConstant.REMEMBER_EXPIRED_SECONDS : NewBlogConstant.DEFAULT_EXPIRED_SECONDS;
        //短信登录的
        if (user.getPassword() == null || user.getPassword().equals("")) {

            Map<String, Object> login = userService.login(user.getPhone(), null, expiredSeconds);
            Object ticket = login.get("ticket");
            if (ticket == null) {
                Object res = null;
                Set<String> strings = login.keySet();
                for (String key : strings) {
                    res = login.get(key);
                }
                resultVo = new ResultVo(2, res.toString());
            } else {
                Cookie loginCookie = new Cookie("loginTicket", ticket.toString());
                loginCookie.setPath(contextPath);
                loginCookie.setMaxAge(expiredSeconds);
                response.addCookie(loginCookie);
                resultVo = new ResultVo(1, "用户登录成功");
            }
        }
        //说明是账号密码登录的
        else {
            Map<String, Object> login = userService.login(user.getUsername(), user.getPassword(), expiredSeconds);
            Object ticket = login.get("ticket");
            if (ticket == null) {
                Object res = null;
                Set<String> strings = login.keySet();
                for (String key : strings) {
                    res = login.get(key);
                }
                resultVo = new ResultVo(2, res.toString());
            } else {
                Cookie loginCookie = new Cookie("loginTicket", ticket.toString());
                loginCookie.setPath(contextPath);
                loginCookie.setMaxAge(expiredSeconds);
                response.addCookie(loginCookie);
                resultVo = new ResultVo(1, "用户登录成功");
            }
        }
        String json = new ObjectMapper().writeValueAsString(resultVo);
        return json;
    }
}
