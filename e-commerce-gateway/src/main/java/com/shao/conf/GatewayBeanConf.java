package com.shao.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author : SH35856
 * @Description: 网关需要注入到容器中的Bean
 * @date: 2022/9/12 13:36
 */
@Configuration
public class GatewayBeanConf {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
