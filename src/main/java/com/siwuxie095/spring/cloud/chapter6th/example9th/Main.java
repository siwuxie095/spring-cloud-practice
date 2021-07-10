package com.siwuxie095.spring.cloud.chapter6th.example9th;

/**
 * @author Jiajing Li
 * @date 2021-06-12 23:27:51
 */
public class Main {

    /**
     * 小结
     *
     * （1）Spring Cloud 使构建服务网关变得十分简单。
     * （2）Zuul 服务网关与 Netflix 的 Eureka 服务器集成，可以自动将通过 Eureka 注册的服务映射到 Zuul 路由。
     * （3）Zuul 可以对所有正在管理的路由添加前缀，因此可以轻松地给路由添加 /api 之类的前缀。
     * （4）可以使用 Zuul 手动定义路由映射。这些路由映射是在应用程序配置文件中手动定义的。
     * （5）通过使用 Spring Cloud Config 服务器，可以动态地重新加载路由映射，而无须重新启动 Zuul 服务器。
     * （6）可以在全局和个体服务水平上定制 Zuul 的 Hystrix 和 Ribbon 的超时。
     * （7）Zuul 允许通过 Zuul 过滤器实现自定义业务逻辑。Zuul 有三种类型的过滤器，即前置过滤器、后置过滤器和路由过滤器。
     * （8）Zuul 前置过滤器可用于生成一个关联 ID，该关联 ID 可以注入流经 Zuul 的每个服务中。
     * （9）Zuul 后置过滤器可以将关联 ID 注入服务客户端的每个 HTTP 服务响应中。
     * （10）自定义 Zuul 路由过滤器可以根据 Eureka 服务 ID 执行动态路由，以便在同一服务的不同版本之间进行 A/B 测试。
     */
    public static void main(String[] args) {

    }

}
