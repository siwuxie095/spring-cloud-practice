package com.siwuxie095.spring.cloud.chapter5th.example4th;

/**
 * @author Jiajing Li
 * @date 2021-06-03 21:45:47
 */
public class Main {

    /**
     * 熔断机制
     *
     * 建立断路器、回退和舱壁模式的实现需要对线程和线程管理有深入的了解。然而终归是要面对现实，编写健壮的线程
     * 代码是一门艺术，正确地完成它是困难的。为了实现一套高质量的断路器、回退和舱壁模式，需要大量的工作。幸运
     * 的是，你可以使用 Spring Cloud 和 Netflix 的 Hystrix 库来提供一个经过大量测试的库，它日常被应用在
     * Netflix 的微服务结构中。
     *
     * 后续将介绍如何：
     * （1）配置许可服务的 Maven 构建文件（pom.xml）包括 Spring Cloud/Hystrix 依赖。
     * （2）使用 Spring Cloud/Hystrix 注解和断路器模式包装远程调用。
     * （3）在远程资源上自定义单独的断路器来使用每次调用的自定义超时时间。还将演示如何配置断路器，以便控制在
     * 断路器 "跳闸" 之前发生多少次故障。
     * （4）在断路器必须中断调用或调用失败时执行回退策略。
     * （5）在你的服务中使用单个线程池隔离服务调用和建立不同的远程资源之间的舱壁。
     */
    public static void main(String[] args) {

    }

}
