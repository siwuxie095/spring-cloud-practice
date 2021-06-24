package com.siwuxie095.spring.cloud.chapter9th.example4th;

/**
 * @author Jiajing Li
 * @date 2021-06-24 21:28:00
 */
@SuppressWarnings("all")
public class Main {

    /**
     * 使用 Open Zipkin 进行分布式跟踪
     *
     * 具有关联 ID 的统一日志记录平台是一个强大的调试工具。但是，在这里的剩余部分中，将不再关注如何跟踪日志条目，而是
     * 关注如何跨不同微服务可视化事务流。一张干净简洁的图片比一百万条日志条目有用。
     *
     * 分布式跟踪涉及提供一张可视化的图片，说明事务如何流经不同的微服务。分布式跟踪工具还将对单个微服务响应时间作出粗
     * 略的估计。但是，分布式跟踪工具不应该与成熟的应用程序性能管理（Application Performance Management，APM）
     * 包混淆。这些包可以为服务中的实际代码提供开箱即用的低级性能数据，除了提供响应时间，它还能提供其他性能数据，如内
     * 存利用率、CPU 利用率和 I/O 利用率。
     *
     * 这就是 Spring Cloud Sleuth 和 OpenZipkin（也称为 Zipkin）项目的亮点。Zipkin 是一个分布式跟踪平台，可用
     * 于跟踪跨多个服务调用的事务。Zipkin 允许开发人员以图形方式查看事务占用的时间量，并分解在调用中涉及的每个微服务
     * 所用的时间。在微服务架构中，Zipkin 是识别性能问题的宝贵工具。
     *
     * 建立 Spring Cloud Sleuth 和 Zipkin 涉及四项操作：
     * （1）将 Spring Cloud Sleuth 和 Zipkin JAR 文件添加到捕获跟踪数据的服务中；
     * （2）在每个服务中配置 Spring 属性以指向收集跟踪数据的 Zipkin 服务器；
     * （3）安装和配置 Zipkin 服务器以收集数据；
     * （4）定义每个客户端所使用的采样策略，便于向 Zipkin 发送跟踪信息。
     *
     *
     *
     * 1、添加 Spring Cloud Sleuth 和 Zipkin 依赖项
     *
     * 到目前为止，已经将两个 Maven 依赖项包含到 Zuul 服务、许可证服务以及组织服务中。这两个依赖项分别是：
     * spring-cloud-starter-sleuth 和 spring-cloud-sleuth-core 依赖项。
     *
     * spring-cloud-starter-sleuth 依赖项用于包含在服务中启用 Spring Cloud Sleuth 所需的基本 Spring Cloud
     * Sleuth 库。
     *
     * 当开发人员必须要以编程方式与 Spring Cloud Sleuth 进行交互时，就需要使用 spring-cloud-sleuth-core 依赖
     * 项。
     *
     * 要与 Zipkin 集成，需要添加第二个 Maven 依赖项，名为 spring-cloud-sleuth-zipkin。如下代码展示了添加
     * spring-cloud-sleuth-zipkin 依赖项后，在 Zuul、许可证以及组织服务中应该存在的 Maven 条目。
     *
     * <dependency>
     *   <groupId>org.springframework.cloud</groupId>
     *   <artifactId>spring-cloud-starter-sleuth</artifactId>
     * </dependency>
     * <dependency>
     *   <groupId>org.springframework.cloud</groupId>
     *   <artifactId>spring-cloud-sleuth-zipkin</artifactId>
     * </dependency>
     *
     *
     *
     * 2、配置服务以指向 Zipkin
     *
     * 有了 JAR 文件，接下来就需要配置想要与 Zipkin 进行通信的每一项服务。这项任务可以通过设置一个 Spring 属性
     * spring.zipkin.baseUrl 来完成，该属性定义了用于与 Zipkin 通信的 URL，它设置在每个服务的 application
     * .yml 属性文件中。
     *
     * 注意：spring.zipkin.baseUrl 也可以作为 Spring Cloud Config 中的属性进行外部化。
     *
     * 在每个服务的 application.yml 文件中，将该值设置为 http://localhost:9411 。但是，在运行时，这里使用在
     * 每个服务的 Docker 配置文件（docker/common/docker-compose.yml）上传递的 ZIPKIN_URI（http://zipkin
     * :9411）变量来覆盖这个值。
     *
     *
     * PS：Zipkin、RabbitMQ 与 Kafka
     *
     * Zipkin 确实有能力通过 RabbitMQ 或 Kafka 将其跟踪数据发送到 Zipkin 服务器。从功能的角度来看，不管使用
     * HTTP、RabbitMQ 还是 Kafka，Zipkin 的行为没有任何差异。通过使用 HTTP 跟踪，Zipkin 使用异步线程发送性
     * 能数据。另外，使用 RabbitMQ 或 Kafka 来收集跟踪数据的主要优势是，如果 Zipkin 服务器关闭，任何发送给
     * Zipkin 的跟踪信息都将 "排队"，直到 Zipkin 能够收集到数据。
     *
     * Spring Cloud Sleuth 通过 RabbitMQ 和 Kafka 向 Zipkin 发送数据的配置在 Spring Cloud Sleuth 文档
     * 中有介绍，因此这里将不再赘述。
     *
     *
     *
     * 3、安装和配置 Zipkin 服务器
     *
     * 要使用 Zipkin，首先需要建立一个 Spring Boot 项目。接下来，需要向 pom.xml 文件添加两个 JAR 依赖项。如下
     * 代码展示了这两个 JAR 依赖项。
     *
     * <dependency>
     *   <groupId>io.zipkin.java</groupId>
     *   <artifactId>zipkin-server</artifactId>　　
     * </dependency>
     * <dependency>
     *   <groupId>io.zipkin.java</groupId>
     *   <artifactId>zipkin-autoconfigure-ui</artifactId>　　
     * </dependency>
     *
     * 其中：第一个依赖项包含用于创建 Zipkin 服务器所需的核心类，第二个依赖项包含用于运行 Zipkin 服务器的 UI 部
     * 分所需的核心类。
     *
     *
     * PS：选择 @EnableZipkinServer 还是 @EnableZipkinStreamServer
     *
     * 关于上述 JAR 依赖项，有一件事需要注意，那就是它们不是基于 Spring Cloud 的依赖项。虽然 Zipkin 是一个基于
     * Spring Boot 的项目，但是 @EnableZipkinServer 并不是一个 Spring Cloud 注解，它是 Zipkin 项目的一部
     * 分。这通常会让 Spring Cloud Sleuth 和 Zipkin 的新手混淆，因为 Spring Cloud 团队确实编写了
     * @EnableZipkinStreamServer 注解作为 Spring Cloud Sleuth 的一部分，它简化了 Zipkin 与 RabbitMQ 和
     * Kafka 的使用。
     *
     * 这里选择使用 @EnableZipkinServer 是因为它创建简单。使用 @EnableZipkinStreamServer 需要创建和配置正
     * 在跟踪的服务以发布消息到 RabbitMQ 或 Kafka，此外，还需要设置和配置 Zipkin 服务器来监听 RabbitMQ 或
     * Kafka，以此来跟踪数据。@EnableZipkinStreamServer 注解的优点是，即使 Zipkin 服务器不可用，也可以继续
     * 收集跟踪数据。这是因为跟踪消息将在消息队列中累积跟踪数据，直到 Zipkin 服务器可用于处理消息记录。如果使用
     * 了 @EnableZipkinServer 注解，而 Zipkin 服务器不可用，那么服务发送给 Zipkin 的跟踪数据将会丢失。
     *
     *
     * 在定义完 JAR 依赖项之后，现在需要将 @EnableZipkinServer 注解添加到 Zipkin 服务引导类中。如下所示。
     *
     * @SpringBootApplication
     * @EnableZipkinServer
     * public class ZipkinServerApplication {
     *
     *     public static void main(String[] args) {
     *         SpringApplication.run(ZipkinServerApplication.class, args);
     *     }
     *
     * }
     *
     * 在这段代码中要注意的关键点是 @EnableZipkinServer 注解的使用。这个注解能够启动这个 Spring Boot 服务作为
     * 一个 Zipkin 服务器。此时，你可以构建、编译和启动 Zipkin 服务器，作为 Docker 容器之一。
     *
     * 运行 Zipkin 服务器只需要很少的配置。在运行 Zipkin 服务器时，唯一需要配置的东西，就是 Zipkin 存储来自服务
     * 的跟踪数据的后端数据存储。Zipkin 支持四种不同的后端数据存储。这些数据存储是：
     * （1）内存数据；
     * （2）MySQL；
     * （3）Cassandra；
     * （4）Elasticsearch。
     *
     * 在默认情况下，Zipkin 使用内存数据存储来存储跟踪数据。Zipkin 团队建议不要在生产系统中使用内存数据库。内存
     * 数据库只能容纳有限的数据，并且在 Zipkin 服务器关闭或丢失时，数据就会丢失。
     *
     * 注意：这里将使用 Zipkin 的内存数据存储。配置 Zipkin 中使用的各个数据存储超出了这里的范围，但是，如果你对
     * 这个主题感兴趣，可以在 Zipkin GitHub 存储库中查阅更多信息。
     *
     *
     *
     * 4、设置跟踪级别
     *
     * 到目前为止，已经配置了要与 Zipkin 服务器通信的客户端，并且已经配置完 Zipkin 服务器准备运行。在开始使用
     * Zipkin 之前，还需要再做一件事情，那就是定义每个服务应该向 Zipkin 写入数据的频率。
     *
     * 在默认情况下，Zipkin 只会将所有事务的 10% 写入 Zipkin 服务器。可以通过在每一个向 Zipkin 发送数据的服
     * 务上设置一个 Spring 属性来控制事务采样。这个属性叫 spring.sleuth.sampler.percentage，它的值介于 0
     * 和 1 之间。
     * （1）值为 0 表示 Spring Cloud Sleuth 不会发送任何事务数据。
     * （2）值为 0.5 表示 Spring Cloud Sleuth 将发送所有事务的 50%。
     *
     * 对于这里来讲，将为所有服务发送跟踪信息。要做到这一点，可以设置 spring.sleuth.sampler.percentage 的值，
     * 也可以使用 AlwaysSampler 替换 Spring Cloud Sleuth 中使用的默认 Sampler 类。AlwaysSampler 可以作为
     * Spring Bean 注入应用程序中。例如，许可证服务在 Application 中将 AlwaysSampler 定义为 Spring Bean。
     *
     *     @Bean
     *     public Sampler defaultSampler() {
     *         return new AlwaysSampler();
     *     }
     *
     * Zuul 服务、许可证服务和组织服务都定义了 AlwaysSampler，因此在这里中，所有的事务都会被 Zipkin 跟踪。
     *
     *
     *
     * 5、使用 Zipkin 跟踪事务
     *
     * 以一个场景来开始这一节。假设你是 EagleEye 应用程序的一名开发人员，并且你在这周处于待命状态。你从客户那里
     * 收到一张工单，他抱怨说 EagleEye 应用程序的某一部分现在运行缓慢。你怀疑是许可证服务导致的，但问题是，为什
     * 么它会运行缓慢呢？问题究竟出在了哪里呢？许可证服务依赖于组织服务，而这两个服务都对不同的数据库进行调用。究
     * 竟是哪个服务表现不佳？此外，你知道这些服务正在不断被迭代更新，因此有人可能添加了一个新的服务调用。了解参与
     * 用户事务的所有服务以及它们的性能时间对于支持分布式架构（如微服务架构）是至关重要的。
     *
     * 接下来，你将开始使用 Zipkin 来观察来自组织服务的两个事务（它们由 Zipkin 服务进行跟踪）。组织服务是一个
     * 简单的服务，它只对单个数据库进行调用。你所要做的就是使用 POSTMAN 向组织服务发送两个调用（对 http://
     * localhost:5555/api/organization/v1/organizations/e254f8c-c442-4ebe-a82a-e2fc1d1ff78a 发起
     * GET 请求）。组织服务调用将流经 Zuul API 网关，然后再将调用定向到下游组织服务实例。
     *
     * 调用了两次组织服务之后，转到 http://localhost:9411，看看 Zipkin 已经捕获的跟踪结果。从界面左上角的下
     * 拉框中选择 "organizationservice"，然后点击 "Find traces" 按钮，就可以看到查询结果了。
     *
     * PS：可以在 Zipkin 的查询界面选择想要跟踪的服务以及一些基本的查询过滤器。
     *
     * 现在，如果你查看查询结果，就会发现 Zipkin 捕获了两个事务，每个事务都被分解为一个或多个跨度（span）。在
     * Zipkin 中，一个跨度代表一个特定的服务或调用，Zipkin 会捕获每一个跨度的计时信息。查询结果的每一个事务都
     * 包含三个跨度：两个跨度在 Zuul 网关中，还有一个是组织服务。记住，Zuul 网关不会盲目地转发 HTTP 调用。它
     * 接收传入的 HTTP 调用并终止这个调用，然后构建一个新的到目标服务的调用（在本例中是组织服务）。原始调用的
     * 终止是因为 Zuul 要添加前置过滤器、路由过滤器以及后置过滤器到进入该网关的每一个调用。这就是在 Zuul 服务
     * 中看到两个跨度的原因。
     *
     * 通过 Zuul 对组织服务的两次调用分别用了 3.204 s 和 77.2365 ms。因为查询的是组织服务调用（而不是 Zuul
     * 网关调用），从查询结果可以看到组织服务在总事务时间中占了 92% 和 72%。
     *
     * 还可以深入了解运行时间最长的调用（3.204 s）的细节。你可以通过点击事务并深入了解细节来查看更多详细信息。
     *
     * 从 Zuul 角度来看，整个事务大约需要 3.204 s。然而，Zuul 发出的组织服务调用耗费了整个调用过程 3.204 s
     * 中的 2.967 s。查询结果中展示的每个跨度都可以深入到更多的细节。点击组织服务跨度，并查看可以从这个调用中
     * 看到哪些额外的细节。其中最有价值的信息之一是客户端（Zuul）何时调用组织服务、组织服务何时接收到调用以及
     * 组织服务何时作出响应等分解信息。这种类型的计时信息在检测和识别网络延迟问题方面是非常宝贵的。
     *
     *
     *
     * 6、可视化更复杂的事务
     *
     * 如果想要确切了解服务调用之间存在哪些服务依赖关系，该怎么办？可以通过 Zuul 调用许可证服务，然后向 Zipkin
     * 查询许可证服务的跟踪。
     *
     * 这项工作可以通过对许可证服务的 http://localhost:5555/api/licensing/v1/organizations
     * /e254f8c-c442-4ebe-a82a-e2fc1d1ff78a/licenses/f3831f8c-c338-4ebe-a82a-e2fc1d1ff78a
     * 端点进行 GET 调用来完成。
     *
     * 从查询结果可以看到对许可证服务的调用涉及 4 个离散的 HTTP 调用。首先是对 Zuul 网关的调用，然后从 Zuul
     * 网关到许可证服务，接下来许可证服务通过 Zuul 调用组织服务。
     *
     *
     *
     * 7、捕获消息传递跟踪
     *
     * Spring Cloud Sleuth 和 Zipkin 不仅会跟踪 HTTP 调用，Spring Cloud Sleuth 还会向 Zipkin 发送在服务
     * 中注册的入站或出站消息通道上的跟踪数据。
     *
     * 消息传递可能会在应用程序内引发它自己的性能和延迟问题。这句话的意思是，服务可能无法快速处理队列中的消息，或
     * 者可能存在网络延迟问题。在构建基于微服务的应用程序时，曾经遇到了所有这些情况。
     *
     * 通过使用 Spring Cloud Sleuth 和 Zipkin，开发人员可以确定何时从队列发布消息以及何时收到消息。除此之外，
     * 开发人员还可以查看在队列中接收到消息并进行处理时发生了什么行为。
     *
     * 正如之前每次添加、更新或删除一条组织记录时，就会生成一条 Kafka 消息并通过 Spring Cloud Stream 发布。许
     * 可证服务接收消息，并更新用于缓存数据的 Redis 键值存储。
     *
     * 现在，将删除组织记录，并观察由 Spring Cloud Sleuth 和 Zipkin 跟踪的事务。你可以通过 POSTMAN 向组织服
     * 务发出 DELETE http://local-host:5555/api/organization/v1/organizations/e254f8c-c442-4ebe-
     * a82a-e2fc1d1ff78a 请求。
     *
     * 之前已经了解了如何将跟踪 ID 添加为 HTTP 响应首部。并且添加了一个名为 tmx-correlation-id 的新 HTTP 响
     * 应首部。在一次调用中，这个 tmx-correlation-id 返回值是 5e14cae0d90dc8d4。你可以通过在 Zipkin 查询界
     * 面右上角的搜索框中输入调用所返回的跟踪 ID，来向 Zipkin 搜索这个特定的跟踪 ID。
     *
     * 有了跟踪 ID 就可以向 Zipkin 查询特定的事务，并可以查看到删除消息发布到输出消息通道。此消息通道 output 用
     * 于发布消息到名为 orgChangeTopic 的主题。
     *
     * 通过查询 Zipkin 并搜索收到的消息可以看到许可证服务收到消息。遗憾的是，Spring Cloud Sleuth 不会将已发布
     * 消息的跟踪 ID 传播给消息的消费者。相反，它会生成一个新的跟踪 ID。但是，可以向 Zipkin 服务器查询所有许可证
     * 服务的事务，并通过最新消息对事务进行排序。
     *
     * 当已经找到目标许可证服务的事务时，就可以深入了解这个事务。
     *
     * 到目前为止，已经使用 Zipkin 来跟踪服务中的 HTTP 和消息传递调用。但是，如果要对未由 Zipkin 检测的第三方
     * 服务执行跟踪，那该怎么办呢？例如，如果想要获取对 Redis 或 PostgresSQL 调用的特定跟踪和计时信息，该怎么
     * 办呢？幸运的是，Spring Cloud Sleuth 和 Zipkin 允许开发人员为事务添加自定义跨度，以便跟踪与这些第三方
     * 调用相关的执行时间。
     *
     *
     *
     * 8、添加自定义跨度
     *
     * 在 Zipkin 中添加自定义跨度是非常容易的。可以从向许可证服务添加一个自定义跨度开始，这样就可以跟踪从 Redis
     * 中提取数据所需的时间。然后，将向组织服务添加自定义跨度，以查看从组织数据库中检索数据需要多长时间。
     *
     * 为了将一个自定义跨度添加到许可证服务对 Redis 的调用中，需要修改 OrganizationRestTemplateClient 类中
     * 的 checkRedisCache() 方法。
     *
     * @Component
     * public class OrganizationRestTemplateClient {
     *
     *     @Autowired
     *     RestTemplate restTemplate;
     *
     *     @Autowired
     *     Tracer tracer;
     *
     *     @Autowired
     *     OrganizationRedisRepository orgRedisRepo;
     *
     *     private static final Logger logger =
     *          LoggerFactory.getLogger(OrganizationRestTemplateClient.class);
     *
     *     private Organization checkRedisCache(String organizationId) {
     *         Span newSpan = tracer.createSpan("readLicensingDataFromRedis");
     *         try {
     *             return orgRedisRepo.findOrganization(organizationId);
     *         }
     *         catch (Exception ex){
     *             logger.error("Error encountered while trying to retrieve organization {}
     *             check Redis Cache.  Exception {}", organizationId, ex);
     *             return null;
     *         }
     *         finally {
     *             newSpan.tag("peer.service", "redis");
     *             newSpan.logEvent(org.springframework.cloud.sleuth.Span.CLIENT_RECV);
     *             tracer.close(newSpan);
     *         }
     *     }
     *
     *     // ...
     *
     * }
     *
     * 这段代码创建了一个名为 readLicensingDataFromRedis 的自定义跨度。接下来，将同样添加一个名为 getOrgDbCall
     * 的自定义跨度到组织服务中，以监控从 Postgres 数据库中检索组织数据需要多长时间。对组织服务数据库的调用跟踪可以
     * 在 OrganizationService 类中看到。其中，getOrg() 方法包含自定义跟踪。
     *
     * @Service
     * public class OrganizationService {
     *     @Autowired
     *     private OrganizationRepository orgRepository;
     *
     *     @Autowired
     *     private Tracer tracer;
     *
     *     @Autowired
     *     SimpleSourceBean simpleSourceBean;
     *
     *     private static final Logger logger =
     *          LoggerFactory.getLogger(OrganizationService.class);
     *
     *     public Organization getOrg
     *             (String organizationId) {
     *         Span newSpan = tracer.createSpan("getOrgDBCall");
     *
     *         logger.debug("In the organizationService.getOrg() call");
     *         try {
     *             return orgRepository.findById(organizationId);
     *         }
     *         finally{
     *             newSpan.tag("peer.service", "postgres");
     *             newSpan.logEvent(org.springframework.cloud.sleuth.Span.CLIENT_RECV);
     *             tracer.close(newSpan);
     *         }
     *     }
     *
     *     // ...
     *
     * }
     *
     * 有了这两个自定义跨度，就可以重启服务，然后访问 GET http://localhost:5555/api/licensing/v1/organizations
     * /e254f8c-c442-4ebe-a82a-e2fc1d1ff78a/licenses/f3831f8c-c338-4ebe-a82a-e2fc1d1ff78a 端点。如果在
     * Zipkin 中查看事务，应该看到增加了两个额外的跨度。
     *
     * 从查询结果可以看到与 Redis 和数据库查询相关的附加跟踪和计时信息。其中对 Redis 的调用用了 1.099 ms。由于调用
     * 没有在 Redis 缓存中找到记录，所以对 Postgres 数据库的 SQL 调用用了 4.784 ms。
     */
    public static void main(String[] args) {

    }

}
