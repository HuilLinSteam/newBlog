package com.newblog.huil.user;

import com.newblog.huil.HuilApplication;
import com.newblog.huil.config.properties.MyBlogProperties;
import com.newblog.huil.utils.SmsUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = HuilApplication.class)
public class TestSmsUtils {
    @Autowired
    SmsUtils smsUtils;
    @Test
    public void testSms(){
        String code = smsUtils.getCode();
        String s = smsUtils.regSendSMS("18127963844", code);
        System.out.println("验证码为:"+s);
//        System.out.println(myBlogProperties.getSms().getProduct());
//        System.out.println(myBlogProperties.getSms().getDomain());
//        System.out.println(myBlogProperties.getSms().getAccessKeyId());
//        System.out.println(myBlogProperties.getSms().getAccessKeySecret());
//        System.out.println(myBlogProperties.getSms().getRegionId());
//        System.out.println(myBlogProperties.getSms().getOnsRegionId());
//        System.out.println(myBlogProperties.getSms().getEndpointName());
//        System.out.println(myBlogProperties.getSms().getTemplateCode());
//        System.out.println(myBlogProperties.getSms().getSignName());
    }
}
