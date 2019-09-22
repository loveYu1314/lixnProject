package com.lixn.login;

import com.lixn.login.filter.CompareKickOutFilter;
import com.lixn.login.filter.KickOutFilter;
import com.lixn.login.filter.QueueKickOutFilter;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;


/**
 * @创建人 lixiangnan
 * @创建时间 2019/9/19
 * @描述
 */
@SpringBootApplication
public class LoginApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoginApplication.class,args);
    }

    @Bean
    public RedissonClient redissonClient(){
        Config config = new Config();
        config.setCodec(new JsonJacksonCodec())
                .useSingleServer()
                .setAddress("redis://localhost:6379");
        return Redisson.create(config);
    }

    @ConditionalOnProperty(value = {"queue-filter.enabled"})
    @Bean
    public KickOutFilter queueKickOutFilter() {
        return new QueueKickOutFilter();
    }

    @ConditionalOnMissingBean(KickOutFilter.class)
    @Bean
    public KickOutFilter compareKickOutFilter() {
        return new CompareKickOutFilter();
    }

    @Bean
    public FilterRegistrationBean testFilterRegistration(KickOutFilter kickOutFilter) {
        System.out.println(kickOutFilter);
        FilterRegistrationBean registration = new FilterRegistrationBean(kickOutFilter);
        registration.addUrlPatterns("/user/*");
        registration.setName("kickOutFilter");
        return registration;
    }
}
