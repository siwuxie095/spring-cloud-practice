package com.siwuxie095.spring.cloud.chapter6th.example3rd;

/**
 * @author Jiajing Li
 * @date 2021-06-09 21:25:17
 */
@SuppressWarnings("all")
public class Main {

    /**
     * Spring Cloud 和 Netflix Zuul 简介
     *
     * Spring Cloud 集成了 Netflix 开源项目 Zuul。Zuul 是一个服务网关，它非常容易通过 Spring Cloud 注解
     * 进行创建和使用。Zuul 提供了许多功能，具体包括以下几个。
     * （1）将应用程序中的所有服务的路由映射到一个 URL：Zuul 不局限于一个 URL。在 Zuul 中，开发人员可以定义
     * 多个路由条目，使路由映射非常细粒度（每个服务端点都有自己的路由映射）。然而，Zuul 最常见的用例是构建一个
     * 单一的入口点，所有服务客户端调用都将经过这个入口点。
     * （2）构建可以对通过网关的请求进行检查和操作的过滤器：这些过滤器允许开发人员在代码中注入策略执行点，以一
     * 致的方式对所有服务调用执行大量操作。
     *
     * 要开始使用 Zuul，需要完成下面三件事。
     * （1）建立一个 Zuul Spring Boot 项目，并配置适当的 Maven 依赖项。
     * （2）使用 Spring Cloud 注解修改这个 Spring Boot 项目，将其声明为 Zuul 服务。
     * （3）配置 Zuul 以便 Eureka 进行通信（可选）。
     *
     *
     *
     * 1、建立一个 Zuul Spring Boot 项目
     *
     * 要构建一个 Zuul 服务器，需要建立一个新的 Spring Boot 服务并定义相应的 Maven 依赖项。在 Maven 中建立
     * Zuul 只需要很少的步骤，只需要在 pom.xml 文件中定义一个依赖项：
     *
     * <dependency>
     *   <groupId>org.springframework.cloud</groupId>
     *   <artifactId>spring-cloud-starter-zuul</artifactId>
     * </dependency>
     *
     * 这个依赖项告诉 Spring Cloud 框架，该服务将运行 Zuul，并适当地初始化 Zuul。
     *
     *
     *
     * 2、为 Zuul 服务使用 Spring Cloud 注解
     *
     * 在定义完 Maven 依赖项后，需要为 Zuul 服务的引导类添加注解。Zuul 服务实现的引导类即 Application 类。
     * 如下代码展示了如何为 Zuul 服务的引导类添加注解。
     *
     * @SpringBootApplication
     * // 使服务成为一个 Zuul 服务器
     * @EnableZuulProxy
     * public class ZuulServerApplication {
     *
     *     public static void main(String[] args) {
     *         SpringApplication.run(ZuulServerApplication.class, args);
     *     }
     *
     * }
     *
     * 就这样，这里只需要一个注解：@EnableZuulProxy。
     *
     * 注意：如果你浏览过文档或启用了自动补全，那么可能会注意到一个名为 @EnableZuulServer 的注解。使用此注解
     * 将创建一个 Zuul 服务器，它不会加载任何 Zuul 反向代理过滤器，也不会使用 Netflix Eureka 进行服务发现。
     * 开发人员想要构建自己的路由服务，而不使用任何 Zuul 预置的功能时会使用 @EnableZuulServer，举例来讲，当
     * 开发人员需要使用 Zuul 与 Eureka 之外的其他服务发现引擎（如 Consul）进行集成的时候。对于这里来说，只会
     * 使用 @EnableZuulProxy 注解。
     *
     *
     *
     * 3、配置 Zuul 与 Eureka 进行通信
     *
     * Zuul 代理服务器默认设计为在 Spring 产品上工作。因此，Zuul 将自动使用 Eureka 来通过服务 ID 查找服务，
     * 然后使用 Netflix Ribbon 对来自 Zuul 的请求进行客户端负载均衡。
     *
     * 配置过程的最后一步是修改 Zuul 服务器的 application.yml 文件，以指向 Eureka 服务器。如下代码展示了
     * Zuul 与 Eureka 通信所需的 Zuul 配置。
     *
     * eureka:
     *   instance:
     *     preferIpAddress: true
     *   client:
     *     registerWithEureka: true
     *     fetchRegistry: true
     *     serviceUrl:
     *       defaultZone: http://localhost:8761/eureka/
     */
    public static void main(String[] args) {

    }

}
