package com.siwuxie095.spring.cloud.chapter6th.example4th;

/**
 * @author Jiajing Li
 * @date 2021-06-10 21:29:55
 */
public class Main {

    /**
     * 在 Zuul 中配置路由
     *
     * Zuul 的核心是一个反向代理。反向代理是一个中间服务器，它位于尝试访问资源的客户端和资源本身之间。客户端甚至不知道
     * 它正与代理之外的服务器进行通信。反向代理负责捕获客户端的请求，然后代表客户端调用远程资源。
     *
     * 在微服务架构的情况下，Zuul（反向代理）从客户端接收微服务调用并将其转发给下游服务。服务客户端认为它只与 Zuul 通
     * 信。Zuul 要与下游服务进行沟通，Zuul 必须知道如何将进来的调用映射到下游路由。Zuul 有几种机制来做到这一点，包括：
     * （1）通过服务发现自动映射路由；
     * （2）使用服务发现手动映射路由；
     * （3）使用静态 URL 手动映射路由。
     *
     *
     *
     * 1、通过服务发现自动映射路由
     *
     * Zuul 的所有路由映射都是通过在 application.yml 文件中定义路由来完成的。但是，Zuul 可以根据其服务 ID 自动路由
     * 请求，而不需要配置。如果没有指定任何路由，Zuul 将自动使用正在调用的服务的 Eureka 服务 ID，并将其映射到下游服务
     * 实例。例如，如果要调用 organizationservice 并通过 Zuul 使用自动路由，则可以使用以下 URL 作为端点，让客户端
     * 调用 Zuul 服务实例：
     *
     * http://localhost:5555/organizationservice/v1/organizations/e254f8c-c442-4ebe-a82a-e2fc1d1ff78a
     *
     * Zuul 服务器可通过 http://localhost:5555 进行访问，如：
     *
     * http://localhost:5555/organizationservice/v1/organizations/
     *
     * 该服务中的端点路径的第一部分表示正在尝试调用的服务（organizationservice）。如下阐明了该映射的实际操作。
     * （1）organizationservice：服务名称充当服务网关查找服务物理位置的键。
     * （2）v1/organizations：路径的其余部分是将要调用的实际 URL 端点。
     *
     * PS：Zuul 将使用 organizationservice 应用程序名称来将请求映射到组织服务实例。
     *
     * 使用带有 Eureka 的 Zuul 的优点在于，开发人员不仅可以拥有一个可以发出调用的单个端点，有了 Eureka，开发人员还可
     * 以添加和删除服务的实例，而无须修改 Zuul。例如，可以向 Eureka 添加新服务，Zuul 将自动路由到该服务，因为 Zuul
     * 会与 Eureka 进行通信，了解实际服务端点的位置。
     *
     * 如果要查看由 Zuul 服务器管理的路由，可以通过 Zuul 服务器上的 /routes 端点来访问这些路由，这将返回服务中所有
     * 映射的列表（可访问 http://localhost:5555/routes）。
     *
     * 从输出结果可知，通过 Zuul 注册的服务的映射展示在从 /route 调用返回的 JSON 体的左边，路由映射到的实际 Eureka
     * 服务 ID 展示在其右边。其中：
     * （1）左边：Zuul 中的服务路由是基于 Eureka 的服务 ID 自动创建的。
     * （2）右边：路由所映射的 Eureka 服务 ID。
     *
     * PS：在 Eureka 中映射的每个服务现在都将被映射为 Zuul 路由。
     *
     *
     *
     * 2、使用服务发现手动映射路由
     *
     * Zuul 允许开发人员更细粒度地明确定义路由映射，而不是单纯依赖服务的 Eureka 服务 ID 创建的自动路由。
     *
     * 假设开发人员希望通过缩短组织名称来简化路由，而不是通过默认路由 /organizationservice/v1/organizations
     * /{organization-id} 在 Zuul 中访问组织服务。开发人员可以通过在 application.yml 中手动定义路由映射来
     * 做到这一点。
     *
     * zuul:
     *   routes:
     *     organizationservice: /organization/**
     *
     * 通过添加上述配置，现在就可以通过访问 /organization/v1/organizations/{organization-id} 路由来访问
     * 组织服务了。如果再次检查 Zuul 服务器的端点，会注意到有两个条目代表组织服务。
     *
     * 第一个服务条目是在 application.yml 文件中定义的映射，如下：
     *
     * "organization/**": "organizationservice"
     *
     * 第二个服务条目是由 Zuul 根据组织服务的 Eureka ID 创建的自动映射：
     *
     * "/organizationservice/**":"organizationservice"
     *
     * 注意：在使用自动路由映射时，Zuul 只基于 Eureka 服务 ID 来公开服务，如果服务的实例没有在运行，
     * Zuul 将不会公开该服务的路由。然而，如果在没有使用 Eureka 注册服务实例的情况下，手动将路由映射
     * 到服务发现 ID，那么 Zuul 仍然会显示这条路由。如果尝试为不存在的服务调用路由，Zuul 将返回 500
     * 错误。
     *
     * 如果想要排除 Eureka 服务 ID 路由的自动映射，只提供自定义的组织服务路由，可以向 application.yml 文件
     * 添加一个额外的 Zuul 参数 ignored-services。如下代码展示了如何使用 ignored-services 属性从 Zuul
     * 完成的自动映射中排除 Eureka 服务 ID organizationservice。
     *
     * zuul:
     *   ignored-services: 'organizationservice'
     *   routes:
     *     organizationservice: /organization/**
     *
     * ignored-services 属性允许开发人员定义想要从注册中排除的 Eureka 服务 ID 的列表，该列表以逗号进行分隔。
     * 现在，再调用 /routes 端点时，应该只能看到自定义的组织服务映射。
     *
     * 如果要排除所有基于 Eureka 的路由，可以将 ignored-services 属性设置为 "*"。
     *
     * 服务网关的一种常见模式是通过使用 /api 之类的标记来为所有的服务调用添加前缀，从而区分 API 路由与内容路由。
     * Zuul 通过在 Zuul 配置中使用 prefix 属性来支持这项功能。如下在概念上勾画了这种映射前缀的样子。
     *
     * http://localhost:5555/api/organization/v1/organizations/
     *
     * 其中：
     * （1）api：让 /api 路由前缀紧接着服务的简化名称所组成的路由并不少见。
     * （2）organization：这里已经将服务映射到名称 "organization"。
     *
     * PS：通过使用前缀，Zuul 会将 /api 前缀映射到它管理的每个服务。
     *
     * 在如下代码中，将看到如何分别为组织服务和许可证服务建立特定的路由，排除所有 Eureka 生成的服务，并使用
     * /api 前缀为服务添加前缀。
     *
     * zuul:
     *   ignored-services: '*'　　⇽---　ignored-services 被设置为 *，以排除所有基于 Eureka 服务 ID
     *                                 的路由的注册
     *   prefix: /api　　⇽---　所有已定义的服务都将添加前缀 /api
     *   routes:
     *     organizationservice: /organization/**　　⇽---　organizationservice 和 licensingservice
     *                                                   分别映射到 organization 和 licensing
     *     licensingservice: /licensing/**
     *
     * 完成此配置并重新加载 Zuul 服务后，访问 /routes 端点时应该会看到以下两个条目：
     * （1）/api/organization
     * （2）/api/licensing
     *
     * 下面来看看如何使用 Zuul 来映射到静态 URL。静态 URL 是指向未通过 Eureka 服务发现引擎注册的服务的 URL。
     *
     *
     *
     * 3、使用静态 URL 手动映射路由
     *
     * Zuul 可以用来路由那些不受 Eureka 管理的服务。在这种情况下，可以建立 Zuul 直接路由到一个静态定义的 URL。例如，
     * 假设许可证服务是用 Python 编写的，并且仍然希望通过 Zuul 进行代理，那么可以使用如下代码中的 Zuul 配置来达到此
     * 目的。
     *
     * zuul:
     *  routes:
     *    licensestatic:　　⇽---　Zuul 用于在内部识别服务的关键字
     *      path: /licensestatic/**　　⇽---　许可证服务的静态路由
     *      url: http://licenseservice-static:8081　　⇽---　已建立许可证服务的静态实例，它将被直接调用，而不是
     *                                                     由 Zuul 通过 Eureka 调用
     *
     * 完成这一配置更改后，就可以访问 /routes 端点来看添加到 Zuul 的静态路由。输出结果中包含：
     *
     * "/api/licensestatic/**": "http://licenseservice-static:8081"
     *
     * 现在，licensestatic 端点不再使用 Eureka，而是直接将请求路由到 http://licenseservice-static:8081 端点。
     * 这里存在一个问题，那就是通过绕过 Eureka，只有一条路径可以用来指向请求。幸运的是，开发人员可以手动配置 Zuul
     * 来禁用 Ribbon 与 Eureka 集成，然后列出 Ribbon 将进行负载均衡的各个服务实例。如下代码展示了这一点。
     *
     * zuul:
     *   routes:
     *     licensestatic:
     *       path: /licensestatic/**
     *       serviceId: licensestatic　　⇽---　定义一个服务 ID，该服务 ID 将用于在 Ribbon 中查找服务
     * ribbon:
     *   eureka:
     *     enabled: false　　⇽---　在 Ribbon 中禁用 Eureka 支持
     * licensestatic:
     *   ribbon:
     *     listOfServers: http://licenseservice-static1:8081,
     *       http://licenseservice-static2:8082　　⇽---　指定请求会路由到的服务器列表
     *
     * 配置完成后，调用 /routes 端点现在将显示 /api/licensestatic 路由已被映射到名为 licensestatic 的服务 ID。
     * 输入结果中包含：
     *
     * "/api/licensestatic/**": "licensestatic"
     *
     *
     * PS：处理非 JVM 服务
     *
     * 静态映射路由并在 Ribbon 中禁用 Eureka 支持会造成一个问题，那就是禁用了对通过 Zuul 服务网关
     * 运行的所有服务的 Ribbon 支持。这意味着 Eureka 服务器将承受更多的负载，因为 Zuul 无法使用
     * Ribbon 来缓存服务的查找。记住，Ribbon 不会在每次发出调用的时候都调用 Eureka。相反，它将在
     * 本地缓存服务实例的位置，然后定期检查 Eureka 是否有变化。缺少了 Ribbon，Zuul 每次需要解析服
     * 务的位置时都会调用 Eureka。
     *
     * 之前讨论了如何使用多个服务网关，根据所调用的服务类型来执行不同的路由规则和策略。对于非 JVM 应
     * 用程序，可以建立单独的 Zuul 服务器来处理这些路由。然而，对于非基于 JVM 的语言，最好是建立一
     * 个 Spring Cloud "Sidecar" 实例。Spring Cloud Sidecar 允许开发人员使用 Eureka 实例注册
     * 非 JVM 服务，然后通过 Zuul 进行代理。
     *
     *
     *
     * 4、动态重新加载路由配置
     *
     * 接下来要在 Zuul 中配置路由来看看如何动态重新加载路由。动态重新加载路由的功能非常有用，因为它允许在不回收 Zuul
     * 服务器的情况下更改路由的映射。现有的路由可以被快速修改，以及添加新的路由，都无需在环境中回收每个 Zuul 服务器。
     * 之前如何使用 Spring Cloud 配置服务来外部化微服务配置数据。可以使用 Spring Cloud Config 来外部化 Zuul 路
     * 由。在 EagleEye 示例中，可以在 config-repo 创建一个名为 zuulservice 的新应用程序文件夹。就像组织服务和许
     * 可证服务一样，这里将创建三个文件（即 zuulservice.yml、zuulservice-dev.yml 和 zuulservice-prod.yml），
     * 它们将保存路由配置。
     *
     * PS：config-repo 链接：https://github.com/carnellj/config-repo
     *
     * 为了与之前配置中的示例保持一致，这里已经将路由格式从层次化格式更改为 "." 格式。初始的路由配置将包含一个条目：
     *
     * zuul.prefix=/api
     *
     * 如果访问 /routes 端点，应该会看到在 Zuul 中显示的所有基于 Eureka 的服务，并带有 /api 的前缀。现在，如果想
     * 要动态地添加新的路由映射，只需对配置文件进行更改，然后将配置文件提交回 Spring Cloud Config 从中提取配置数据
     * 的 Git 存储库。例如，如果想要禁用所有基于 Eureka 的服务注册，并且只公开两个路由（一个用于组织服务，另一个用
     * 于许可证服务），则可以修改 zuulservice-*.yml 文件，如下所示：
     *
     * zuul.ignored-services: '*'
     * zuul.prefix: /api
     * zuul.routes.organizationservice: /organization/**
     * zuul.routes.organizationservice: /licensing/**
     *
     * 接下来，将更改提交给 GitHub。Zuul 公开了基于 POST 的端点路由 /refresh，其作用是让 Zuul 重新加载路由配置。
     * 在访问完 refresh 端点之后，如果访问 /routes 端点，就会看到两条新的路由，所有基于 Eureka 的路由都不见了。
     *
     *
     *
     * 5、Zuul 和服务超时
     *
     * Zuul 使用 Netflix 的 Hystrix 和 Ribbon 库，来帮助防止长时间运行的服务调用影响服务网关的性能。在默认情况下，
     * 对于任何需要用超过 1 s 的时间（这是 Hystrix 默认值）来处理请求的调用，Zuul 将终止并返回一个 HTTP 500 错误。
     * 幸运的是，开发人员可以通过在 Zuul 服务器的配置中设置 Hystrix 超时属性来配置此行为。
     *
     * 开发人员可以使用 hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds 属性来为
     * 所有通过 Zuul 运行的服务设置 Hystrix 超时。例如，如果要将默认的 Hystrix 超时设置为 2.5 s，就可以在 Zuul
     * 的 Spring Cloud 配置文件中使用以下配置：
     *
     * zuul.prefix:  /api
     * zuul.routes.organizationservice: /organization/**
     * zuul.routes.licensingservice: /licensing/**
     * zuul.debug.request: true
     * hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds: 2500
     *
     * 如果需要为特定服务设置 Hystrix 超时，可以使用需要覆盖超时的服务的 Eureka 服务 ID 名称来替换属性的 default
     * 部分。例如，如果想要将 licensingservice 的超时更改为 3 s，并让其他服务使用默认的 Hystrix 超时，可以在配置
     * 中添加与下面类似的内容：
     *
     * hystrix.command.licensingservice.execution.isolation.thread.timeoutInMilliseconds: 3000
     *
     * 最后，还需知晓另外一个超时属性。
     *
     * 虽然已经覆盖了 Hystrix 的超时，Netflix Ribbon 同样会超时任何超过 5 s 的调用。尽管这里强烈建议重新审视调用
     * 时间超过 5 s 的调用的设计，但可以通过设置属性 servicename.ribbon.ReadTimeout 来覆盖 Ribbon 超时。例如，
     * 如果想要覆盖 licensingservice 超时时间为 7 s，可以使用以下配置：
     *
     * hystrix.command.licensingservice.execution.isolation.thread.timeoutInMilliseconds: 7000
     * licensingservice.ribbon.ReadTimeout: 7000
     *
     * 注意：对于超过 5 s 的配置，必须同时设置 Hystrix 和 Ribbon 超时。
     */
    public static void main(String[] args) {

    }

}
