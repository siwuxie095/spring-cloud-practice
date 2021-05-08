package com.siwuxie095.spring.cloud.simpleservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jiajing Li
 * @date 2021-05-08 21:07:46
 */
@SuppressWarnings("all")
// 告诉 Spring Boot 框架，这是 Spring Boot 服务的入口点。
@SpringBootApplication
// 告诉 Spring Boot，这个类将作为 Spring RestController 暴露
@RestController
// 这个应用程序的所有 URL 将以前缀 /hello 进行暴露
@RequestMapping(value="hello")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    // Spring Boot 将端点暴露为基于 GET 的 REST 端点，它将接受两个参数：firstName 和 lastName
    @RequestMapping(value="/{firstName}/{lastName}",method = RequestMethod.GET)
    // 将 URL 传入的 firstName 和 lastName 参数映射到 hello 方法的两个变量
    public String hello( @PathVariable("firstName") String firstName,
                         @PathVariable("lastName") String lastName) {
        // 返回一个手动编写的简单 JSON 字符串
        return String.format("{\"message\":\"Hello %s %s\"}", firstName, lastName);
    }

}
