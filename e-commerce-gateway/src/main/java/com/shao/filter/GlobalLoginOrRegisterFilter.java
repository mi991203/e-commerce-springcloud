package com.shao.filter;

import com.alibaba.fastjson.JSON;
import com.shao.constant.CommonConstant;
import com.shao.constant.GatewayConstant;
import com.shao.util.TokenParseUtil;
import com.shao.vo.JwtToken;
import com.shao.vo.LoginUserInfo;
import com.shao.vo.UsernameAndPassword;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author : SH35856
 * @Description: 服务鉴权过滤器
 * @date: 2022/9/9 13:36
 */
@Slf4j
@Component
public class GlobalLoginOrRegisterFilter implements GlobalFilter, Ordered {
    /**
     * 从注册中心拿到节点信息，负载均衡
     */
    private final LoadBalancerClient loadBalancerClient;

    private final RestTemplate restTemplate;

    public GlobalLoginOrRegisterFilter(LoadBalancerClient loadBalancerClient, RestTemplate restTemplate) {
        this.loadBalancerClient = loadBalancerClient;
        this.restTemplate = restTemplate;
    }

    /**
     * <h2>登录、注册、鉴权</h2>
     * 1. 如果是登录或注册, 则去授权中心拿到 Token 并返回给客户端
     * 2. 如果是访问其他的服务, 则鉴权, 没有权限返回 401
     * */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 如果是登录
        if (request.getURI().getPath().contains(GatewayConstant.LOGIN_URI)) {
            String token = getTokenFromAuthorityCenter(request, GatewayConstant.AUTHORITY_CENTER_TOKEN_URL_FORMAT);
            // header是不能设置为null的
            response.getHeaders().add(CommonConstant.JWT_USER_INFO_KEY, null == token ? "null" : token);
            response.setStatusCode(HttpStatus.OK);
            return response.setComplete();
        }
        if (request.getURI().getPath().contains(GatewayConstant.REGISTER_URI)) {
            String token = getTokenFromAuthorityCenter(request, GatewayConstant.AUTHORITY_CENTER_REGISTER_URL_FORMAT);
            // header是不能设置为null的
            response.getHeaders().add(CommonConstant.JWT_USER_INFO_KEY, null == token ? "null" : token);
            response.setStatusCode(HttpStatus.OK);
            return response.setComplete();
        }

        // 3. 访问其他的服务, 则鉴权, 校验是否能够从 Token 中解析出用户信息
        String token = request.getHeaders().getFirst(CommonConstant.JWT_USER_INFO_KEY);
        LoginUserInfo loginUserInfo = null;
        try {
            loginUserInfo = TokenParseUtil.parseUserInfoFromToken(token);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        // 获取不到登录用户信息, 返回 401
        if (loginUserInfo == null) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        // 解析通过, 则放行
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 2;
    }

    /**
     * 从授权中心获取到Token
     * @param request 请求
     * @param uriFormat 请求格式
     * @return token
     */
    private String getTokenFromAuthorityCenter(ServerHttpRequest request, String uriFormat) {
        // service id 就是服务名字, 负载均衡
        ServiceInstance serviceInstance = loadBalancerClient.choose(CommonConstant.AUTHORITY_CENTER_SERVICE_ID);
        log.info("Nacos Client Info: [{}], [{}], [{}]",
                serviceInstance.getServiceId(), serviceInstance.getInstanceId(),
                JSON.toJSONString(serviceInstance.getMetadata()));
        String requestUrl = String.format(uriFormat, serviceInstance.getHost(), serviceInstance.getPort());
        UsernameAndPassword requestBody = JSON.parseObject(parseBodyFromRequest(request), UsernameAndPassword.class);
        log.info("login request url and body: [{}], [{}]", requestUrl, JSON.toJSONString(requestBody));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JwtToken token = restTemplate.postForObject(
                requestUrl,
                new HttpEntity<>(JSON.toJSONString(requestBody), headers),
                JwtToken.class
        );
        if (null != token) {
            return token.getToken();
        }
        return null;
    }

    /**
     * 从Post请求中拿取到Body里面的数据
     * @param request 请求对象
     * @return body里面信息
     */
    private String parseBodyFromRequest(ServerHttpRequest request) {
        // 获取请求体
        Flux<DataBuffer> body = request.getBody();
        AtomicReference<String> bodyRef = new AtomicReference<>();
        body.subscribe(buffer -> {
            StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
            DataBufferUtils.release(buffer);
        });
        return bodyRef.get();
    }
}
