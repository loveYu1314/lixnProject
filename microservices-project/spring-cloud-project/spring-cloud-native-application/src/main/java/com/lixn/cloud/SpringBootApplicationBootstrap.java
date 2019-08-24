package com.lixn.cloud;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.RestController;

/**
 * @创建人 lixiangnan
 * @创建时间 2019/8/23
 * @描述
 */
@EnableAutoConfiguration
@RestController
public class SpringBootApplicationBootstrap {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext parentContext = new AnnotationConfigApplicationContext();
        parentContext.setId("lixn");
        // 在"lixn" 上下文注册一个 "helloWorld" String 类型的 Bean
        parentContext.registerBean("helloWorld",String.class,"Hello,World");
        // 启动"lixn" 上下文
        parentContext.refresh();

        // 类比于 Spring WebMVC，Root WebApplication 和 DispatcherServlet WebApplication
        // DispatcherServlet WebApplication parent = Root WebApplication
        // DispatcherServlet Servlet
        // Filter -> Root WebApplication

        new SpringApplicationBuilder(SpringBootApplicationBootstrap.class)
                .parent(parentContext)//显示设置双亲上下文
                .run(args);
    }
}
