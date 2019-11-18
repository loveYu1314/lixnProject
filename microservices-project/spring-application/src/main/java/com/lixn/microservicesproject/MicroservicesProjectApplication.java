package com.lixn.microservicesproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@SpringBootApplication
public class MicroservicesProjectApplication {

	public static void main(String[] args) {

//      SpringApplication.run(MicroservicesProjectApplication.class, args);

		SpringApplication springApplication = new SpringApplication(MicroservicesProjectApplication.class);
		Map<String,Object> properties = new LinkedHashMap<>();
		properties.put("server.port",0);
		springApplication.setDefaultProperties(properties);
		springApplication.setWebApplicationType(WebApplicationType.NONE);//设置为非web应用
		ConfigurableApplicationContext context = springApplication.run(args);
		System.out.println(context.getBean(MicroservicesProjectApplication.class));
		// 输出当前 Spring Boot 应用的 ApplicationContext 的类名
		System.out.println("当前spring boot应用上下文的类：" + context.getClass().getName());


//		new SpringApplicationBuilder(MicroservicesProjectApplication.class)//Fluent API
//				.properties("server.port=0")//随机向OS要一个可用的端口
//				.run();


//		SpringApplication springApplication = new SpringApplication(MicroservicesProjectApplication.class);
//		Map<String,Object> properties = new LinkedHashMap<>();
//		properties.put("server.port",0);
//		springApplication.setDefaultProperties(properties);
//		ConfigurableApplicationContext context = springApplication.run(args);
//		System.out.println(context.getBean(MicroservicesProjectApplication.class));
	}

}
