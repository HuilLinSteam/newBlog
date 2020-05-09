package com.newblog.huil.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author HuilLIN
 */
@Configuration
public class GlobalCorsConfig {
    @Value("${allow.url}")
    private String allowURL;
    @Bean
    public CorsFilter corsFilter(){
        CorsConfiguration configuration = new CorsConfiguration();
        //表示允许http://www.mayunse.com:8084这个域名，跨域访问后端资源
        configuration.addAllowedOrigin(allowURL);
        //AJAX访问允许客户端保存cookie
        configuration.setAllowCredentials(true);
        //允许所有的方法请求跨域:"GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS"
        configuration.addAllowedMethod("*");
        //允许增加所有的请求头:"Accept", "Origin", "X-Requested-With", "Content-Type",
        //                        "Last-Modified", "device", "token"
        configuration.addAllowedHeader("*");
        //允许暴露给前端的请求头
        configuration.addExposedHeader("Set-Cookie");
        //configuration.addExposedHeader(HttpHeaders.SET_COOKIE);
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**",configuration);
        return new CorsFilter(configSource);
    }
}
