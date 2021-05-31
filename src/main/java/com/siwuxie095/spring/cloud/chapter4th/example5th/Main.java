package com.siwuxie095.spring.cloud.chapter4th.example5th;

/**
 * @author Jiajing Li
 * @date 2021-05-30 22:56:22
 */
public class Main {

    /**
     * 使用 Spring Eureka 注册服务
     *
     * 现在，你有一个基于 Spring 的 Eureka 服务器运行。在这里，你将配置您的组织和许可服务，并将它们自己
     * 注册到 Eureka 服务器。这项准备工作是为了让服务客户端从你的 Eureka 注册信息中查找服务。
     *
     * 用 Eureka 注册一个基于 Spring Boot 的微服务是一个非常简单的练习。这里的目的，不需要了解编写服务
     * 的所有的 Java 代码（这里特意保留小量的代码），而是专注于在之前创建的 Eureka 服务中注册服务。
     *
     * 你需要做的第一件事是把 Spring Eureka 依赖增加到你的组织服务的 pom.xml 文件：
     *
     *         <dependency>
     *             <groupId>org.springframework.cloud</groupId>
     *             <artifactId>spring-cloud-starter-eureka</artifactId>
     *         </dependency>
     *
     * spring-cloud-starter-eureka 库是正在使用的唯一新库。Spring Cloud 将使用 spring-cloud
     * -starter-eureka 构件与 Eureka 服务进行交互。
     *
     * 当你创建 pom.xml 文件，你需要告诉 Spring Boot 使用 Eureka 注册组织服务。这个注册是通过增加组织
     * 服务的 src/main/java/resources/application.yml 文件的配置，如下所列。
     *
     * spring:
     *   application:
     *     name: organization-service
     *   profiles:
     *     active:
     *       default
     *   cloud:
     *     config:
     *       enabled: true
     *
     * eureka:
     *   instance:
     *     preferIpAddress: true
     *   client:
     *     registerWithEureka: true
     *     fetchRegistry: true
     *     serviceUrl:
     *         defaultZone: http://localhost:8761/eureka/
     *
     * 在 Eureka 中注册的每个服务都有两个要素：应用程序 ID 和实例 ID。应用程序 ID 用于表示一组服务实例。
     * 在一个基于 Spring Boot 的微服务，应用程序 ID 将总是由 spring.application.name 属性设置值。
     * 对于组织服务，spring.application.name 被命名为 organization-service。实例 ID 将是一个随机
     * 数，用于表示单个服务实例。
     *
     * 注意：记住，通常 spring.application.name 属性在 bootstrap.yml 文件配置。这里已经将它包括在
     * application.yml 来说明用途。该代码将与 spring.application.name 一起工作，但这个属性长期合
     * 适的地方为是 bootstrap.yml 文件。
     *
     * 你的配置的第二部分提供了服务应该如何和在哪里注册到 Eureka 服务。eureka.instance
     * .preferIpAddress 属性告诉 Eureka 要注册到 Eureka 的是服务 IP 地址，而不是主机
     * 名。
     *
     *
     * PS：为什么更喜欢 IP 地址
     *
     * 默认情况下，Eureka 将会尝试注册与其通信服务的主机名。这在基于服务器的环境中运行良好，在该环境中，
     * 服务被指派了一个 DNS 支持的主机名。然而，在一个基于容器的部署（例如，Docker 容器），将使用随机
     * 生成的主机名启动而不是容器的 DNS 条目。
     *
     * 如果你不设置 eureka.instance.preferIpAddress 为 true，你的客户端应用程序将不能妥善解决主机
     * 名的位置，因为没有这个容器的 DNS 条目。设置 preferIpAddress 属性将通知 Eureka 服务，客户端
     * 通过 IP 地址广播。
     *
     * 就个人而言，总是将这个属性设置为 true。基于云的微服务应该是短暂的和无状态的。它们可以启动并随时
     * 关闭。IP 地址更适合这些类型的服务。
     *
     *
     * eureka.client.registerWithEureka 属性是触发器，告诉组织服务在 Eureka 注册它本身。eureka
     * .client.fetchRegistry 属性用于 Spring Eureka 客户端读取注册信息的本地副本。将此属性设置为
     * true 将在本地缓存注册信息，而不是每次查找都调用 Eureka 服务。每隔 30 秒，客户端软件将重新与
     * Eureka 服务通信，以便对注册信息进行任何更改。
     *
     * 最后一个属性，eureka.serviceUrl.defaultZone 属性，使用一个逗号分隔的 Eureka 服务列表，客户
     * 端将使用解析服务位置。对于这里而言，你只需要一个 Eureka 服务。
     *
     *
     * PS：Eureka 的高可用性
     *
     * 设置多个 URL 服务对于高可用性是不够的。eureka.serviceUrl.defaultZone 属性只提供了一个与客户
     * 端通信的 Eureka 服务列表。你还需要设置 Eureka 服务来复制它们彼此之间注册信息的内容。
     *
     * 一组 Eureka 注册中心使用对等通信模型进行通信，为了解集群中的其他节点，其中每个 Eureka 服务都必
     * 须配置。建立一个 Eureka 集群超出了这里的范围，不再详细讨论。
     *
     *
     * 此时，你将在你的 Eureka 服务中注册一个服务。
     *
     * 你可以使用 Eureka 的 REST API 来查看注册信息的内容。要查看服务的所有实例，请单击以下 GET
     * 端点：
     *
     * http://<eureka service>:8761/eureka/apps/<APPID>
     *
     * 例如，为了在注册信息中查看组织服务，你可以调用：
     *
     * http://localhost:8761/eureka/apps/organization-service
     *
     * 调用 Eureka 的 REST API 来查看组织服务，将显示在 Eureka 注册的服务实例的 IP 地址，以及
     * 服务状态。
     *
     * Eureka 服务返回的默认格式是 XML。Eureka 还可以将数据作为 JSON 有效载荷返回，但必须将 HTTP 头
     * 的 Accept 属性设置为 application/json。
     *
     *
     * PS：在 Eureka 和服务启动时，不要心急
     *
     * 当一个服务在 Eureka 注册，服务通过 Eureka 成为可用之前，Eureka 将在 30 秒的过程中等待三个连续
     * 的健康检查。这一暖机时间将抛开开发人员，因为它们认为 Eureka 如果他们在服务启动后立即尝试调用它们
     * 的服务，它们就不会注册他们的服务。这里的代码示例在 Docker 环境下运行，因为 Eureka 服务和应用程
     * 序服务（许可和组织服务）都在同一时间启动。请注意，在应用程序启动后，即使服务本身已经启动，你可能会
     * 收到关于未找到服务的 404 错误。等待 30 秒钟后再调用你的服务。
     */
    public static void main(String[] args) {

    }

}
