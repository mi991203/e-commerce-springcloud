package com.shao.service;

import org.springframework.cloud.client.ServiceInstance;

import java.util.List;

public interface NacosClientService {
    List<ServiceInstance> getNacosInstance(String serviceId);
}
