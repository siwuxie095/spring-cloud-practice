package com.siwuxie095.spring.cloud.chapter6th.example9th;

/**
 * @author Jiajing Li
 * @date 2021-06-12 23:27:51
 */
public class Main {

    /**
     * 小结
     *
     * （1）Spring Cloud 使构建服务网关变得微不足道。
     * （2）Zuul 服务网关集成了 Netflix 的 Eureka 服务器，并且可以将在 Eureka 注册的服务自动映射
     * 到一个 Zuul 路由。
     * （3）Zuul 可以为受管理的所有路由添加前缀，所以你可以很容易为你的路由添加一些如 /api 的前缀。
     * （4）使用 Zuul，你可以手动定义路由映射。这些路由映射是在应用程序配置文件中手动定义的。
     * （5）通过使用 Spring Cloud 配置服务器，你可以动态的重新加载路由映射，而无需重新启动 Zuul
     * 服务器。
     * （6）你可以自定义 Zuul 的 Hystrix 和 Ribbon 超时时间为全局或单独的服务级别。
     * （7）Zuul 允许你通过 Zuul 过滤器实现自定义业务逻辑。Zuul 有三种类型的过滤器：前置、后置和路
     * 由过滤器。
     * （8）Zuul 前置过滤器可用于生成一个关联 ID，它可以注入到流过 Zuul 的每一个服务。
     * （9）Zuul 后置过滤器可以为返回到服务客户端的每个 HTTP 服务响应注入一个关联 ID。
     * （10）自定义 Zuul 路由过滤器可以执行基于 Eureka 服务 ID 的动态路由来为相同服务的不同版本之
     * 间做 A/B 测试。
     */
    public static void main(String[] args) {

    }

}
