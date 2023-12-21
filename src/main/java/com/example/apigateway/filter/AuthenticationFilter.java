package com.example.apigateway.filter;

import com.example.springbootmicroservicesframework.utils.Const;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private WebClient.Builder webClientBuilder;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                throw new BadCredentialsException("missing authorization header");
            }

            var authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            if (StringUtils.isBlank(authHeader) || !authHeader.startsWith(Const.HEADER_AUTHORIZATION_PREFIX)) {
                throw new BadCredentialsException("token missing");
            }
            try {
                return webClientBuilder.build().get().uri("lb://user-service/api/user/auth/verify-token")
                        .headers(httpHeaders -> httpHeaders.set(HttpHeaders.AUTHORIZATION, authHeader))
                        .retrieve()
                        .bodyToMono(Object.class)
                        .map(o -> {
                            exchange.getRequest()
                                    .mutate()
                                    .headers(httpHeaders -> httpHeaders.set("userId", "test-user-id"));
                            return exchange;
                        })
                        .flatMap(chain::filter);
            } catch (Exception e) {
                throw new BadCredentialsException("unauthorized access to application");
            }
        });
    }

    public static class Config {

    }
}
