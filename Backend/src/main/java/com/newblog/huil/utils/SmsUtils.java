package com.newblog.huil.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.newblog.huil.config.properties.MyBlogProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * @author HuilLIN
 */
@Configuration
public class SmsUtils {
    @Autowired
    private MyBlogProperties myBlogProperties;
    /**
     * 用户注册短信
     * @param testPhone
     * @param code
     * @return 返回OK则表示发送成功
     */
    public String regSendSMS(String testPhone,String code){
        //产品名称:云通信短信API产品,开发者无需替换
        String product = myBlogProperties.getSms().getProduct();
        //产品域名,开发者无需替换
        String domain = myBlogProperties.getSms().getDomain();
        // TODO 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
        String accessKeyId = myBlogProperties.getSms().getAccessKeyId();
        String accessKeySecret = myBlogProperties.getSms().getAccessKeySecret();
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        //初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile(myBlogProperties.getSms().getRegionId(), accessKeyId, accessKeySecret);
        try {
            DefaultProfile.addEndpoint(myBlogProperties.getSms().getEndpointName(), myBlogProperties.getSms().getOnsRegionId(), product, domain);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        IAcsClient acsClient = new DefaultAcsClient(profile);
        //组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        //必填:待发送手机号
        request.setPhoneNumbers(testPhone);
        //必填:短信签名-可在短信控制台中找到
        //request.setSignName("HuilLin博客");
        request.setSignName(myBlogProperties.getSms().getSignName());
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(myBlogProperties.getSms().getTemplateCode());
        request.setTemplateParam("{\"code\":\"" + code + "\"}");
        SendSmsResponse sendSmsResponse = null;
        try {
            sendSmsResponse = acsClient.getAcsResponse(request);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        /**返回OK则表示发送成功*/
        return sendSmsResponse.getCode();
    }

    public String getCode(){
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 6; i++) {
            int j = (int)(Math.random() * 10);
            sb.append(j);
        }
        return sb.toString();
    }
}
