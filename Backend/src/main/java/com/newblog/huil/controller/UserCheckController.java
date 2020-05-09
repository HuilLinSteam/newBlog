package com.newblog.huil.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.newblog.huil.entity.vo.ResultVo;
import com.newblog.huil.service.UserService;
import com.newblog.huil.utils.RedisKeyUtil;
import com.newblog.huil.utils.TencentFireWallUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author HuilLIN
 */
@RequestMapping("/userCheck")
@RestController
public class UserCheckController {
    @Autowired
    private UserService userService;
    private ResultVo resultVo;
    @Autowired
    private TencentFireWallUtil tencentFireWallUtil;
    @RequestMapping(value = "/checkUserName/{username}",method = RequestMethod.POST)
    public String checkUserName(@PathVariable String username) throws JsonProcessingException {
        boolean rs = userService.checkUserName(username);
        //用户名存在
        if(rs){
            resultVo = new ResultVo(2,"用户名已存在");
        }else {
            resultVo = new ResultVo(1,"用户名可注册");
        }
        String json = new ObjectMapper().writeValueAsString(resultVo);
        return json;
    }

    @RequestMapping(value = "/checkPhone/{phone}",method = RequestMethod.POST)
    public String checkPhone(@PathVariable String phone) throws JsonProcessingException {
        boolean rs = userService.checkUserPhone(phone);
        if(rs){
            resultVo = new ResultVo(2,"手机已注册");
        }else {
            resultVo = new ResultVo(1,"手机未注册");
        }
        String json = new ObjectMapper().writeValueAsString(resultVo);
        return json;
    }

    @RequestMapping(value = "/sendCode/{phone}",method = RequestMethod.POST)
    public String sendCode(@PathVariable String phone) throws JsonProcessingException {
        //获取发送码
        String regSMS = userService.getRegSMS(phone);
        if(regSMS!=null){
            resultVo = new ResultVo(1,"短信发送成功");
        }else {
            resultVo = new ResultVo(2,"短信发送失败");
        }
        String json = new ObjectMapper().writeValueAsString(resultVo);
        return json;
    }

    @RequestMapping("/captcha")
    public String captcha(HttpServletRequest request) {
        String ticket = request.getParameter("ticket");
        String randstr = request.getParameter("randstr");
        //返回结果
        int captchaCode = tencentFireWallUtil.getCaptchaCode(ticket, randstr);
        //返回1则说明通过验证
        if(captchaCode == 1){
            resultVo = new ResultVo(1,"验证通过");
        }else {
            resultVo = new ResultVo(2,"验证失败");
        }
        String json = null;
        try {
            json = new ObjectMapper().writeValueAsString(resultVo);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }


    @RequestMapping(value = "/checkRegCode",method = RequestMethod.POST)
    public String checkRegCode(HttpServletRequest request){
        String code = request.getParameter("code");
        String phone = request.getParameter("phone");
        boolean res = userService.checkCode(code,RedisKeyUtil.getREGKey(phone));
        if(res){
            resultVo = new ResultVo(1,"验证码输入正确");
        }else {
            resultVo = new ResultVo(2,"验证码输入错误，请重新输入");
        }
        String json = null;
        try{
            json = new ObjectMapper().writeValueAsString(resultVo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }


    @RequestMapping(value = "/sendLogCode/{phone}",method = RequestMethod.POST)
    public String sendLogCode(@PathVariable String phone) throws JsonProcessingException {
        //获取发送码
        String logSMS = userService.getLogSMS(phone);
        if(logSMS!=null){
            resultVo = new ResultVo(1,"短信发送成功");
        }else {
            resultVo = new ResultVo(2,"短信发送失败");
        }
        String json = new ObjectMapper().writeValueAsString(resultVo);
        return json;
    }

    @RequestMapping(value = "/checkLogCode",method = RequestMethod.POST)
    public String checkLogCode(HttpServletRequest request){

        String code = request.getParameter("code");
        String phone = request.getParameter("phone");
        boolean res = userService.checkCode(code, RedisKeyUtil.getLOGKey(phone));
        if(res){
            resultVo = new ResultVo(1,"验证码输入正确");
        }else {
            resultVo = new ResultVo(2,"验证码输入错误，请重新输入");
        }
        String json = null;
        try{
            json = new ObjectMapper().writeValueAsString(resultVo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

}
