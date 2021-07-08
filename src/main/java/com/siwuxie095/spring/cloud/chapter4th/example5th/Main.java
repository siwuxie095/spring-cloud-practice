package com.siwuxie095.spring.cloud.chapter4th.example5th;

/**
 * @author Jiajing Li
 * @date 2021-05-30 22:56:22
 */
public class Main {

    /**
     * 通过 Spring Eureka 注册服务
     *
     * 现在已经有了一个基于 Spring 的 Eureka 服务器正在运行。在这里，将配置组织服务和许可证服务，以便通过
     * Eureka 服务器来注册它们自身。这项工作是为了让服务客户端从 Eureka 注册表中查找服务做好准备，可以让你
     * 对如何通过 Eureka 注册 Spring Boot 微服务有一个明确的认识。
     *
     * 通过 Eureka 注册一个基于 Spring Boot 的微服务是非常简单的。在这里不会详细介绍编写服务所涉及的所有
     * Java 代码（这里故意将代码量保持得很少），而是专注于如何使用之前创建的 Eureka 服务注册表来注册服务。
     *
     * 首先需要做的是将 Spring Eureka 依赖项添加到组织服务的 pom.xml 文件中：
     *
     * <dependency>
     *   <groupId>org.springframework.cloud</groupId>
     *   <artifactId>spring-cloud-starter-eureka</artifactId>　　⇽---　 引入 Eureka 库，以便可以
     *                                                                 使用 Eureka 注册服务
     * </dependency>
     *
     * 唯一使用的新库是 spring-cloud-starter-eureka 库。spring-cloud-starter- eureka 拥有 Spring
     * Cloud 用于与 Eureka 服务进行交互的 jar 文件。
     *
     * 在创建好 pom.xml 文件后，需要告诉 Spring Boot 通过 Eureka 注册组织服务。这个注册是通过组织服务的
     * src/main/java/resources/application.yml 文件中的额外配置来完成的，如下所示。
     *
     * spring:
     *   application:
     *     name: organizationservice　　⇽---　 将使用 Eureka 注册的服务的逻辑名称
     *   profiles:
     *     active:
     *       default
     *   cloud:
     *    config:
     *      enabled: true
     * eureka:
     *   instance:
     *     preferIpAddress: true　　⇽---　 注册服务的 IP，而不是服务器名称
     *   client:
     *     registerWithEureka: true　　⇽---　 向 Eureka 注册服务
     *     fetchRegistry: true
     *     serviceUrl:　　⇽---　 拉取注册表的本地副本
     *       defaultZone: http://localhost:8761/eureka/　　⇽---　 Eureka 服务的位置
     *
     * 每个通过 Eureka 注册的服务都会有两个与之相关的组件：应用程序 ID 和实例 ID。应用程序 ID 用于表示一
     * 组服务实例。在基于 Spring Boot 的微服务中，应用程序 ID 始终是由 spring.application.name 属性
     * 设置的值。对于上述组织服务，spring.application.name 被命名为 organizationservice。实例 ID 是
     * 一个随机数，用于代表单个服务实例。
     *
     * 注意：记住，通常 spring.application.name 属性写在 bootstrap.yml 文件中。为了便于说明，这里把
     * 它包含在 application.yml 文件中。上述代码将与 spring.application.name 一起使用，但是从长远来
     * 看，这个属性的适当位置是在 bootstrap.yml 文件中。
     *
     * 配置的第二部分提供了如何通过 Eureka 注册服务以及将服务注册在哪里。eureka.instance.preferIpAddress
     * 属性告诉 Eureka，要将服务的 IP 地址而不是服务的主机名注册到 Eureka。
     *
     *
     * PS：为什么偏向于 IP 地址
     *
     * 在默认情况下，Eureka 在尝试注册服务时，将会使用主机名让外界与它进行联系。这种方式在基于服务器的环境
     * 中运行良好，在这样的环境中，服务会被分配一个 DNS 支持的主机名。但是，在基于容器的部署（如 Docker）
     * 中，容器将以随机生成的主机名启动，并且该容器没有 DNS 记录。
     *
     * 如果没有将 eureka.instance.preferIpAddress 设置为 true，那么客户端应用程序将无法正确地解析主机
     * 名的位置，因为该容器不存在 DNS 记录。设置 preferIpAddress 属性将通知 Eureka 服务，客户端想要通过
     * IP 地址进行通告。
     *
     * 就这里而言，始终将这个属性设置为 true。基于云的微服务应该是短暂的和无状态的，它们可以随意启动和关闭。
     * IP 地址更适合这些类型的服务。
     *
     *
     * eureka.client.registerWithEureka 属性是一个触发器，它可以告诉组织服务通过 Eureka 注册它本身。
     *
     * eureka.client.fetchRegistry 属性用于告知 Spring Eureka 客户端以获取注册表的本地副本。将此属性
     * 设置为 true 将在本地缓存注册表，而不是每次查找服务都调用 Eureka 服务。每隔 30 s，客户端软件就会重
     * 新联系 Eureka 服务，以便查看注册表是否有任何变化。
     *
     * 最后一个属性 eureka.serviceUrl.defaultZone 包含客户端用于解析服务位置的 Eureka 服务的列表，该
     * 列表以逗号进行分隔。对于这里而言，只有一个 Eureka 服务。
     *
     *
     * PS：Eureka 高可用性
     *
     * 建立多个 URL 服务并不足以实现高可用性。eureka.serviceUrl.defaultZone 属性仅为客户端提供一个进行
     * 通信的 Eureka 服务列表。除此之外，还需要建立多个 Eureka 服务，以便相互复制注册表的内容。
     *
     * 一组 Eureka 注册表相互之间使用点对点通信模型进行通信，在这种模型中，必须对每个 Eureka 服务进行配置，
     * 以了解集群中的其他节点。建立 Eureka 集群的内容超出了这里的范围。如果你有兴趣建立 Eureka 集群，可以
     * 访问 Spring Cloud 项目的网站以获取更多信息。
     *
     *
     * 到目前为止，已经有一个通过 Eureka 服务注册的服务。
     *
     * 可以使用 Eureka 的 REST API 来查看注册表的内容。要查看服务的所有实例，可以以 GET 方法访问端点：
     *
     * http://<eureka service>:8761/eureka/apps/<APPID>
     *
     * 例如，要查看注册表中的组织服务，可以访问：
     *
     * http://localhost:8761/eureka/apps/organizationservice
     *
     * Eureka 服务返回的默认格式是 XML。Eureka 还可以将结果数据作为 JSON 净荷返回，但是必须将 HTTP 首部
     * Accept 设置为 application/json。
     *
     *
     * PS：在 Eureka 和服务启动时要保持耐心
     *
     * 当服务通过 Eureka 注册时，Eureka 将在 30 s 内等待 3 次连续的健康检查，然后才能通过 Eureka 获取
     * 该服务。这个热身过程让开发者感到疑惑，因为如果他们在服务启动后立即调用他们的服务，他们会认为 Eureka
     * 还没有注册他们的服务。这一点在 Docker 环境运行的代码示例中很明显，因为 Eureka 服务和应用程序服务
     * （许可证服务和组织服务）都是在同一时间启动的。请注意，在启动应用程序后，尽管服务本身已经启动，你可能
     * 会收到关于未找到服务的 404 错误。等待 30 s，然后再尝试调用服务。
     */
    public static void main(String[] args) {

    }

}
