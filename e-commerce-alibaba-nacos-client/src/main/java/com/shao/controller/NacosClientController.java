package com.shao.controller;

import com.shao.service.NacosClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("nacos-client")
@Slf4j
public class NacosClientController {
    private final NacosClientService nacosClientService;

    public NacosClientController(NacosClientService nacosClientService) {
        this.nacosClientService = nacosClientService;
    }

    @GetMapping("/service-instance")
    public List<ServiceInstance> getInstancesByServiceId(@RequestParam("service-id") String serviceId) {
        return nacosClientService.getNacosInstance(serviceId);
    }
}
