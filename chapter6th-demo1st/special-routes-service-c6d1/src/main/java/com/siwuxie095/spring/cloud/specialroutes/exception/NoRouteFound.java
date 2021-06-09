package com.siwuxie095.spring.cloud.specialroutes.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Jiajing Li
 * @date 2021-06-09 23:00:42
 */
@SuppressWarnings("all")
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoRouteFound extends RuntimeException{
}
