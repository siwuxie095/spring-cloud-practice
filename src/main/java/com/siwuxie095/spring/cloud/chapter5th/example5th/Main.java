package com.siwuxie095.spring.cloud.chapter5th.example5th;

/**
 * @author Jiajing Li
 * @date 2021-06-04 08:04:35
 */
@SuppressWarnings("all")
public class Main {

    /**
     * 搭建许可服务器以使用 Spring Cloud 和 Hystrix
     *
     * 要开始对 Hystrix 的探索，需要创建项目的 pom.xml 文件来导入 Spring Hystrix 依赖项。这里将使用之前一直在构建
     * 的许可证服务，并通过添加 Hystrix 的 Maven 依赖项来修改 pom.xml 文件：
     *
     * <dependency>
     *   <groupId>org.springframework.cloud</groupId>
     *   <artifactId>spring-cloud-starter-hystrix</artifactId>
     * </dependency>
     * <dependency>
     *   <groupId>com.netflix.hystrix</groupId>
     *   <artifactId>hystrix-javanica</artifactId>
     *   <version>1.5.9</version>
     * </dependency>
     *
     * 第一个 <dependency> 标签（spring-cloud-starter-hystrix）告诉 Maven 去拉取 Spring Cloud Hystrix 依赖
     * 项。第二个 <dependency> 标签（hystrix-javanica）将拉取核心 Netflix Hystrix 库。创建完 Maven 依赖项后，
     * 可以继续使用之前构建的许可证服务和组织服务来开始 Hystrix 的实现。
     *
     * 注意：
     * 不一定要在 pom.xml 中直接包含 hystrix-javanica 依赖项。在默认情况下，spring-cloud-starter-hystrix 包括
     * 一个 hystrix-javanica 依赖项的版本。这里使用的 Camden.SR5 发行版本使用了 hystrix-javanica-1.5.6。这个
     * hystrix-javanica 的版本有一个不一致的地方，它导致 Hystrix 代码在没有后备的情况下会抛出 java.lang.reflect
     * .UndeclaredThrowableException 而不是 com.netflix.hystrix.exception.HystrixRuntimeException。对于
     * 使用旧版 Hystrix 的许多开发人员来说，这是一个破坏性的变化。hystrix-javanica 库在后来的版本中解决了这个问题，
     * 所以这里专门使用了更高版本的 hystrix-javanica，而不是使用 Spring Cloud 引入的默认版本。
     *
     * 在应用程序代码中开始使用 Hystrix 断路器之前，需要完成的最后一件事情是，使用 @EnableCircuitBreaker 注解来标
     * 注服务的引导类。例如，对于许可证服务，最好将 @EnableCircuitBreaker 注解添加到 Application 类中。如下所示。
     *
     * @SpringBootApplication
     * @EnableEurekaClient
     * // 告诉 Spring Cloud 将要为服务使用 Hystrix
     * @EnableCircuitBreaker
     * public class Application {
     *
     *     @LoadBalanced
     *     @Bean
     *     public RestTemplate restTemplate() {
     *         return new RestTemplate();
     *     }
     *
     *     public static void main(String[] args) {
     *         SpringApplication.run(Application.class, args);
     *     }
     *
     * }
     *
     * 注意：如果忘记将 @EnableCircuitBreaker 注解添加到引导类中，那么 Hystrix 断路器不会处于活动状态。在服务启动
     * 时，不会收到任何警告或错误消息。
     */
    public static void main(String[] args) {

    }

}
