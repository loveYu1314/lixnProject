package com.lixn.spring.cloud.service.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @创建人 lixiangnan
 * @创建时间 2019/9/15
 * @描述
 */
@SpringBootApplication
@EnableDiscoveryClient //尽可能使用EnableDiscoveryClient
public class ZkDSClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZkDSClientApplication.class,args);
    }
}
