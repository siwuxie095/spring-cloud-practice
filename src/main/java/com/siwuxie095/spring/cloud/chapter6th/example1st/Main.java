package com.siwuxie095.spring.cloud.chapter6th.example1st;

/**
 * @author Jiajing Li
 * @date 2021-06-07 21:25:01
 */
public class Main {

    /**
     * 使用 Spring Cloud 和 Zuul 进行服务路由
     *
     * 在像微服务架构这样的分布式架构中，需要确保跨多个服务调用的关键行为的正常运行，如安全、日志记录和用户跟踪。
     * 要实现此功能，开发人员需要在所有服务中始终如一地强制这些特性，而不需要每个开发团队都构建自己的解决方案。
     * 虽然可以使用公共库或框架来帮助在单个服务中直接构建这些功能，但这样做会造成三个影响。
     *
     * 第一，在构建的每个服务中很难始终实现这些功能。开发人员专注于交付功能，在每日的快速开发工作中，他们很容易
     * 忘记实现服务日志记录或跟踪。遗憾的是，对那些在金融服务或医疗保健等严格监管的行业工作的人来说，一致且有文
     * 档记录系统中的行为通常是符合政府法规的关键要求。
     *
     * 第二，正确地实现这些功能是一个挑战。对每个正在开发的服务进行诸如微服务安全的建立与配置可能是很痛苦的。将
     * 实现横切关注点（cross-cutting concern，如安全问题）的责任推给各个开发团队，大大增加了开发人员没有正
     * 确实现或忘记实现这些功能的可能性。
     *
     * 第三，这会在所有服务中创建一个顽固的依赖。开发人员在所有服务中共享的公共框架中构建的功能越多，在通用代码
     * 中无需重新编译和重新部署所有服务就能更改或添加功能就越困难。当应用程序中有 6 个微服务时，这似乎不是什么
     * 大问题，但当这个应用程序拥有更多的服务时（大概 30 个或更多），这就是一个很大的问题。突然间，共享库中内置
     * 的核心功能的升级就变成了一个数月的迁移过程。
     *
     * 为了解决这个问题，需要将这些横切关注点抽象成一个独立且作为应用程序中所有微服务调用的过滤器和路由器的服务。
     * 这种横切关注点被称为服务网关（service gateway）。服务客户端不再直接调用服务。取而代之的是，服务网关作
     * 为单个策略执行点（Policy Enforcement Point，PEP），所有调用都通过服务网关进行路由，然后被路由到最终
     * 目的地。
     *
     * 在这里，将看看如何使用 Spring Cloud 和 Netflix 的 Zuul 来实现一个服务网关。Zuul 是 Netflix 的开源
     * 服务网关实现。具体来说，是要看一下如何使用 Spring Cloud 和 Zuul 来完成以下操作。
     * （1）将所有服务调用放在一个 URL 后面，并使用服务发现将这些调用映射到实际的服务实例。
     * （2）将关联 ID 注入流经服务网关的每个服务调用中。
     * （3）在从客户端发回的 HTTP 响应中注入关联 ID。
     * （4）构建一个动态路由机制，将各个具体的组织路由到服务实例端点，该端点与其他人使用的服务实例端点不同。
     *
     * 后续会深入了解服务网关是如何与这里构建的整体微服务相适应的。
     */
    public static void main(String[] args) {

    }

}
