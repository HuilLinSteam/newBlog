package com.newblog.huil.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.newblog.huil.config.properties.MyBlogProperties;
import com.tencentcloudapi.captcha.v20190722.CaptchaClient;
import com.tencentcloudapi.captcha.v20190722.models.DescribeCaptchaResultRequest;
import com.tencentcloudapi.captcha.v20190722.models.DescribeCaptchaResultResponse;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author HuilLIN
 */
@Configuration
public class TencentFireWallUtil {
    @Autowired
    MyBlogProperties myBlogProperties;
    Integer captchaCode=0;
    public int getCaptchaCode(String ticket, String randstr){
        Credential cred = new Credential(myBlogProperties.getTc().getSecretId(),myBlogProperties.getTc().getSecretKey());
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint(myBlogProperties.getTc().getEndpoint());
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        CaptchaClient client = new CaptchaClient(cred,myBlogProperties.getTc().getRegion(),clientProfile);
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String ip = request.getRemoteHost();
        String params =
                "{" +
                        "\"CaptchaType\":"+myBlogProperties.getTc().getCaptchaType()+"," +
                        "\"Ticket\":"+ticket+"," +
                        "\"UserIp\":"+ip+"," +
                        "\"Randstr\":"+randstr+"," +
                        "'CaptchaAppId':"+myBlogProperties.getTc().getCaptchaAppId()+"," +
                        "\"AppSecretKey\":"+myBlogProperties.getTc().getAppSecretKey()+
                        "}";

        DescribeCaptchaResultRequest req = DescribeCaptchaResultRequest.fromJsonString(params, DescribeCaptchaResultRequest.class);
        DescribeCaptchaResultResponse resp = null;
        try {
            resp = client.DescribeCaptchaResult(req);
            //腾讯响应回来的字符串内容
            String json = DescribeCaptchaResultRequest.toJsonString(resp);
            //将响应的内容转换成json
            JSONObject jsonObject = JSON.parseObject(json);
            //通过对象取值
            captchaCode = jsonObject.getInteger("CaptchaCode");
        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
        }
        return captchaCode;
    }
}
