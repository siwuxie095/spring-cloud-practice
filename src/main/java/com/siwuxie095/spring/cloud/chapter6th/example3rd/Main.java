package com.siwuxie095.spring.cloud.chapter6th.example3rd;

/**
 * @author Jiajing Li
 * @date 2021-06-09 21:25:17
 */
@SuppressWarnings("all")
public class Main {

    /**
     * 使用 Spring Cloud 注解配置 Zuul 服务
     *
     * Spring Cloud 集成了 Netflix 的开源项目 Zuul。Zuul 是服务网关，通过 Spring Cloud 注解非常易于
     * 设置和使用。Zuul 提供了许多功能，包括：
     * （1）将应用程序中所有服务的路由映射到一个 URL。Zuul 并不局限于单一的 URL。在 Zuul，你可以定义多个
     * 路由条目，使路径映射极细粒度（每个服务端点都有自己的路由映射）。然而，对于 Zuul 第一和最常见的情况
     * 是建立一个单一的入口点，所有的服务客户端调用都流经它。
     * （2）构建能够检查和响应通过网关的请求的过滤器。这些过滤器允许您在代码中注入策略执行点，并以一致的方式
     * 在所有服务调用上执行大量操作。
     *
     * 开始使用 Zuul 之前，你要做的三件事：
     * （1）创建一个 Zuul Spring Boot 项目并配置适当的 Maven 的依赖。
     * （2）修改你的 Spring Boot 项目与 Spring Cloud 注解，告诉它，它将是一个 Zuul 服务。
     * （3）配置 Zuul 与 Eureka 通信（可选）。
     *
     *
     *
     * 1、配置 Spring Boot 工程引用 Zuul 依赖
     *
     * 创建一个 Zuul 服务器，你需要建立一个新的 Spring Boot 启动服务并定义相应的 Maven 依赖。幸运的是，
     * 在 Maven 中很少需要设置 Zuul。你只需要在你的 zuul-server/pom.xml 文件中定义一个依赖：
     *
     *         <dependency>
     *             <groupId>org.springframework.cloud</groupId>
     *             <artifactId>spring-cloud-starter-zuul</artifactId>
     *         </dependency>
     *
     * 这个依赖告诉 Spring Cloud 框架，这个服务将运行 Zuul 和适当初始化 Zuul。
     *
     *
     *
     * 2、使用 Spring Cloud 注解配置 Zuul 服务
     *
     * 你定义 Maven 依赖之后，你需要注解 Zuul 服务的引导类。Zuul 服务引导类实现可以在 Application 类找
     * 到。
     *
     * @SpringBootApplication
     * @EnableZuulProxy
     * public class ZuulServerApplication {
     *
     *     public static void main(String[] args) {
     *         SpringApplication.run(ZuulServerApplication.class, args);
     *     }
     *
     * }
     *
     * 就是这样。只有一个注解需要到位：@EnableZuulProxy。
     *
     * 注意:如果你查看文档，你可能会注意到一个名为 @EnableZuulServer 的注解。使用这个注解将创建一个 Zuul
     * 服务器，它不加载任何的 Zuul 反向代理过滤器或使用 Netflix Eureka 服务发现。当你想建立你自己的路由
     * 服务和不使用任何 Zuul 预置功能时，@EnableZuulServer 被使用。举个例子，如果你想使用 Zuul 与服务
     * 发现引擎集成（如：Eureka、Consul），就只能使用 @EnableZuulProxy 注解。
     *
     *
     *
     * 3、配置 Zuul 与 Eureka 通信
     *
     * Zuul 代理服务器被设计为默认情况下在 Spring 产品下工作。因此，Zuul 将自动使用 Eureka 通过服务 ID
     * 查找服务，然后使用 Netflix 的 Ribbon 为 Zuul 请求做客户端的负载均衡。
     *
     * 在配置过程的最后一步是修改你的 Zuul 服务器的 application.yml 文件指向你的 Eureka 服务器。下列代
     * 码显示的是 Zuul 与 Eureka 通信的配置。
     *
     * eureka:
     *   instance:
     *     preferIpAddress: true
     *   client:
     *     registerWithEureka: true
     *     fetchRegistry: true
     *     serviceUrl:
     *         defaultZone: http://localhost:8761/eureka/
     */
    public static void main(String[] args) {

    }

}
