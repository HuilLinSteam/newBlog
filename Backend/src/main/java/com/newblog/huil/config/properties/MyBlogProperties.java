package com.newblog.huil.config.properties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author HuilLIN
 * 在application.properties中所有前缀为blog.global的配置都会被加载
 */
@ConfigurationProperties(prefix = "blog.global")
@Data
public class MyBlogProperties {
    private SmsProperties sms = new SmsProperties();
    private TcFireWallProperties tc = new TcFireWallProperties();
}
