package com.newblog.huil.config;

import com.newblog.huil.config.properties.MyBlogProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author HuilLIN
 */
@Configuration
@EnableConfigurationProperties(MyBlogProperties.class)
public class MyBlogPropertiesConfig {
    /**MyBlogProperties.class的配置生效，MyBlogProperties.class也不会报错*/
}
