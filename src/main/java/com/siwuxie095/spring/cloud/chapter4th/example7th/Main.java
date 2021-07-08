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
     * （2）诸如 Eureka 这样的服务发现引擎可以在不影响服务客户端的情况下，无缝地向环境中添加和从环境中移除服务实例。
     * （3）通过在进行服务调用的客户端中缓存服务的物理位置，客户端负载均衡可以提供额外的性能和弹性。
     * （4）Eureka 是 Netflix 项目，在与 Spring Cloud 一起使用时，很容易对 Eureka 进行建立和配置。
     * （5）这里在 Spring Cloud、Netflix Eureka 和 Netflix Ribbon 中使用了三种不同的机制来调用服务。这些机制
     * 包括：
     * 1）使用 Spring Cloud 服务 DiscoveryClient；
     * 2）使用 Spring Cloud 和支持 Ribbon 的 RestTemplate；
     * 3）使用 Spring Cloud 和 Netflix 的 Feign 客户端。
     */
    public static void main(String[] args) {

    }

}
