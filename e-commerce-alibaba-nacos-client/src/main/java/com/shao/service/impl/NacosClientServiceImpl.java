package com.shao.service.impl;

import com.shao.service.NacosClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class NacosClientServiceImpl implements NacosClientService {

    private final DiscoveryClient discoveryClient;

    public NacosClientServiceImpl(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    public List<ServiceInstance> getNacosInstance(String serviceId) {
      log.info("nacos serviceId = {}", serviceId);
      return discoveryClient.getInstances(serviceId);
    }
}
