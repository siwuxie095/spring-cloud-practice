package com.siwuxie095.spring.cloud.chapter9th.example3rd;

/**
 * @author Jiajing Li
 * @date 2021-06-24 07:22:22
 */
@SuppressWarnings("all")
public class Main {

    /**
     * 日志聚合与 Spring Cloud Sleuth
     *
     * 在大型的微服务环境中（特别是在云环境中），日志记录数据是调试问题的关键工具。但是，因为基于微服务的应用程序
     * 的功能被分解为小型的细粒度的服务，并且单个服务类型可以有多个服务实例，所以尝试绑定来自多个服务的日志数据以
     * 解决用户的问题可能非常困难。试图跨多个服务器调试问题的开发人员通常不得不尝试以下操作。
     * （1）登录到多个服务器以检查每个服务器上的日志。这是一项非常费力的任务，尤其是在所涉及的服务具有不同的事务
     * 量，导致日志以不同的速率滚动的时候。
     * （2）编写尝试解析日志并标识相关的日志条目的本地查询脚本。由于每个查询可能不同，因此开发人员经常会遇到大量
     * 的自定义脚本，用于从日志中查询数据。
     * （3）延长停止服务的进程的恢复，因为开发人员需要备份驻留在服务器上的日志。如果托管服务的服务器彻底崩溃，则
     * 日志通常会丢失。
     *
     * 上面列出的每一个问题都是遇到过的实际问题。在分布式服务器上调试问题是一件很糟糕的工作，并且常常会明显增加
     * 识别和解决问题所需的时间。
     *
     * 一种更好的方法是，将所有服务实例的日志实时流到一个集中的聚合点，在那里可以对日志数据进行索引并进行搜索。
     * 这种 "统一" 的日志记录架构在概念层面是这样工作的，如下：
     * （1）聚合机制收集所有数据并将其收集到一个公共数据存储中。
     * （2）当数据进入中心数据存储时，它以可搜索的格式被索引和存储。
     * （3）开发和运维团队可以查询日志数据以找到单个事务。来自 Spring Cloud Sleuth 日志条目的跟踪 ID 可用于
     * 跨服务绑定日志条目。
     *
     * PS：将聚合日志与跨服务日志条目的唯一事务 ID （即 跟踪 ID）结合，更易于管理分布式事务的调试。
     *
     * 幸运的是，有多个开源产品和商业产品可以帮助实现前面描述的日志记录架构。此外，还存在多个实现模型，可供开发
     * 人员在内部部署、本地管理或者基于云的解决方案之间进行选择。如下总结了可用于日志记录基础设施的几个选择。
     * （1）
     * 产品名称：Elasticsearch、Logstash、Kibana（ELK）
     * 实现模式：开源、商业、通常实施于内部部署
     * 备注：通用搜索引擎；可以通过 ELK 技术栈进行日志聚合；需要最多的手工操作
     * （2）
     * 产品名称：Graylog
     * 实现模式：开源、商业、通常实施于内部部署
     * 备注：设计为在内部安装的开源平台
     * （3）
     * 产品名称：Splunk
     * 实现模式：仅限于商业、内部部署和基于云
     * 备注：最古老且最全面的日志管理和聚合工具；最初是内部部署的解决方案, 但后来提供了云服务
     * （4）
     * 产品名称：Sumo Logic
     * 实现模式：免费增值模式、商业、基于云
     * 备注：免费增值模式/分层定价模型；仅作为云服务运行；需要用公司的工作账户去注册
     * （不能是 Gmail 或 Yahoo 账户）
     * （5）
     * 产品名称：Papertrail
     * 实现模式：免费增值模式、商业、基于云
     * 备注：免费增值模式/分层定价模型；仅作为云服务运行
     *
     * 很难从上面选出哪个是最好的。每个组织都各不相同，并且有不同的需求。
     *
     * 在这里，将以 Papertrail 为例，介绍如何将 Spring Cloud Sleuth 支持的日志集成到统一的日志记录平台中。
     * 选择 Papertrail 出于以下三个原因。
     * （1）它有一个免费增值模式，可以注册一个免费的账户。
     * （2）它非常容易创建，特别是和 Docker 这样的容器运行时工作。
     * （3）它是基于云的。这里虽然认为良好的日志基础设施对于微服务应用程序是至关重要的，但不认为大多数组织都有
     * 时间或技术才能去正确地创建和管理一个日志记录平台。
     *
     *
     *
     * 1、Spring Cloud Sleuth 与 Papertrail 集成实战
     *
     * 下面来看看如何使用 Spring Cloud Sleuth 和 Papertrail 来实现一个和上面介绍相同的通用的统一日志架构。
     *
     * 为了让 Papertrail 与这里的环境一起工作，必须采取以下措施。
     * （1）创建一个 Papertrail 账户并配置一个 Papertrail syslog 连接器。
     * （2）定义一个 Logspout Docker 容器，以从所有 Docker 容器捕获标准输出。
     * （3）通过基于来自 Spring Cloud Sleuth 的关联 ID 发出查询来测试这一实现。
     *
     * 如下展示了这一实现的最终状态，以及 Spring Cloud Sleuth 和 Papertrail 如何与解决方案融合。
     * （1）单个容器将其日志数据写入标准输出。它们的配置没有任何变化。
     * （2）在 Docker 中，所有容器都将它们的标准输出写入名为 Docker.sock 的内部文件系统中。
     * （3）Logspout Docker 容器监听 Docker.sock，并将标准输出写入远程 syslog 位置。
     * （4）Papertrail 公开了一个特定于用户应用程序的 syslog 端口。它接收传入的日志数据，并对日志
     * 数据建立索引并存储。
     * （5）Papertrail Web 应用程序允许用户发出查询。在这里，用户可以输入 Spring Cloud Sleuth
     * 的跟踪 ID，以查看来自不同服务的所有含有该跟踪 ID 的日志条目。
     *
     * PS：使用原生 Docker 功能、Logspout 和 Papertrail 可以快速实现统一的日志记录架构。
     *
     *
     *
     * 2、创建 Papertrail 账户并配置 syslog 连接器
     *
     * 这里将从创建一个 Papertrail 账号开始。要开始使用 PaperTrail，应访问 https://papertrailapp.com 并
     * 点击绿色的 "Start Logging-Free Plan" 按钮。
     *
     * Papertrail 不需要大量的信息去启动，只需要一个有效的电子邮箱地址即可。填写完账户信息后, 将出现一个界面，
     * 用于创建记录数据的第一个系统。
     *
     * 在默认情况下，Papertrail 允许开发人员通过 Syslog 调用向它发送日志数据。Syslog 是源于 UNIX 的日志消息
     * 传递格式，它允许通过 TCP 和 UDP 发送日志消息。Papertrail 将自动定义一个 Syslog 端口，可以使用它来写入
     * 日志消息。在这里将使用这个默认端口。点击 "Add your first system" 按钮时，syslog 连接字符串将自动生成。
     *
     * 到目前为止，已经设置完 Papertrail。接下来必须配置 Docker环境，以便将运行服务的每个容器的输出捕获到远程
     * syslog 端点。
     *
     * 注意：你需要确保自己使用了 Papertrail 为自己生成的连接字符串，或者通过 Papertrail Settings -> Log
     * destinations 菜单选项来定义一个连接字符串。
     *
     *
     *
     * 3、将 Docker 输出重定向到 Papertrail
     *
     * 通常情况下，如果在虚拟机中运行每个服务，那么必须配置每个服务的日志记录配置，以便将它的日志信息发送到一个
     * 远程 syslog 端点（如通过 Papertrail 公开的那个端点）。
     *
     * 幸运的是，Docker 让从物理机或虚拟机上运行的 Docker 容器中捕获所有输出变得非常容易。Docker 守护进程通
     * 过一个名为 docker.sock 的 Unix 套接字来与所有 Docker 容器进行通信。在 Docker 所在的服务器上，每个
     * 容器都可以连接到 docker.sock，并接收由该服务器上运行的所有其他容器生成的所有消息。用最简单的术语来说，
     * docker.sock 就像一个管道，容器可以插入其中，并捕获 Docker 运行时环境中进行的全部活动，这些 Docker
     * 运行时环境是在 Docker 守护进程运行的虚拟服务器上的。
     *
     * 这里将使用一个名为 Logspout 的 "Docker 化" 软件，它会监听 docker.sock 套接字，然后捕获在 Docker
     * 运行时生成的任意标准输出消息，并将它们重定向输出到远程 syslog（Papertrail）。要建立 Logspout 容器，
     * 必须要向 docker-compose.yml 文件添加一个条目，它用于启动这里代码示例使用的所有 Docker 容器。需要
     * 修改 docker/common/docker-compose.yml 文件以添加以下条目：
     *
     * logspout:
     *   image: gliderlabs/logspout
     *   command: syslog://logs5.papertrailapp.com:21218
     *   volumes:
     *     - /var/run/docker.sock:/var/run/docker.sock
     *
     * 注意：在上面的代码片段中，你需要将 command 属性中的值替换为 Papertrail 提供的值。如果你使用上述
     * Logspout 代码片段，Logspout 容器会很乐意将日志条目写入这里的 Papertrail 账户。
     *
     * 现在，当你启动这里的 Docker 环境时，所有发送到容器标准输出的数据都将发送到 Papertrail。在启动完这里的
     * Docker 示例之后，你通过登录自己的 Papertrail 账户，然后点击界面右上角的 "Events" 按钮，就可以看到数
     * 据都发送到 Papertrail。
     *
     * 总结：在定义了 Logspout Docker 容器的情况下，写入每个容器标准输出的数据将被发送到 Papertrail。即 单
     * 个服务的日志事件被写入容器的标准输出中，容器中的标准输出由 Logspout 捕获，然后发送到 Papertrail。
     *
     *
     * PS：为什么不使用 Docker 日志驱动程序
     *
     * Docker 1.6 及更高版本允许开发人员定义其他日志驱动程序，以记录在每个容器中写入的 stdout/stderr 消息。
     * 其中一个日志记录驱动程序是 syslog 驱动程序，它可用于将消息写入远程 syslog 监听器。
     *
     * 为什么这里会选择 Logspout 而不是使用标准的 Docker 日志驱动程序？主要原因是灵活性。Logspout 提供了
     * 定制日志数据发送到日志聚合平台的功能。Logspout 提供的功能有以下几个。
     * （1）能够一次将日志数据发送到多个端点：许多公司都希望将自己的日志数据发送到一个日志聚合平台，同时还需要
     * 安全监控工具，用于监控生成的日志中的敏感数据。
     * （2）在一个集中的位置过滤哪些容器将发送它们的日志数据：使用 Docker 驱动程序，开发人员需要在 docker
     * -compose.yml 文件中为每个容器手动设置日志驱动程序，而 Logspout 则允许开发人员在集中式配置中定义特定
     * 容器甚至特定字符串模式的过滤器。
     * （3）自定义 HTTP 路由，允许应用程序通过特定的 HTTP 端点来写入日志信息：这个特性允许开发人员完成一些事
     * 情，例如将特定的日志消息写入特定的下游日志聚合平台。举个例子，开发人员可能会将一般的日志消息从 stdout
     * /stderr 转到 Papertrail，与此同时，可能会希望将特定应用程序审核信息发送到内部的 Elasticsearch 服务
     * 器。
     * （4）与 syslog 以外的协议集成：Logspout 可以通过 UDP 和 TCP 协议发送消息。此外，Logspout 还具有第
     * 三方模块，可以将 Docker 的 stdout/stderr 整合到 Elasticsearch 中。
     *
     *
     *
     * 4、在 Papertrail 中搜索 Spring Cloud Sleuth 的跟踪 ID
     *
     * 现在，日志正在流向 Papertrail，可以真正开始感激 Spring Cloud Sleuth 将跟踪 ID 添加到所有日志条目
     * 中。要查询与单个事务相关的所有日志条目，只需在 Papertrail 的事件界面的查询框中输入跟踪 ID 并进行查询
     * 即可。
     *
     * 跟踪 ID 可用于筛选与单个事务相关的所有日志条目。
     *
     *
     * PS：统一日志记录和对平凡的赞美
     *
     * 不要低估拥有一个统一的日志架构和服务关联策略的重要性。这似乎是一项平凡的任务，但这里曾使用了类似于
     * Papertrail 的日志聚合工具为正在开发的一个项目跟踪三个不同服务之间的竞态条件。事实表明，这个竞态
     * 条件已经存在了一年多时间了，但处于竞态条件下的服务一直运行良好，直到增加了一点儿负载并加入另一个参
     * 与者才导致问题出现。
     *
     * 经过了 1.5 周的时间进行日志查询，并遍历了几十个独特场景的跟踪输出之后才发现了这个问题。如果没有聚
     * 合的日志记录平台，也就不会发现这个问题。这次经历再次肯定了以下几件事。
     * （1）确保在服务开发的早期定义和实现日志策略：一旦项目开展起来，实现日志基础设施会是一项冗长的、有
     * 时很困难的工作并且还会耗费大量时间。
     * （2）日志记录是微服务基础设施的一个关键部分：在实现你自己的日志记录方案或是尝试实现内部部署的日志
     * 记录方案之前，一定要再三考虑清楚。花在基于云的日志记录平台上的钱是值得的。
     * （3）学习日志记录工具：几乎每个日志平台都有一个查询语言来查询合并的日志。日志是信息和度量的一个极
     * 其重要的来源。它们本质上是另一种类型的数据库，花在学习查询上的时间将会带来巨大的回报。
     *
     *
     *
     * 5、使用 Zuul 将关联 ID 添加到 HTTP 响应
     *
     * 如果你检查使用 Spring Cloud Sleuth 进行服务调用所返回的 HTTP 响应，永远不会看到在调用中使用的跟踪 ID
     * 在 HTTP 响应首部中返回。通过查阅 Spring Cloud Sleuth 的文档，就会得知 Spring Cloud Sleuth 团队认
     * 为返回的跟踪数据可能是一个潜在的安全问题（尽管他们没有明确列出理由）。
     *
     * 然而，在调试问题时，在 HTTP 响应中返回关联 ID 或跟踪 ID 是非常重要的。Spring Cloud Sleuth 允许开发
     * 人员使用其跟踪 ID 和跨度 ID "装饰" HTTP 响应信息。然而，这种做法涉及编写三个类并注入两个定制的 Spring
     * bean。如果你想采取这种方法，可以查阅 Spring Cloud Sleuth 文档。一个更简单的解决方案是编写一个将在
     * HTTP 响应中注入跟踪 ID 的 Zuul 后置过滤器。
     *
     * 之前 Zuul API 网关时，看到了如何构建一个 Zuul 后置响应过滤器，将生成的用于服务的关联 ID 添加到调用者
     * 返回的 HTTP 响应中。现在要修改这个过滤器以添加 Spring Cloud Sleuth 首部。
     *
     * 要创建 Zuul 响应过滤器，需要将 JAR 依赖项 spring-cloud-starter-sleuth 添加到 Zuul 服务器的
     * pom.xml 文件中。spring-cloud-starter-sleuth 依赖项用于告诉 Spring Cloud Sleuth，希望 Zuul
     * 参与 Spring Cloud 跟踪。后续介绍 Zipkin 时，你会看到 Zuul 服务将成为所有服务调用中的第一个调用。
     *
     * spring-cloud-starter-sleuth 依赖项如下：
     *
     * <dependency>
     *   <groupId>org.springframework.cloud</groupId>
     *   <artifactId>spring-cloud-starter-sleuth</artifactId>　　
     * </dependency>
     *
     * 添加完新的依赖项，实际的 Zuul 后置过滤器就很容易实现了。如下代码展示了用于构建 Zuul 过滤器的源代码。
     *
     * @Component
     * public class ResponseFilter extends ZuulFilter {
     *
     *     private static final int  FILTER_ORDER = 1;
     *     private static final boolean  SHOULD_FILTER = true;
     *     private static final Logger logger = LoggerFactory.getLogger(ResponseFilter.class);
     *
     *
     *     @Autowired
     *     Tracer tracer;
     *
     *     @Override
     *     public String filterType() {
     *         return "post";
     *     }
     *
     *     @Override
     *     public int filterOrder() {
     *         return FILTER_ORDER;
     *     }
     *
     *     @Override
     *     public boolean shouldFilter() {
     *         return SHOULD_FILTER;
     *     }
     *
     *     @Override
     *     public Object run() {
     *         RequestContext ctx = RequestContext.getCurrentContext();
     *         ctx.getResponse().addHeader("tmx-correlation-id",
     *            tracer.getCurrentSpan().traceIdString());
     *         return null;
     *     }
     *
     * }
     *
     * 因为 Zuul 现在已经启用了 Spring Cloud Sleuth，所以可以通过自动装配 Tracer 类到 ResponseFilter
     * 从 ResponseFilter 中访问跟踪信息。Tracer 类可用于访问正在执行的当前 Spring Cloud Sleuth 跟踪
     * 信息。tracer.getCurrentSpan().traceIdString() 方法以字符串的形式检索当前正在进行的事务的跟踪 ID。
     *
     * 将跟踪 ID 添加到通过 Zuul 的传出 HTTP 响应是很简单的。这一步骤通过调用以下代码来完成：
     *
     * RequestContext ctx = RequestContext.getCurrentContext();
     * ctx.getResponse().addHeader("tmx-correlation-id",
     *    tracer.getCurrentSpan().traceIdString());
     *
     * 有了这段代码，如果通过 Zuul 网关调用了一个 EagleEye 微服务，那么应该会得到一个名为
     * tmx-correlation-id 的 HTTP 响应首部。
     */
    public static void main(String[] args) {

    }

}
