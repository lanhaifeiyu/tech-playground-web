package com.lhfeiyu.tech.config;

import com.lhfeiyu.tech.handler.JwtInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebJwtConfig implements WebMvcConfigurer {

    /**
     * 添加拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //拦截路径可自行配置多个 可用 ，分隔开
        /*registry.addInterceptor(new JwtInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/api/v1/logon")
                .excludePathPatterns("/api/v1/dept/exportExcel")
                .excludePathPatterns("/w/**");*/

        registry.addInterceptor(new JwtInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/v1/logon")
                .excludePathPatterns("/api/v1/dept/exportExcel");
    }

}
