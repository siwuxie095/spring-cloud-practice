package com.siwuxie095.spring.cloud.chapter4th.example4th;

/**
 * @author Jiajing Li
 * @date 2021-05-30 17:42:07
 */
@SuppressWarnings("all")
public class Main {

    /**
     * 构建 Spring Eureka 服务
     *
     * 在这里，将通过 Spring Boot 建立 Eureka 服务。与 Spring Cloud 配置服务一样，这里将从构建新的 Spring
     * Boot 项目开始，并应用注解和配置来建立 Spring Cloud Eureka 服务。首先从 Maven 的 pom.xml 开始。如下
     * 代码展示了正在建立的 Spring Boot 项目所需的 Eureka 服务依赖项。
     *
     * <?xml version="1.0" encoding="UTF-8"?>
     * <project xmlns="http://maven.apache.org/POM/4.0.0"
     * ➥  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
     *     xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://
     *     ➥  maven.apache.org/xsd/maven-4.0.0.xsd">
     *
     *   <modelVersion>4.0.0</modelVersion>
     *
     *   <groupId>com.thoughtmechanix</groupId>
     *   <artifactId>eurekasvr</artifactId>
     *   <version>0.0.1-SNAPSHOT</version>
     *   <packaging>jar</packaging>
     *
     *   <name>Eureka Server</name>
     *   <description>Eureka Server demo project</description>
     *
     * <!-- 没有显示使用 Spring Cloud Parent 的 Maven 定义 -->
     *   <dependencies>
     *     <dependency>
     *       <groupId>org.springframework.cloud</groupId>
     *       <artifactId>spring-cloud-starter-eureka-server</artifactId>　　⇽---　 告诉 Maven 构建
     *                                                             包含 Eureka 库（其中包括 Ribbon）
     *     </dependency>
     *   </dependencies>
     *
     * 为了简洁，省略了 pom.xml 的其余部分
     * ....
     * </project>
     *
     * 接着，需要创建 src/main/resources/application.yml 文件，在这里需要添加以独立模式（例如，集群中没有
     * 其他节点）运行 Eureka 服务所需的配置，如下所示。
     *
     * server:
     *   port: 8761　　⇽---　 Eureka 服务器将要监听的端口
     *
     * eureka:
     *   client:
     *     registerWithEureka: false　　⇽---　 不要使用 Eureka 服务进行注册
     *   fetchRegistry: false　　⇽---　 不要在本地缓存注册表信息
     *   server:
     *     waitTimeInMsWhenSyncEmpty: 5　　⇽---　 在服务器接收请求之前等待的初始时间
     *
     * 要设置的关键属性是 server.port 属性，它用于设置 Eureka 服务的默认端口。
     *
     * eureka.client.registerWithEureka 属性会告知服务，在 Spring Boot Eureka 应用程序启动时不要通
     * 过 Eureka 服务注册，因为它本身就是 Eureka 服务。eureka.client.fetchRegistry 属性设置为 false，
     * 以便 Eureka 服务启动时，它不会尝试在本地缓存注册表信息。在运行 Eureka 客户端时，为了缓存通过 Eureka
     * 注册的 Spring Boot 服务，需要更改 eureka.client.fetchRegistry 的值。
     *
     * 你会注意到，最后一个属性 eureka.server.waitTimeInMsWhenSyncEmpty 被注释掉了。在本地测试服务时，
     * 应该取消注释此行，因为 Eureka 不会马上通告任何通过它注册的服务，默认情况下它会等待 5 min，让所有的
     * 服务都有机会在通告它们之前通过它来注册。进行本地测试时取消注释此行，将有助于加快 Eureka 服务启动和
     * 显示通过它注册服务所需的时间。
     *
     * 每次服务注册需要 30 s 的时间才能显示在 Eureka 服务中，因为 Eureka 需要从服务接收 3 次连续心跳包
     * ping，每次心跳包 ping 间隔 10 s，然后才能使用这个服务。在部署和测试服务时，要牢记这一点。
     *
     * 在建立 Eureka 服务时，需要进行的最后一项工作就是在启动 Eureka 服务的应用程序引导类中添加注解。对于
     * Eureka 服务，应用程序引导类即 EurekaServerApplication。如下代码展示了添加注解的位置。
     *
     * @SpringBootApplication
     * // 在 Spring 服务中启用 Eureka 服务器
     * @EnableEurekaServer
     * public class EurekaServerApplication {
     *
     *     public static void main(String[] args) {
     *         SpringApplication.run(EurekaServerApplication.class, args);
     *     }
     *
     * }
     *
     * 只需要使用一个新的注解 @EnableEurekaServer，就可以让服务成为一个 Eureka 服务。此时，可以通过运行
     * mvn spring-boot:run 或运行 docker-compose 来启动服务。一旦运行这个命令，Eureka 服务就会运行，
     * 此时没有任何服务注册在这个 Eureka 服务中。后续将构建组织服务，并通过这个 Eureka 服务注册。
     */
    public static void main(String[] args) {

    }

}
