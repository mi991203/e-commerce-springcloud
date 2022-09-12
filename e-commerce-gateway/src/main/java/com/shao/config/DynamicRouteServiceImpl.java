package com.shao.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author : SH35856
 * @Description: 动态更新路由网关实现类
 * @date: 2022/9/8 14:29
 */
@Service
@Slf4j
public class DynamicRouteServiceImpl implements ApplicationEventPublisherAware {
    private final RouteDefinitionWriter routeDefinitionWriter;

    private final RouteDefinitionLocator routeDefinitionLocator;

    /**
     * 事件推送句柄对象
     */
    private ApplicationEventPublisher applicationEventPublisher;

    public DynamicRouteServiceImpl(RouteDefinitionWriter routeDefinitionWriter, RouteDefinitionLocator routeDefinitionLocator) {
        this.routeDefinitionWriter = routeDefinitionWriter;
        this.routeDefinitionLocator = routeDefinitionLocator;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public String addRouteDefinition(RouteDefinition routerDefinition) {
        log.info("gateway add route: [{}]", routerDefinition);
        // 保存路由配置并发布出去——刷新网关路由定义
        routeDefinitionWriter.save(Mono.just(routerDefinition)).subscribe();
        // 发布事件通知给gateway,同步新增路由定义
        this.applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
        return "add success";
    }

    /**
     * <h2>根据路由 id 删除路由配置</h2>
     * */
    private String deleteById(String id) {
        try {
            log.info("gateway delete route id: [{}]", id);
            this.routeDefinitionWriter.delete(Mono.just(id)).subscribe();
            // 发布事件通知给 gateway 更新路由定义
            this.applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
            return "delete success";
        } catch (Exception ex) {
            log.error("gateway delete route fail: [{}]", ex.getMessage(), ex);
            return "delete fail";
        }
    }

    /**
     * 全量更新覆盖更新
     * */
    public String updateList(List<RouteDefinition> definitions) {

        log.info("gateway update route: [{}]", definitions);

        // 先拿到当前 Gateway 中存储的路由定义
        List<RouteDefinition> routeDefinitionsExits =
                routeDefinitionLocator.getRouteDefinitions().buffer().blockFirst();
        if (!CollectionUtils.isEmpty(routeDefinitionsExits)) {
            // 清除掉之前所有的 "旧的" 路由定义
            routeDefinitionsExits.forEach(rd -> {
                log.info("delete route definition: [{}]", rd);
                deleteById(rd.getId());
            });
        }

        // 把更新的路由定义同步到 gateway 中
        definitions.forEach(definition -> updateByRouteDefinition(definition));
        return "success";
    }

    /**
     * <h2>更新路由</h2>
     * 更新的实现策略比较简单: 删除 + 新增 = 更新
     * */
    private String updateByRouteDefinition(RouteDefinition definition) {
        try {
            log.info("gateway update route: [{}]", definition);
            this.routeDefinitionWriter.delete(Mono.just(definition.getId()));
        } catch (Exception ex) {
            return "update fail, not find route routeId: " + definition.getId();
        }

        try {
            this.routeDefinitionWriter.save(Mono.just(definition)).subscribe();
            this.applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
            return "update route success";
        } catch (Exception ex) {
            return "update route fail";
        }
    }

}
