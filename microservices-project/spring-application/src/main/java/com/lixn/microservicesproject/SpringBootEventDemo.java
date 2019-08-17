package com.lixn.microservicesproject;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @创建人 lixiangnan
 * @创建时间 2019/8/10
 * @描述
 */
@EnableAutoConfiguration
public class SpringBootEventDemo {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SpringBootEventDemo.class)
                .listeners(event -> {
                    System.err.println("监听到的事件：" + event.getClass().getSimpleName());
                })
                .run(args)
                .close();
    }
}
