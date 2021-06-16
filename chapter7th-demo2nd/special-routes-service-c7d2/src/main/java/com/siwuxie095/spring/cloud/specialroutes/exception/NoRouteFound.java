package com.siwuxie095.spring.cloud.specialroutes.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Jiajing Li
 * @date 2021-06-16 22:59:04
 */
@SuppressWarnings("all")
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoRouteFound extends RuntimeException{
}
