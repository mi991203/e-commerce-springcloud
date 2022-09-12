package com.shao.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * @author : SH35856
 * @Description: 监听Nacos中相关配置的变更。整体思路就是在项目初始化的时候读取Nacos中特定group下的data_id的配置信息，然后加载，然后设置监听，当配置发生更改的时候，会触发监听处理事件逻辑。
 * @date: 2022/9/8 16:16
 */
@Component
@Slf4j
@DependsOn("gatewayConfig")
public class DynamicRouteServiceImplByNacos {

    private final DynamicRouteServiceImpl dynamicRouteService;

    public DynamicRouteServiceImplByNacos(DynamicRouteServiceImpl dynamicRouteService) {
        this.dynamicRouteService = dynamicRouteService;
    }

    @PostConstruct
    public void init() {
        log.info("gateway route init...");
        // Nacos配置服务对象
        ConfigService configService;
        try {
            // init ConfigService
            final Properties properties = new Properties();
            properties.setProperty("serverAddr", GatewayConfig.NACOS_SERVER_ADDR);
            properties.setProperty("namespace", GatewayConfig.NACOS_NAMESPACE);
            configService = NacosFactory.createConfigService(properties);
        } catch (NacosException e) {
            log.error(e.getMessage(), e);
            return;
        }
        try {
            final String configStr = configService.getConfig(GatewayConfig.NACOS_ROUTE_DATA_ID, GatewayConfig.NACOS_ROUTE_GROUP, GatewayConfig.DEFAULT_TIMEOUT);
            final List<RouteDefinition> routeDefinitionList = JSON.parseArray(configStr, RouteDefinition.class);
            if (!CollectionUtils.isEmpty(routeDefinitionList)) {
                for (RouteDefinition routeDefinition : routeDefinitionList) {
                    log.info("init definition: {}", routeDefinition.toString());
                    dynamicRouteService.addRouteDefinition(routeDefinition);
                }
            }
        } catch (NacosException e) {
            log.error(e.getMessage(), e);
            return;
        }
        // 设置监听器
        try {
            configService.addListener(GatewayConfig.NACOS_ROUTE_DATA_ID, GatewayConfig.NACOS_ROUTE_GROUP, new Listener() {
                /**
                 * 使用默认线程池
                 * @return null
                 */
                @Override
                public Executor getExecutor() {
                    return null;
                }

                /**
                 * 收到配置更新如何处理
                 * @param s 新的配置信息
                 */
                @Override
                public void receiveConfigInfo(String s) {
                    log.info("nacos_route_group={},nacos_route_data_id={}的路由配置信息发生了变更, 变更后的信息为{}", GatewayConfig.NACOS_ROUTE_GROUP,
                            GatewayConfig.NACOS_ROUTE_DATA_ID, s);
                    final List<RouteDefinition> routeDefinitionList = JSON.parseArray(s, RouteDefinition.class);
                    dynamicRouteService.updateList(routeDefinitionList);
                }
            });
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        }
    }
}
