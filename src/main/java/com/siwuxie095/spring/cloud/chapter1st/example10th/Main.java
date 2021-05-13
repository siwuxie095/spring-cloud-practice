package com.siwuxie095.spring.cloud.chapter1st.example10th;

/**
 * @author Jiajing Li
 * @date 2021-05-13 08:23:38
 */
public class Main {

    /**
     * 使用 Spring Cloud 构建微服务
     *
     * 在这一部分中，将简单介绍 Spring Cloud 技术，你将使用它创建你的微服务。这是一个高层次的概述，在这里，
     * 当你使用各种技术，在你需要的时候会教你每个细节。
     *
     * 从零开始实现所有微服务模式将是一份惊人的工作量。幸运的是，Spring 团队集成了大量经过充分测试的开源项
     * 目为 Spring 子项目，统称为 Spring Cloud（https://spring.io/projects/spring-cloud）。
     *
     * Spring Cloud 把开源公司，如 Pivotal，HashiCorp 和 Netflix 交付的产品整合在一起。 Spring Cloud
     * 简化了设置和配置这些项目到你的 Spring 应用程序，使你可以专注于写代码，没有被掩埋在如何构建和部署一个
     * 微服务应用的所有基础设施的配置细节里。
     *
     * 微服务的模式可以映射到实现它们的 Spring Cloud 项目。
     *
     *
     *
     * 1、Spring Boot
     *
     * Spring Boot 是用于微服务实现的核心技术。Spring Boot 通过简化构建基于 REST 微服务的核心任务，极大
     * 的简化了微服务开发。Spring Boot 也极大的简化了 HTTP 方式谓词（GET, PUT, POST 和 DELETE）与 URLs
     * 的映射，JSON 与 Java 对象互相转换的序列化协议，以及 Java 异常返回与标准的 HTTP 错误代码的映射。
     *
     *
     *
     * 2、Spring Cloud Config
     *
     * Spring Cloud Config 处理应用程序的配置数据的管理，通过一个集中的服务使你的应用程序配置数据（尤其是
     * 你的环境的具体配置数据）从你的部署微服务分离干净。这确保了无论你扩展多少微服务实例，它们总是会有相同的
     * 配置。Spring Cloud Config 有自己的属性管理仓库，还集成了开源项目，如以下：
     * （1）Git：Git 是一个开放源码的版本控制系统，可以管理和跟踪的任何类型的文本文件的变化。Spring Cloud
     * Config 可以与 Git 支持的存储库集成，并从存储库中读取应用程序的配置数据。
     * （2）Consul：Consul 是一个开放源代码的服务发现工具，允许服务实例使用该服务注册自己。服务客户端可以向
     * Consul 询问服务实例位于何处。Consul 还包括关键的基于键值的数据库，可以使用 Spring Cloud Config
     * 来存储应用程序配置数据。
     * （3）Eureka：Eureka 是一个开放源码的 Netflix 项目，如 Consul，提供类似的服务发现能力。Eureka 也
     * 有一个键值数据库，可以与 Spring Cloud Config 一起使用。
     *
     * PS：
     * （1）Git：https://git-scm.com/
     * （2）Consul：https://www.consul.io/
     * （3）https://github.com/netflix/eureka
     *
     *
     *
     * 3、Spring Cloud Service Discovery
     *
     * 通过 Spring Cloud 服务发现，你可以将消费服务的客户端从服务器部署的物理位置（IP 和/或服务器名称）抽
     * 离出来。服务消费者通过逻辑名称而不是物理位置调用服务器的业务逻辑。Spring Cloud 服务发现还负责注册和
     * 注销服务实例，当服务实例启动和停止的时候。Spring Cloud 服务发现可以使用 Consul 和 Eureka 作为服务
     * 发现引擎。
     *
     *
     *
     * 4、Spring Cloud/Netflix Hystrix and Ribbon
     *
     * Spring Cloud 在很大程度上集成了 Netflix 开源项目。为了实现微服务客户端弹性模式，Spring Cloud 将
     * Netflix Hystrix 库和 Ribbon 项目集成在一起，在你的微服务实施中使用它们。
     *
     * 使用 Netflix Hystrix 库，你可以快速实现服务客户端的弹性模式，如断路器和舱壁模式。
     *
     * 而 Netflix Ribbon 项目简化了与服务发现代理（如 Eureka）的集成，它还提供了来自服务消费者的服务调用
     * 的客户端负载均衡。这使得即使服务发现代理暂时不可用，客户端也可以继续进行服务调用。
     *
     * PS：
     * （1）Netflix Hystrix：https://github.com/Netflix/Hystrix
     * （2）Netflix Ribbon：https://github.com/Netflix/Ribbon
     *
     *
     *
     * 5、Spring Cloud/Netflix Zuul
     *
     * Spring Cloud 使用 Netflix Zuul 项目为你的微服务应用提供服务路由能力。Zuul 是服务网关，它代理服务
     * 请求并确保在目标服务被调用之前，所有对你微服务的调用都通过一个单一的 "前门"。有了这种集中的服务调用，
     * 你可以执行标准的服务策略，如安全授权验证、内容过滤和路由规则。
     *
     * PS：
     * （1）Netflix Zuul：https://github.com/Netflix/zuul
     *
     *
     *
     * 6、Spring Cloud Stream
     *
     * Spring Cloud Stream 是一门有利的技术，你可以很容易将轻量级消息处理整合到你的微服务。使用 Spring
     * Cloud Stream，你可以构建智能的微服务，它可以使用在你的应用程序发生的异步事件。在 Spring Cloud
     * Stream，你能快速将你的微服务与消息中间件集成，如：RabbitMQ 和 Kafka。
     *
     * PS：
     * （1）Spring Cloud Stream：https://spring.io/projects/spring-cloud-stream
     * （2）RabbitMQ：https://www.rabbitmq.com/
     * （3）Kafka:http://kafka.apache.org/
     *
     *
     *
     * 7、Spring Cloud Sleuth
     *
     * Spring Cloud Sleuth 允许你将唯一的跟踪标识符集成到 HTTP 调用和在你的应用程序使用的消息通道
     * （RabbitMQ、Apache Kafka）。这些跟踪号，有时称为关联或跟踪 ID，允许你跟踪一个事务，因为它在
     * 应用程序中的不同服务之间传递。在 Spring Cloud Sleuth，这些跟踪 ID 被自动添加到你在微服务中
     * 产生的任意日志语句。
     *
     * Spring Cloud Sleuth 真正的优势是与日志聚合技术工具（如 Papertrail）和跟踪工具（如 Zipkin）
     * 结合。Papertrail 是一个基于云计算的日志平台，用于将不同微服务实时日志汇总到一个可查询的数据库。
     * 开源 Zipkin 使用 Spring Cloud Sleuth 产生的数据，允许你可视化一个单一事务的有关联的服务调
     * 用流程。
     *
     * PS：
     * （1）Spring Cloud Sleuth：https://spring.io/projects/spring-cloud-sleuth
     * （2）Papertrail：http://papertrailapp.com
     * （3）Zipkin：http://zipkin.io
     *
     *
     *
     * 8、Spring Cloud Security
     *
     * Spring Cloud Security 是一个身份验证和授权框架，它能控制谁可以访问你的服务和它们能为你的服务
     * 做些什么？Spring Cloud Security 是基于令牌的，它允许服务通过一个由身份验证服务器发出的令牌互
     * 相通信。每个服务接收到一个调用，可以检查在 HTTP 调用中提供的令牌，来验证用户的身份和它们访问服
     * 务的权限。另外，Spring Cloud Security 支持 JavaScript Web Token。JavaScript Web Token
     * （JWT）框架规范 OAuth2 令牌格式如何被创建和为数据签名创建的令牌提供标准。
     *
     * PS：
     * （1）Spring Cloud Security：https://spring.io/projects/spring-cloud-security
     * （2）JavaScript Web Token：https://jwt.io/
     *
     *
     *
     * 9、准备些什么
     *
     * 为准备实现，这里要进行技术改造。Spring 框架是面向应用开发的。Spring 框架（包括 Spring Cloud）
     * 没有创建 "构建和部署" 管道的工具。实现 "构建和部署" 管道要使用下列工具：Travis CI 构建工具和
     * Docker 容器，来创建包含你的微服务的最终的服务器镜像。
     *
     * PS：
     * （1）Travis CI：https://travis-ci.org/
     * （2）Docker：https://www.docker.com/
     *
     *
     *
     * 总结，微服务模式分别对应 Spring Cloud 的这些子项目：
     *
     * （1）微服务核心开发模式：Spring Boot，其中配置管理对应 Spring Cloud Config，事件处理对应 Spring
     * Cloud Stream。
     *
     * （2）微服务路由模式：Spring Cloud Service Discovery，其中服务发现对应 Spring Cloud/Netflix
     * Eureka，服务路由对应 Spring Cloud/Netflix Zuul。
     *
     * （3）微服务客户端弹性模式：其中客户端负载均衡对应 Spring Cloud/Netflix Ribbon，断路器对应 Spring
     * Cloud/Netflix Hystrix，回退对应 Spring Cloud/Netflix Hystrix，舱壁对应 Spring Cloud/Netflix
     * Hystrix。
     *
     * （4）微服务安全模式：其中认证对应 Spring Cloud Security/OAuth2，授权对应 Spring Cloud
     * Security/OAuth2，证书管理和传播对应 Spring Cloud Security/OAuth2/JWT。
     *
     * （5）微服务日志记录和跟踪模式：其中日志关联分析对应 Spring Cloud Sleuth，日志聚合对应 Spring
     * Cloud Sleuth（with Papertrail），微服务跟踪对应 Spring Cloud Sleuth（with Zipkin）。
     *
     * （6）微服务构建和部署模式：其中构建和部署管道对应 Travis CI，基础设施即代码对应 Docker，不可变的服
     * 务器对应 Docker，凤凰服务器对应 Travis CI/Docker。
     */
    public static void main(String[] args) {

    }

}
