package com.lixn.mvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @创建人 lixiangnan
 * @创建时间 2019/10/27
 * @描述
 */
@SpringBootApplication
public class MvcViewApplication {

  public static void main(String[] args) {
      SpringApplication springApplication = new SpringApplication(MvcViewApplication.class);
      springApplication.run(args);
  }
}
