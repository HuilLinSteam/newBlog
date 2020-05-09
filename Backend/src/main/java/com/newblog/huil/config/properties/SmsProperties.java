package com.newblog.huil.config.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * @author HuilLIN
 */
@Getter
@Setter
public class SmsProperties {
    /**注意这里的product和blog.global.sms.product中的loginPage必须名字一致，否则会出错*/
    /**默认值为无*/
    /**#产品名称:云通信短信API产品,开发者无需替换*/
    private String product="";
    /**#产品域名,开发者无需替换*/
    private String domain="";
    /**#开发者自己的AK*/
    private String accessKeyId="";
    private String accessKeySecret="";
    private String regionId="";
    private String onsRegionId="";
    private String endpointName="";
    /**#短信签名-可在短信控制台中找到*/
    private String signName="HuilLin博客";
    /**#短信模板-可在短信控制台中找到*/
    private String templateCode="";
}
