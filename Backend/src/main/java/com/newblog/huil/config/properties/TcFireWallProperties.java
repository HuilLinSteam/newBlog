package com.newblog.huil.config.properties;

import lombok.Data;

/**
 * @author HuilLIN
 */
@Data
public class TcFireWallProperties {
    private String secretId="";
    private String secretKey="";
    private String endpoint="";
    private String region="";
    private String captchaAppId="";
    private String appSecretKey="";
    private String captchaType="";
}
