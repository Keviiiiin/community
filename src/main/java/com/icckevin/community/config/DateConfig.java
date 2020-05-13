package com.icckevin.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

/**
 * @description: 日期格式转换
 * @author: iccKevin
 * @create: 2020-05-13 21:17
 **/
@Component
public class DateConfig {
    @Bean
    public SimpleDateFormat simpleDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
}