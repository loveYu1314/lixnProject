package com.lixn.microservicesproject;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 * @创建人 lixiangnan
 * @创建时间 2019/10/27
 * @描述
 */
@Configuration
public class SpringAnnotationDemo {

  public static void main(String[] args) {

      // XML配置文件驱动   ClassPathXmlApplicationContext
      // Annotation驱动
      // 找BeanDefinition     // @Bean  @Configuration
      AnnotationConfigApplicationContext context  = new AnnotationConfigApplicationContext();
      //注册一个 Configuration Class = SpringAnnotationDemo
      context.register(SpringAnnotationDemo.class);
      context.refresh();
    System.out.println(context.getBean(SpringAnnotationDemo.class));
  }
}
