package com.siwuxie095.spring.cloud.chapter4th.example4th;

/**
 * @author Jiajing Li
 * @date 2021-05-30 17:42:07
 */
@SuppressWarnings("all")
public class Main {

    /**
     * 创建 Spring Eureka Service
     *
     * 在这里，你将使用 Spring Boot 设置 Eureka 服务。与 Spring Cloud 配置服务一样，设置 Spring
     * Cloud Eureka 服务首先要构建一个新的 Spring Boot 项目，并应用注解和配置。从 Maven pom.xml
     * 文件开始，如下显示了 Spring Boot 项目需要设置的 Eureka 服务依赖。
     *
     *         <dependency>
     *             <groupId>org.springframework.cloud</groupId>
     *             <artifactId>spring-cloud-starter-eureka-server</artifactId>
     *         </dependency>
     *
     * 然后你就需要设置 src/main/resources/application.yml 文件，它使用需要的配置来设置在独立模式
     * 下运行的 Eureka 服务（例如，集群中没有其他节点），如下所示。
     *
     * server:
     *   port: 8761
     *
     * eureka:
     *   client:
     *     registerWithEureka: false
     *     fetchRegistry: false
     *   server:
     *     waitTimeInMsWhenSyncEmpty: 5
     *
     * 关键属性被设置为 server.port 属性，它设置用于该服务的默认端口。
     *
     * eureka.client.registerWithEureka 属性告诉服务当 Spring Boot Eureka 应用启动的时候，不在
     * Eureka 服务注册，因为它是 Eureka 服务。eureka.client.fetchRegistry 属性被设置为 false，
     * Eureka 服务启动时，它不会尝试本地化缓存它的注册信息。当运行一个 Eureka 客户端时，你需要为要在
     * Eureka 注册的 Spring Boot 服务更改此值。
     *
     * 你会发现最后一个属性，eureka.server.waitTimeInMsWhenSync 为空，被注释掉了。当你测试你的本地
     * 服务时，你应该取消注释，因为 Eureka 不会立即通告任何已注册的服务。默认情况下，它会等待五分钟，在
     * 广播之前给所有的服务一个注册的机会。为本地测试取消注释，将有助于将对大大加快 Eureka 服务启动时间
     * 和显示注册服务。
     *
     * 单个服务注册并显示在 Eureka 服务需要 30 秒，因为在回复服务准备就绪之前，需要连续三次心跳，每次
     * 从服务 ping 间隔 10 秒。在部署和测试自己的服务时，请记住这一点。在设置 Eureka 服务时，你要做
     * 的最后一件工作是向应用程序引导类添加一个注解，用于启动 Eureka 服务。
     *
     * 对于 Eureka 服务，应用引导类如下：
     *
     * @SpringBootApplication
     * @EnableEurekaServer
     * public class EurekaServerApplication {
     *
     *     public static void main(String[] args) {
     *         SpringApplication.run(EurekaServerApplication.class, args);
     *     }
     *
     * }
     *
     * 你只使用一个新的注解，告诉你的服务是一个 Eureka 服务，即 @EnableEurekaServer。此刻，你可以
     * 通过 mvn spring-boot:run 运行启动 Eureka 服务或 docker-compose 启动服务。一旦运行了这个
     * 命令，你将有一个正在运行的 Eureka 服务，其中没有注册服务。后续你将创建组织服务并将其注册到
     * Eureka 服务中。
     */
    public static void main(String[] args) {

    }

}
