package com.lhfeiyu.tech.config;

import com.lhfeiyu.tech.filter.CorsFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * https://www.javazhiyin.com/45056.html
 */
@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean corsFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean(new CorsFilter());
        registration.addUrlPatterns("/*");
        registration.setName("corsFilter");
        return registration;
    }

}
