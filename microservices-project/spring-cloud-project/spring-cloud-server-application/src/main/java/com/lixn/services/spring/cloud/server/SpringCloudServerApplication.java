package com.lixn.services.spring.cloud.server;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

/**
 * @创建人 lixiangnan
 * @创建时间 2019/9/22
 * @描述
 */
@SpringBootApplication
@EnableDiscoveryClient // 激活服务发现客户端
//@EnableHystrix // 激活 Hystrix
public class SpringCloudServerApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SpringCloudServerApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }
}
