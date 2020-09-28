package com.lhfeiyu.tech;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication //(exclude = {RedisAutoConfiguration.class}) // , RedisRepositoriesAutoConfiguration.class
@MapperScan(value = {"com.zom.statistics.dao.mapper.common", "com.zom.statistics.dao.mapper.logMapper"})
//@ServletComponentScan(basePackages = "com.zom.statistics.filter") // reg by config/WebConfig
@EnableTransactionManagement
@EnableScheduling
@EnableCaching
public class TechPlaygroundWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(TechPlaygroundWebApplication.class, args);
    }

}
