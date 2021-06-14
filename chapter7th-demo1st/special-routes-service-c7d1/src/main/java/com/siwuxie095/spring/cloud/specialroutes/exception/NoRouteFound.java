package com.siwuxie095.spring.cloud.specialroutes.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Jiajing Li
 * @date 2021-06-14 21:30:15
 */
@SuppressWarnings("all")
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoRouteFound extends RuntimeException{
}
