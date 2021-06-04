package com.siwuxie095.spring.cloud.chapter5th.example5th;

/**
 * @author Jiajing Li
 * @date 2021-06-04 08:04:35
 */
@SuppressWarnings("all")
public class Main {

    /**
     * 配置使用 Spring Cloud 和 Hystrix 的依赖
     *
     * 开始对 Hystrix 的研究，你需要设置你的项目的 pom.xml 文件导入 Spring Hystrix 依赖。你将获得已经构建的
     * 许可服务，并通过添加 Hystrix 的 Maven 依赖来修改 pom.xml 文件：
     *
     *  <dependency>
     *     <groupId>org.springframework.cloud</groupId>
     *     <artifactId>spring-cloud-starter-hystrix</artifactId>
     *  </dependency>
     *  <dependency>
     *     <groupId>com.netflix.hystrix</groupId>
     *     <artifactId>hystrix-javanica</artifactId>
     *     <version>1.5.9</version>
     *  </dependency>
     *
     * 第一个 <dependency> 标签（spring-cloud-starter-hystrix）告诉 Maven 拉取 Spring Cloud Hystrix
     * 依赖。第二个 <dependency> 标签（hystrix-javanica）将拉取 Netflix Hystrix 核心库。设置 Maven 依赖，
     * 你就可以开始使用在之前构建的许可服务和组织服务的 Hystrix 实现。
     *
     * 注意：你不必直接在 pom.xml 文件包括 hystrix-javanica 依赖。默认情况下，spring-cloud-starter-hystrix
     * 包括了一个版本的 hystrix-javanica 的依赖。这里的 Camden.SR5 版本使用 hystrix-javanica-1.5.6。
     * hystrix-javanica 的版本有一个不一致的引入，导致 Hystrix 代码没有回退，抛出 java.lang.reflect.
     * UndeclaredThrowableException 异常代替 com.netflix.hystrix.exception.HystrixRuntimeException
     * 异常。对于许多使用旧版本的 Hystrix 的开发者来说，这是一个突然的变化。hystrix-javanica 库固定在以后的
     * 版本中，所以这里特意使用一个较新的 hystrix-javanica 版本替换 Spring Cloud 内使用的默认版本。
     *
     * 最后一件事是，你能在你的应用程序代码里使用 Hystrix 断路器之前，使用 @EnableCircuitBreaker 注解注释你
     * 的服务的引导类。例如，对于许可服务，你将在 Application 类中增加 @EnableCircuitBreaker 注解。如下所示。
     *
     * @SpringBootApplication
     * @EnableEurekaClient
     * @EnableCircuitBreaker
     * public class Application {
     *
     *     @LoadBalanced
     *     @Bean
     *     public RestTemplate restTemplate(){
     *         return new RestTemplate();
     *     }
     *
     *     public static void main(String[] args) {
     *         SpringApplication.run(Application.class, args);
     *     }
     *
     * }
     *
     * 注意：如果你忘记在引导类添加 @EnableCircuitBreaker 注解，没有断路器将会被激活。当服务启动时，你将不会
     * 收到任何警告或错误消息。
     */
    public static void main(String[] args) {

    }

}
