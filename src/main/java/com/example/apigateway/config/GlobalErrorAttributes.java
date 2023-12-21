package com.example.apigateway.config;

import com.example.springbootmicroservicesframework.exception.BadRequestException;
import com.example.springbootmicroservicesframework.utils.Const;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {


    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        var map = super.getErrorAttributes(request, options);
        var error = this.getError(request);

        if (error instanceof AuthenticationException) {
            map.put(Const.ERROR, HttpStatus.UNAUTHORIZED);
            map.put(Const.MESSAGE, HttpStatus.UNAUTHORIZED.getReasonPhrase());
            map.put(Const.STATUS, HttpStatus.UNAUTHORIZED.value());
        } else if (error instanceof BadRequestException) {
            map.put(Const.ERROR, HttpStatus.BAD_REQUEST);
            map.put(Const.MESSAGE, HttpStatus.BAD_REQUEST.getReasonPhrase());
            map.put(Const.STATUS, HttpStatus.BAD_REQUEST.value());
        } else {
            map.put(Const.ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
            map.put(Const.MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            map.put(Const.STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return map;
    }

}
