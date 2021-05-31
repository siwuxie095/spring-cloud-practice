package com.siwuxie095.spring.cloud.chapter4th.example7th;

/**
 * @author Jiajing Li
 * @date 2021-05-31 22:59:29
 */
public class Main {

    /**
     * 小结
     *
     * （1）服务发现模式用于抽象服务的物理位置。
     * （2）像 Eureka 这样的服务发现引擎可以无缝地添加和删除环境中的服务实例，而不影响服务客户端。
     * （3）客户端负载均衡可以通过在服务调用的客户端上缓存服务的物理位置来提供额外的性能和弹性。
     * （4）Eureka 是一个 Netflix 项目，当与 Spring Cloud 一起使用时，很容易设置和配置。
     * （5）你在 Spring Cloud，Netflix Eureka 和 Netflix Ribbon 中使用三种不同的机制来调用
     * 服务。这些机制包括：
     * 1）使用一个 Spring Cloud 服务 DiscoveryClient。
     * 2）使用 Spring Cloud 和支持 Ribbon 的 RestTemplate。
     * 3）使用 Spring Cloud 和 Netflix 的 Feign 客户端。
     */
    public static void main(String[] args) {

    }

}
