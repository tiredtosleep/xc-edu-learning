package com.xuecheng.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@SpringBootApplication
@EnableEurekaServer//这是一个eureka服务
public class GovernCenterApplication {
    public static void main(String[] args){
        SpringApplication.run(GovernCenterApplication.class,args);
    }
}
