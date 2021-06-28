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
// 告诉 Spring Boot 框架，该类是 Spring Boot 服务的入口点
@SpringBootApplication
// 告诉 Spring Boot，要将该类中的代码公开为 Spring RestController 类
@RestController
// 此应用程序中公开的所有 URL 将以 /hello 前缀开头
@RequestMapping(value="hello")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    // Spring Boot 公开为一个基于 GET 方法的 REST 端点，它将使用两个参数，即 firstName 和 lastName
    @RequestMapping(value="/{firstName}/{lastName}",method = RequestMethod.GET)
    // 将 URL 中传入的 firstName 和 lastName 参数映射为传递给 hello 方法的两个变量
    public String hello( @PathVariable("firstName") String firstName,
                         @PathVariable("lastName") String lastName) {
        // 返回一个手动构建的简单 JSON 字符串
        return String.format("{\"message\":\"Hello %s %s\"}", firstName, lastName);
    }

}
