package com.lixn.spring;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 * @创建人 lixiangnan
 * @创建时间 2019/7/30
 * @描述
 */
@Configuration
public class SpringAnnotationDemo {

    public static void main(String[] args) {
        // ClassPathXmlApplicationContext
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(SpringAnnotationDemo.class);
        //上下文启动
        context.refresh();
        System.out.println(context.getBean(SpringAnnotationDemo.class));
    }
}
