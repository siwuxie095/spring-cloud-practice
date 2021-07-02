package com.siwuxie095.spring.cloud.chapter1st.example10th;

/**
 * @author Jiajing Li
 * @date 2021-05-13 08:23:38
 */
public class Main {

    /**
     * 使用 Spring Cloud 构建微服务
     *
     * 这里将简要介绍在构建微服务时会使用的 Spring Cloud 技术。这是一个高层次的概述。后续使用各项技术时，
     * 会根据需要来讲解这些技术的细节。
     *
     * 从零开始实现所有这些模式将是一项巨大的工作。幸好，Spring 团队将大量经过实战检验的开源项目整合到一
     * 个称为 Spring Cloud 的 Spring 子项目中。
     *
     * Spring Cloud 将 Pivotal、HashiCorp 和 Netflix 等开源公司的工作封装在一起。Spring Cloud 简
     * 化了将这些项目设置和配置到 Spring 应用程序中的工作，以便开发人员可以专注于编写代码，而不会陷入配
     * 置构建和部署微服务应用程序的所有基础设施的细节中。
     *
     * 六类微服务模式可以映射到实现它们的 Spring Cloud 项目。
     * （1）开发模式：
     * a）核心微服务模式：Spring Boot
     * b）配置管理：Spring Cloud Config
     * c）异步消息处理：Spring Cloud Stream
     * （2）路由模式：
     * a）服务发现模式：Spring Cloud/Netflix Eureka
     * b）服务路由模式：Spring Cloud/Netflix Zuul
     * （3）客户端弹性模式：
     * a）客户端负载均衡：Spring Cloud/Netflix Ribbon
     * b）断路器模式：Spring Cloud/Netflix Hystrix
     * c）后备模式：Spring Cloud/Netflix Hystrix
     * d）舱壁模式：Spring Cloud/Netflix Hystrix
     * （4）安全模式：
     * a）授权：Spring Cloud Security/OAuth2
     * b）验证：Spring Cloud Security/OAuth2
     * c）凭据管理和传播：Spring Cloud Security/OAuth2/JWT
     * （5）日志记录模式：
     * a）日志关联：Spring Cloud Sleuth
     * b）日志聚合：Spring Cloud Sleuth（与 Papertrail）
     * c）微服务跟踪：Spring Cloud Sleuth/Zipkin
     * （6）构建部署模式：
     * a）持续集成：Travis CI
     * b）基础设施即代码：Docker
     * c）不可变服务器：Docker
     * d）凤凰服务器：Travis CI/Docker
     *
     * 下面更详细地了解一下这些技术。
     *
     *
     *
     * 1、Spring Boot
     *
     * Spring Boot 是微服务实现中使用的核心技术。Spring Boot 通过简化构建基于 REST 的微服务的核心任务，
     * 大大简化了微服务开发。Spring Boot 还极大地简化了将 HTTP 类型的动词（GET、PUT、POST和DELETE）映
     * 射到 URL、JSON 协议序列化与 Java 对象的相互转化，以及将 Java 异常映射回标准 HTTP 错误代码的工作。
     *
     *
     *
     * 2、Spring Cloud Config
     *
     * Spring Cloud Config 通过集中式服务来处理应用程序配置数据的管理，因此应用程序配置数据（特别是环境
     * 特定的配置数据）与部署的微服务完全分离。这确保了无论启动多少个微服务实例，这些微服务实例始终具有相同
     * 的配置。Spring Cloud Config 拥有自己的属性管理存储库，也可以与以下开源项目集成。
     * （1）Git：Git 是一个开源版本控制系统，它允许开发人员管理和跟踪任何类型的文本文件的更改。
     * Spring Cloud Config 可以与 Git 支持的存储库集成，并读出存储库中的应用程序的配置数据。
     * （2）Consul：Consul 是一种开源的服务发现工具，允许服务实例向该服务注册自己。服务客户端
     * 可以向 Consul 咨询服务实例的位置。Consul 还包括可以被 Spring Cloud Config 使用的基
     * 于键值存储的数据库，能够用来存储应用程序的配置数据。
     * （3）Eureka：Eureka 是一个开源的 Netflix 项目，像 Consul 一样，提供类似的服务发现功
     * 能。Eureka 同样有一个可以被 Spring Cloud Config 使用的键值数据库。
     *
     * PS：
     * （1）Git：https://git-scm.com/
     * （2）Consul：https://www.consul.io/
     * （3）https://github.com/netflix/eureka
     *
     *
     *
     * 3、Spring Cloud 服务发现
     *
     * 通过 Spring Cloud 服务发现，开发人员可以从客户端消费的服务中抽象出部署服务器的物理位置（IP 或服务
     * 器名称）。服务消费者通过逻辑名称而不是物理位置来调用服务器的业务逻辑。Spring Cloud 服务发现也处理
     * 服务实例的注册和注销（在服务实例启动和关闭时）。Spring Cloud 服务发现可以使用 Consul 和 Eureka
     * 作为服务发现引擎。
     *
     *
     *
     * 4、Spring Cloud 与 Netflix Hystrix 和 Netflix Ribbon
     *
     * Spring Cloud 与 Netflix 的开源项目进行了大量整合。对于微服务客户端弹性模式，Spring Cloud 封装
     * 了 Netflix Hystrix 库和 Netflix Ribbon 项目，开发人员可以轻松地在微服务中使用它们。
     *
     * 使用 Netflix Hystrix 库，开发人员可以快速实现服务客户端弹性模式，如断路器模式和舱壁模式。
     *
     * 虽然 Netflix Ribbon 项目简化了与诸如 Eureka 这样的服务发现代理的集成，但它也为服务消费者提供了
     * 客户端对服务调用的负载均衡。即使在服务发现代理暂时不可用时，客户端也可以继续进行服务调用。
     *
     * PS：
     * （1）Netflix Hystrix：https://github.com/Netflix/Hystrix
     * （2）Netflix Ribbon：https://github.com/Netflix/Ribbon
     *
     *
     *
     * 5、Spring Cloud 与 Netflix Zuul
     *
     * Spring Cloud 使用 Netflix Zuul 项目为微服务应用程序提供服务路由功能。Zuul 是代理服务请求的服务
     * 网关，确保在调用目标服务之前，对微服务的所有调用都经过一个 "前门"。通过集中的服务调用，开发人员可以
     * 强制执行标准服务策略，如安全授权验证、内容过滤和路由规则。
     *
     * PS：
     * （1）Netflix Zuul：https://github.com/Netflix/zuul
     *
     *
     *
     * 6、Spring Cloud Stream
     *
     * Spring Cloud Stream 是一种可让开发人员轻松地将轻量级消息处理集成到微服务中的支持技术。借助 Spring
     * Cloud Stream，开发人员能够构建智能的微服务，它可以使用在应用程序中出现的异步事件。此外，使用 Spring
     * Cloud Stream 可以快速将微服务与消息代理进行整合，如 RabbitMQ 和 Kafka。
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
     * Spring Cloud Sleuth 允许将唯一跟踪标识符集成到应用程序所使用的 HTTP 调用和消息通道（RabbitMQ、
     * Apache Kafka）之中。这些跟踪号码（有时称为关联 ID 或跟踪 ID）能够让开发人员在事务流经应用程序中
     * 的不同服务时跟踪事务。有了 Spring Cloud Sleuth，这些跟踪 ID 将自动添加到微服务生成的任何日志记
     * 录中。
     *
     * Spring Cloud Sleuth 与日志聚合技术工具（如 Papertrail）和跟踪工具（如 Zipkin）结合时，能够展
     * 现出真正的威力。Papertail 是一个基于云的日志记录平台，用于将日志从不同的微服务实时聚合到一个可查
     * 询的数据库中。Zipkin 可以获取 Spring Cloud Sleuth 生成的数据，并允许开发人员可视化单个事务涉及
     * 的服务调用流程。
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
     * Spring Cloud Security 是一个验证和授权框架，可以控制哪些人可以访问服务，以及他们可以用服务做什么。
     * Spring Cloud Security 是基于令牌的，允许服务通过验证服务器发出的令牌彼此进行通信。接收调用的每个
     * 服务可以检查 HTTP 调用中提供的令牌，以确认用户的身份以及用户对该服务的访问权限。
     *
     * 此外，Spring Cloud Security 支持 JSON Web Token。JSON Web Token（JWT）框架标准化了创建 OAuth2
     * 令牌的格式，并为创建的令牌进行数字签名提供了标准。
     *
     * PS：
     * （1）Spring Cloud Security：https://spring.io/projects/spring-cloud-security
     * （2）JavaScript Web Token：https://jwt.io/
     *
     *
     *
     * 9、代码供应
     *
     * 要实现代码供应，这里将会转移到其他的技术栈。Spring 框架是面向应用程序开发的，它（包括 Spring Cloud）
     * 没有用于创建 "构建和部署" 管道的工具。要实现一个 "构建和部署" 管道，开发人员需要使用 Travis CI 和
     * Docker 这两样工具，前者可以作为构建工具，而后者可以构建包含微服务的服务器镜像。
     *
     * PS：
     * （1）Travis CI：https://travis-ci.org/
     * （2）Docker：https://www.docker.com/
     */
    public static void main(String[] args) {

    }

}
