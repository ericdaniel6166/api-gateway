package com.example.apigateway.config;

import com.example.springbootmicroservicesframework.utils.Const;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;

@Component
@Slf4j
public class GlobalErrorAttributes extends DefaultErrorAttributes {


    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        var map = super.getErrorAttributes(request, options);
        var error = this.getError(request);
        if (error instanceof AuthenticationException) {
            map.put(Const.ERROR, HttpStatus.UNAUTHORIZED);
            map.put(Const.MESSAGE, HttpStatus.UNAUTHORIZED.getReasonPhrase());
            map.put(Const.STATUS, HttpStatus.UNAUTHORIZED.value());
            return map;
        }
        if (error instanceof WebClientResponseException responseException) {
            HttpStatus httpStatus = HttpStatus.valueOf(responseException.getStatusCode().value());
            map.put(Const.ERROR, httpStatus);
            map.put(Const.MESSAGE, httpStatus.getReasonPhrase());
            map.put(Const.STATUS, httpStatus.value());
            return map;
        }
        log.info("error.getClass().getName(): {}, error.getMessage(): {}", error.getClass().getName(), error.getMessage());
        map.put(Const.ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        map.put(Const.MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        map.put(Const.STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value());
        return map;
    }

}
