package com.lixn.spring.cloud.service.client.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @创建人 lixiangnan
 * @创建时间 2019/9/15
 * @描述
 */
@RestController
public class ServiceController {

    @Autowired
    private DiscoveryClient discoveryClient;

    /**
     * 返回所有的服务名称
     * @return
     */
    @GetMapping("/services")
    public List<String> getAllServices(){
        return discoveryClient.getServices();
    }

    @GetMapping("/service/instances/{serviceName}")
    public List<String> getAllServiceInstances(@PathVariable String serviceName){
        return discoveryClient.getInstances(serviceName)
                .stream()
                .map(s -> s.getServiceId() + "-" + s.getHost() + ":" + s.getPort())
                .collect(Collectors.toList());
    }
}
