package com.siwuxie095.spring.cloud.chapter6th.example4th;

/**
 * @author Jiajing Li
 * @date 2021-06-10 21:29:55
 */
public class Main {

    /**
     * 在 Zuul 中配置路由
     *
     * Zuul 本质上是一个反向代理。反向代理是介于客户端试图到达资源和资源本身之间的中间服务器。客户端甚至不知道
     * 它与代理以外的服务器通信。反向代理负责捕获客户端的请求，然后以客户端的名义调用进程资源。
     *
     * 在一个微服务架构的情况下，Zuul（反向代理）以微服务调用从客户端转发到下游服务。服务客户端认为它只与 Zuul
     * 通信。对于 Zuul 与下游客户端通信，Zuul 已经知道如何将传入的调用映射到下游路由。Zuul 有这样几种机制，
     * 包括：
     * （1）通过服务发现自动映射路由
     * （2）通过服务发现手动映射路由
     * （3）使用静态 URL 手动映射路由
     *
     *
     *
     * 1、通过服务发现自动映射路由
     *
     * Zuul 所有路由映射通过 application.yml 文件定义。然而，Zuul 可以基于零配置服务 ID 自动路由请求。如果
     * 你不指定任何路由，Zuul 将自动使用被调用服务的 Eureka 服务 ID 和将它映射到下游服务实例。例如，如果你想
     * 调用你的组织服务和通过 Zuul 使用自动路由，你将会有你的客户端调用 Zuul 服务实例，使用下面的 URL 作为端
     * 点：
     *
     * http://localhost:5555/organizationservice/v1/organizations/e254f8c-c442-4ebea82a-e2fc1d1ff78a
     *
     * 通过 http://localhost:5555 访问你的 Zuul 服务器。你试图调用的服务（organizationservice）由服务中
     * 端点路径的第一部分表示。
     *
     * Zuul 将使用 organizationservice 应用程序名称来映射到组织服务实例的请求。即：
     * （1）服务名称充当服务网关查找服务的物理位置的键。
     * （2）路径的其余部分是将调用的实际 URL 端点。
     *
     * 使用带 Eureka 的 Zuul 的优点是，你不仅现在有一个单一的，你可以调用的端点，使用 Eureka，你还可以添加和
     * 删除一个服务实例而不必修改 Zuul。例如，你可以添加一个新的服务到 Eureka，Zuul 将自动路由到它因为它与
     * Eureka 通信，Eureka 知道实际物理服务端点位于何处。
     *
     * 如果你想看到路由被 Zuul 服务器管理，你可以通过在 Zuul 服务器上的 /routes 端点访问该路由。这将返回服务
     * 上所有映射的列表。可以点击 http://localhost:5555/route 来查看输出。
     *
     * 映射在 Eureka 的每个服务现在将被映射为一个 Zuul 路由。分为双向的两部分：
     * （1）基于 Eureka 服务 ID 自动创建 Zuul 服务路由。
     * （2）路由映射到 Eureka 服务 ID。
     *
     *
     *
     * 2、通过服务发现手动映射路由
     *
     * Zuul 允许你通过明确的定义路由映射来定义更细粒度的路由，而不仅仅是依靠自动路由服务创建服务的 Eureka 服务
     * ID。假如你想通过缩短组织的名字而不是你的组织服务通过默认路由 /organizationservice/v1/organizations
     * /{organizationid} 访问 Zuul 来简化路由。你可以通过在 application.yml 文件中手动定义路由映射：
     *
     * zuul:
     *   routes:
     *     organizationservice: /organization/**
     *
     * 通过添加此配置，你现在可以通过点击 /organization/v1/organizations/{organization-id} 路由来访问组
     * 织服务。如果你再检查 Zuul 服务器的端点（http://localhost:5555/route），可以看到其中包含的组织服务的
     * 自定义路由。
     *
     * 如果仔细查看，你会发现组织服务中有两个条目。第一个服务入口是你定义的 application.yml 文件的映射：
     * "organization/**": "organizationservice"。第二个服务入口是通过 Zuul 创建基于组织服务的
     * Eureka ID 的自动映射："/organizationservice/**": "organizationservice"。
     *
     * 注意：当你使用自动路由映射在 Zuul 暴露完全基于 Eureka 服务 ID 的服务，如果没有服务实例在运行，Zuul 不
     * 会暴露服务路由。然而，如果你手动映射路由到服务发现 ID 且没有实例在 Eureka 注册，Zuul 将仍然显示该路由。
     * 如果你尝试调用不存在的服务路由，Zuul 会返回一个 500 错误。
     *
     * 如果你想排除 Eureka 服务 ID 路由的自动映射和你已定义的可用的组织服务路由，你可以添加一个额外的 Zuul 参
     * 数到你的 application.yml 文件，称为 ignored-services。下面的代码片段显示了 ignored-services 属性
     * 可以用来排除通过 Zuul 自动映射的 Eureka 服务 ID "organizationservice"。
     *
     * zuul:
     *   ignored-services: 'organizationservice'
     *   routes:
     *     organizationservice: /organization/**
     *
     * 现在仅有一个组织服务定义在 Zuul。
     *
     * ignored-services 属性允许你定义一个以逗号分隔的 Eureka 服务 ID 列表，你希望将其排除在注册之外。现在，
     * 当你调用 Zuul 的 /routes 端点，你只能看到你定义的组织服务映射。
     *
     * 如果你想排除所有基于 Eureka 的路由，可以将 ignored-services 属性设置为 "*"。
     *
     * 区分服务网关 API 路由与内容路由的一个帯见的模式是通过使用一类标签的所有服务调用前缀，如：/api，来区别。
     * Zuul 通过在 Zuul 配置中使用前缀属性支持上述情况。
     *
     * 使用前缀，Zuul 将映射 /api 前缀到每个它管理的服务。在下面的代码中，将看到如何为你的组织服务和许可服务设
     * 置特定的路由，排除所有 Eureka 生成的服务，并用 /api 前缀作为服务的前缀。
     *
     * zuul:
     *   ignored-services: '*'
     *   prefix:  /api
     *   routes:
     *     organizationservice: /organization/**
     *     licensingservice: /licensing/**
     *
     * 一旦配置完成，Zuul 服务已经重新加载，当点击 /route 端点时你应该看到这样两项：/api/organization 和
     * /api/licensing。
     *
     * 下面来看看如何使用 Zuul 映射到静态 URL。静态 URL 是指向服务但没有在 Eureka 服务发现引擎注册的 URL。
     *
     *
     *
     * 3、使用静态 URL 手动映射路由
     *
     * Zuul 可用于路由服务，但它不受 Eureka 管理。在这些情况下，Zuul 可建立直接路由到一个静态定义的 URL。例如，
     * 假设你的许可服务是用 Python 写的，你还想通过 Zuul 代理。你会在下面的代码使用 Zuul 配置来实现这一需求。
     *
     * zuul:
     *   routes:
     *     licensestatic:
     *       path: /licensestatic/**
     *       url: http://licenseservice-static:8081
     *
     * 你现在已经映射一个静态路由到许可服务。你可以点击 /routes 端点，并看到已增加到 Zuul 的静态路由。
     *
     * 在这一点上，licensestatic 端点不使用 Eureka 并且直接路由请求到 http://licenseservice-static:8081
     * 端点。问题是通过绕过 Eureka，你只能有一个指向请求的路由。幸运的是，你可以手动配置 Zuul 禁用 Ribbon 与
     * Eureka 整合，然后列出 Ribbon 将负载均衡每个服务实例。下面的清单显示了这一点。
     *
     * zuul:
     *   routes:
     *     licensestatic:
     *      path: /licensestatic/**
     *      serviceId: licensestatic
     * ribbon:
     *   eureka:
     *     enabled: false
     * licensestatic:
     *   ribbon:
     *     listOfServers: http://licenseservice-static1:8081, http://licenseservice-static2:8082
     *
     * 一旦配置完成，对 /routes 端点的调用现在显示 /api/licensestatic 路由已经被映射到一个称为 licensestatic
     * 的服务 ID。
     *
     * PS：处理非 JVM 服务
     *
     * 静态路由映射的问题和在 Ribbon 中禁用 Eureka 支持，将禁用通过 Zuul 服务网关的所有服务的 Ribbon 支持。
     * 这意味着更多的负载将被放置在你的 Eureka 服务器，因为 Zuul 不能使用 Ribbon 缓存查找服务。记住，Ribbon
     * 不会每次都调用 Eureka。相反，它在本地缓存服务实例的位置，然后定期对 Eureka 的更改进行检查。在 Ribbon
     * 缺席的情况下，Zuul 将每次调用 Eureka 来解析服务的位置。
     *
     * 之前谈到了如何使用多个服务网关，根据所调用的服务类型，将执行不同的路由规则和策略。非 JVM 的应用程序，你
     * 可以建立一个单独的 Zuul 服务器来处理这些路由。然而，与非基于 JVM 的语言，你最好设置一个 Spring Cloud
     * "Sidecar" 实例。Spring Cloud sidecar 让你使用 Eureka 实例注册非 JVM 服务，然后通过 Zuul 代理它们。
     * 这里没有涵盖 Spring Sidecar，因为你没有写任何非 JVM 服务，但是设置 Sidecar 实例非常容易。
     *
     *
     *
     * 4、动态重新加载路由配置
     *
     * 下面来看看在在 Zuul 配置路由的下一件事是如何动态重新加载路由。动态重新加载路由的能力非常有用，因为它允许
     * 你改变路由的映射而不必回收 Zuul 服务器。现有的路由可以被快速修改，并通过你的环境中的每个 Zuul 服务器的
     * 回收行为添加新的路由。之前讨论了如何使用 Spring Cloud 配置服务来呈现微服务配置数据。你可以使用 Spring
     * Cloud 配置呈现 Zuul 路由。在 EagleEye 的例子，你可以在你的配置仓库创建一个称为 zuulservice 的新的应
     * 用程序文件夹。像你的组织服务和许可服务，你将创建三个文件: zuulservice.yml， zuulservicedev.yml 和
     * zuulservice-prod.yml，它们将保存你的路由配置。
     *
     * PS：配置仓库：http://github.com/carnellj/config-repo
     *
     * 为了与之前配置中的示例一致，这里更改了路由格式，将其从分层格式调整到 "." 格式。初始路由配置中只有一个条目：
     *
     * zuul.prefix=/api
     *
     * 如果你点击 /routes 端点，你应该看到当前显示在 Zuul，用 /api 作为前缀的所有基于 Eureka 的服务。现在，
     * 如果你想立即添加新的路由映射，你只需对配置文件进行更改，然后将它们提交给 Git 仓库，其中 Spring Cloud
     * 配置正在从配置数据库中提取配置数据。例如，如果你想禁用所有基于 Eureka 服务注册并且只暴露两个路由（组织
     * 服务和许可服务），你可以像这样修改 zuulservice-*.yml 文件：
     *
     * zuul.ignored-services: '*'
     * zuul.prefix: /api
     * zuul.routes.organizationservice: /organization/**
     * zuul.routes.organizationservice: /licensing/**
     *
     * 然后你可以向 GitHub 提交修改。Zuul 以 POST 方式暴露 /refresh 端点路由，该端点会重新加载其路由的配置。
     * 一旦这个 /refresh 被点击，如果你再点击 /routes 端点，你将会看到两个新的路由被暴露，并且所有基于 Eureka
     * 的路由都消失了。
     *
     *
     *
     * 5、Zuul 和服务超时
     *
     * Zuul 使用 Netflix 的 Hystrix 和 Ribbon 库来帮助防止长时间运行的服务调用影响服务网关的性能。默认情况
     * 下，如果任何调用处理一次请求需要消耗超过 1 秒（Hystrix 的默认值），Zuul 将中断调用并返回 HTTP 500 错
     * 误。幸运的是，你可以通过在 Zuul 服务器的配置中设置 Hystrix 超时属性来配置这样的行为。
     *
     * 为经过 Zuul 的所有服务设置 Hystrix 超时时间，你可以使用 hystrix.command.default.execution
     * .isolation.thread.timeoutInMilliseconds 属性。例如，如果你想设置默认 Hystrix 时间为 2.5
     * 秒，你可以在你的 Zuul 的 Spring Cloud 配置文件使用以下配置：
     *
     * zuul.prefix: /api
     * zuul.routes.organizationservice: /organization/**
     * zuul.routes.licensingservice: /licensing/**
     * zuul.debug.request: true
     * hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds: 2500
     *
     * 如果需要为特定服务设置 Hystrix 超时，则可以使用你想要覆盖的超时时间，替换服务的 Eureka 服务 ID 名称属
     * 性的默认部分。例如，如果你想修改仅有 licensingservice 的超时时间为 3 秒，并且让其它剩余的服务使用默认
     * Hystrix 超时时间，你可以使用这样的配置：
     *
     * hystrix.command.licensingservice.execution.isolation.thread.timeoutInMilliseconds: 3000
     *
     * 最后，你需要知道另一个超时属性。虽然你已经覆盖了 Hystrix 超时时间，但 Netflix 的 Ribbon 对任何耗时超
     * 过 5 秒的调用也会超时。
     *
     * 强烈建议你重新审视任何耗时超过 5 秒的调用的设计，你可以通过设置以下属性覆盖 Ribbon 超时时间：
     * servicename.ribbon.ReadTimeout。例如，如果你想覆盖 licensingservice 的超时时间为 7 秒，
     * 你会使用以下配置：
     * hystrix.command.licensingservice.execution.isolation.thread.timeoutInMilliseconds: 7000
     * licensingservice.ribbon.ReadTimeout: 7000
     *
     * 注意：配置的时间超过 5 秒，你必须同时设置 Hystrix 和 Ribbon 的超时时间。
     */
    public static void main(String[] args) {

    }

}
