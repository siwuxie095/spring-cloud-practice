package com.siwuxie095.spring.cloud.chapter8th.example5th;

/**
 * @author Jiajing Li
 * @date 2021-06-22 21:36:34
 */
@SuppressWarnings("all")
public class Main {

    /**
     * Spring Cloud Stream 用例：分布式缓存
     *
     * 到目前为止，已经拥有两个使用消息传递进行通信的服务，但是并没有真正处理消息。现在将要构建之前讨论过的分布式
     * 缓存示例。这里将让许可证服务始终检查分布式的 Redis 缓存以获取与特定许可证相关联的组织数据。如果组织数据在
     * 缓存中存在，那么将从缓存中返回数据。否则，将调用组织服务，并将调用的结果缓存在一个 Redis 散列中。
     *
     * 在组织服务中更新数据时，组织服务将向 Kafka 发出一条消息。许可证服务将接收消息，并对 Redis 发出删除指令，
     * 以清除缓存。
     *
     * PS：云缓存与消息传递
     *
     * 使用 Redis 作为分布式缓存与云中的微服务开发密切相关。以笔者目前的雇主来为例，使用了亚马逊 Web 服务（AWS）
     * 来构建解决方案，并且是亚马逊的 DynamoDB 的重度使用者。同时还使用亚马逊的 ElastiCache（Redis）增强如下
     * 功能。
     * （1）提高查找常用数据的性能：通过使用缓存，显著提高了几个关键服务的性能。这里销售的产品中的所有表都是多租
     * 户的（在单个表中保存多个客户记录），这意味着它们可以非常大。由于缓存倾向于留住 "大量" 使用的数据，所以使
     * 用 Redis 和缓存来避免读取 DynamoDB，从而显著提高了性能。
     * （2）减少持有数据的 DynamoDB 表上的负载（和成本）：在 DynamoDB 中访问数据可能是一项昂贵的提议。应用程序
     * 发出的每一次读取都是一次收费事件。使用 Redis 服务器通过主键读取要比 DynamoDB 读取便宜得多。
     * （3）增加弹性，以便在主数据存储（DynamoDB）存在性能问题时，服务能够优雅地降级：如果 AWS DynamoDB 出现
     * 问题（这确实偶尔发生），使用诸如 Redis 这样的缓存可以帮助服务优雅地降级。根据在缓存中保存的数据量，缓存
     * 解决方案可以帮助减少从访问数据存储中获取的错误的数量。
     *
     * Redis 远远不止是一个缓存解决方案，但是如果开发人员需要一个分布式缓存，它可以充当这个角色。
     *
     *
     *
     * 1、使用 Redis 来缓存查找
     *
     * 现在先从设置许可证服务以使用 Redis 开始。幸运的是，Spring Data 已经简化了将 Redis 引入许可证服务中的工
     * 作。要在许可证服务中使用 Redis，需要做以下四件事情。
     * （1）配置许可证服务以包含 Spring Data Redis 依赖项。
     * （2）构造一个到 Redis 服务器的数据库连接。
     * （3）定义 Spring Data Redis 存储库，代码将使用它与一个 Redis 散列进行交互。
     * （4）使用 Redis 和许可证服务来存储和读取组织数据。
     *
     *
     * 1.1、配置许可证服务以包含 Spring Data Redis 依赖项
     *
     * 需要做的第一件事就是将 spring-data-redis、jedis 以及 common-pools2 依赖项包含在许可证服务的 pom.xml
     * 文件中。如下展示了要包含的依赖项。
     *
     * <dependency>
     *   <groupId>org.springframework.data</groupId>
     *   <artifactId>spring-data-redis</artifactId>
     *   <version>1.7.4.RELEASE</version>
     * </dependency>
     * <dependency>
     *   <groupId>redis.clients</groupId>
     *   <artifactId>jedis</artifactId>
     *   <version>2.9.0</version>
     * </dependency>
     * <dependency>
     *   <groupId>org.apache.commons</groupId>
     *   <artifactId>commons-pool2</artifactId>
     *   <version>2.0</version>
     * </dependency>
     *
     *
     * 1.2、构造一个到 Redis 服务器的数据库连接
     *
     * 既然已经在 Maven 中添加了依赖项，接下来就需要建立一个到 Redis 服务器的连接。Spring 使用开源项目 Jedis
     * 与 Redis 服务器进行通信。
     *
     * 要与特定的 Redis 实例进行通信，需要在 Application 类中公开一个 JedisConnectionFactory 作为 Spring
     * bean。一旦连接到 Redis，将使用该连接创建一个 Spring RedisTemplate 对象。这里很快会实现 Spring Data
     * 存储库类，它们将使用 RedisTemplate 对象来执行查询，并将组织服务数据保存到 Redis 服务中。如下所示。
     *
     * @SpringBootApplication
     * @EnableEurekaClient
     * @EnableCircuitBreaker
     * @EnableBinding(Sink.class)
     * public class Application {
     *
     *     @Autowired
     *     private ServiceConfig serviceConfig;
     *
     *     @Bean
     *     public JedisConnectionFactory jedisConnectionFactory() {
     *         JedisConnectionFactory jedisConnFactory = new JedisConnectionFactory();
     *         jedisConnFactory.setHostName(serviceConfig.getRedisServer());
     *         jedisConnFactory.setPort(serviceConfig.getRedisPort() );
     *         return jedisConnFactory;
     *     }
     *
     *     @Bean
     *     public RedisTemplate<String, Object> redisTemplate() {
     *         RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
     *         template.setConnectionFactory(jedisConnectionFactory());
     *         return template;
     *     }
     *
     * }
     *
     * 建立许可证服务与 Redis 进行通信的基础工作已经完成。下面来编写从 Redis 查询、添加、更新和删除数据的逻辑。
     *
     *
     * 1.3、定义 Spring Data Redis 存储库
     *
     * Redis 是一个键值数据存储，它的作用类似于一个大的、分布式的、内存中的 HashMap。在最简单的情况下，它存储数
     * 据并按键查找数据。Redis 没有任何复杂的查询语言来检索数据。它的简单性是它的优点，也是这么多项目采用它的原因
     * 之一。
     *
     * 因为这里使用 Spring Data 来访问 Redis 存储，所以需要定义一个存储库类。而 Spring Data 可以使用用户定义
     * 的存储库类为 Java 类提供一个简单的机制来访问 Postgres 数据库，而无须开发人员编写低级的 SQL 查询。
     *
     * 对于许可证服务，这里将为 Redis 存储库定义两个文件。将要编写的第一个文件是一个 Java 接口，它将被注入任何
     * 需要访问 Redis 的许可证服务类中。如下所示。
     *
     * public interface OrganizationRedisRepository {
     *     void saveOrganization(Organization org);
     *     void updateOrganization(Organization org);
     *     void deleteOrganization(String organizationId);
     *     Organization findOrganization(String organizationId);
     * }
     *
     * 第二个文件是 OrganizationRedisRepository 接口的实现。该实现类使用了之前定义的 RedisTemplate 来与
     * Redis 服务器进行交互，并对 Redis 服务器执行操作。如下所示。
     *
     * @Repository
     * public class OrganizationRedisRepositoryImpl implements OrganizationRedisRepository {
     *
     *     private static final String HASH_NAME ="organization";
     *
     *     private RedisTemplate<String, Organization> redisTemplate;
     *     private HashOperations hashOperations;
     *
     *     public OrganizationRedisRepositoryImpl() {
     *         super();
     *     }
     *
     *     @Autowired
     *     private OrganizationRedisRepositoryImpl(RedisTemplate redisTemplate) {
     *         this.redisTemplate = redisTemplate;
     *     }
     *
     *     @PostConstruct
     *     private void init() {
     *         hashOperations = redisTemplate.opsForHash();
     *     }
     *
     *
     *     @Override
     *     public void saveOrganization(Organization org) {
     *         hashOperations.put(HASH_NAME, org.getId(), org);
     *     }
     *
     *     @Override
     *     public void updateOrganization(Organization org) {
     *         hashOperations.put(HASH_NAME, org.getId(), org);
     *     }
     *
     *     @Override
     *     public void deleteOrganization(String organizationId) {
     *         hashOperations.delete(HASH_NAME, organizationId);
     *     }
     *
     *     @Override
     *     public Organization findOrganization(String organizationId) {
     *         return (Organization) hashOperations.get(HASH_NAME, organizationId);
     *     }
     *
     * }
     *
     * OrganizationRedisRepositoryImpl 包含用于从 Redis 存储和检索数据的所有 CRUD（Create、Read、Update
     * 和 Delete）逻辑。这里有两个关键问题需要注意。
     * （1）Redis 中的所有数据都是通过一个键存储和检索的。因为是存储从组织服务中检索到的数据，所以自然选择组织 ID
     * 作为存储组织记录的键。
     * （2）一个 Redis 服务器可以包含多个散列和数据结构。在针对 Redis 服务器的每个操作中，需要告诉 Redis 执行
     * 操作的数据结构的名字。这里使用的数据结构名称存储在 HASH_NAME 常量中，其值为 "organization"。
     *
     *
     * 1.4、使用 Redis 和许可证服务来存储和读取组织数据
     *
     * 在完成对 Redis 执行操作的代码之后，就可以修改许可证服务，以便每次许可证服务需要组织数据时，它会在调用组织
     * 服务之前检查 Redis 缓存。检查 Redis 的逻辑将出现在 OrganizationRestTemplateClient 类中。如下所示。
     *
     * @Component
     * public class OrganizationRestTemplateClient {
     *
     *     @Autowired
     *     RestTemplate restTemplate;
     *
     *     @Autowired
     *     OrganizationRedisRepository orgRedisRepo;
     *
     *     private static final Logger logger =
     *     LoggerFactory.getLogger(OrganizationRestTemplateClient.class);
     *
     *     private Organization checkRedisCache(String organizationId) {
     *         try {
     *             return orgRedisRepo.findOrganization(organizationId);
     *         } catch (Exception ex) {
     *             logger.error("Error encountered while trying to retrieve organization {}
     *             check Redis Cache.  Exception {}", organizationId, ex);
     *             return null;
     *         }
     *     }
     *
     *     private void cacheOrganizationObject(Organization org) {
     *         try {
     *             orgRedisRepo.saveOrganization(org);
     *         } catch (Exception ex) {
     *             logger.error("Unable to cache organization {} in Redis. Exception {}",
     *             org.getId(), ex);
     *         }
     *     }
     *
     *     public Organization getOrganization(String organizationId){
     *         logger.debug("In Licensing Service.getOrganization: {}",
     *         UserContext.getCorrelationId());
     *
     *         Organization org = checkRedisCache(organizationId);
     *
     *         if (org != null) {
     *             logger.debug("I have successfully retrieved an organization {}
     *             from the redis cache: {}", organizationId, org);
     *             return org;
     *         }
     *
     *         logger.debug("Unable to locate organization from the redis cache: {}.",
     *         organizationId);
     *
     *         ResponseEntity<Organization> restExchange =
     *                 restTemplate.exchange(
     *                         "http://zuulservice/api/organization/v1/organizations/{organizationId}",
     *                         HttpMethod.GET,
     *                         null, Organization.class, organizationId);
     *
     *         org = restExchange.getBody();
     *
     *         if (org != null) {
     *             cacheOrganizationObject(org);
     *         }
     *
     *         return org;
     *     }
     *
     * }
     *
     * getOrganization() 方法是调用组织服务的地方。在进行实际的 REST 调用之前，尝试使用 checkRedisCache()
     * 方法从 Redis 中检索与调用相关联的组织对象。如果该组织对象不在 Redis 中，则代码将返回一个 null 值。如果
     * 从 checkRedisCache() 方法返回一个 null 值，那么代码将调用组织服务的 REST 端点来检索所需的组织记录。
     * 如果组织服务返回一条组织记录，那么将使用 cacheOrganizationObject() 方法缓存返回的组织对象。
     *
     * 注意：在与缓存进行交互时，要特别注意异常处理。为了提高弹性，如果无法与 Redis 服务器通信，绝对不会让整个
     * 调用失败。相反，这里会记录异常，并让调用转到组织服务。在这个特定的用例中，缓存旨在帮助提高性能，而缓存服
     * 务器的缺失不应该影响调用的成功。
     *
     * 有了 Redis 缓存代码，接下来应该访问许可证服务（是的，目前只有两个服务，但是有很多基础设施），并查看日志
     * 消息。如果你连续访问以下许可证服务端点 http://localhost:5555/api/licensing/v1/organizations
     * /e254f8c-c442-4ebe-a82a-e2fc1d1ff78a/licenses/f3831f8c-c338-4ebe-a82a-e2fc1d1ff78a 两次，
     * 那么应该在日志中看到两个输出语句。
     *
     * 来自控制台的第一行显示，第一次调用尝试为组织访问许可证服务端点 e254f8c-c442-4ebe-a82a-e2fc1d1ff78a。
     * 许可证服务首先检查了 Redis 缓存，但找不到要查找的组织记录。然后代码调用组织服务来检索数据。从控制台显示
     * 出来的第二行表明，在第二次访问许可证服务端点时，组织记录已被缓存了。
     *
     *
     *
     * 2、定义自定义通道
     *
     * 之前在许可证服务和组织服务之间构建了消息集成，以便使用默认的 output 和 input 通道，这些通道与 Source 和
     * Sink 接口一起打包在 Spring Cloud Stream 项目中。然而，如果想要为应用程序定义多个通道，或者想要定制通道
     * 的名称，那么开发人员可以定义自己的接口，并根据应用程序需要公开任意数量的输入和输出通道。
     *
     * 要在许可证服务里面创建名为 inboundOrgChanges 的自定义通道，可以在 CustomChannels 接口中进行定义，如下
     * 所示。
     *
     * public interface CustomChannels {
     *     @Input("inboundOrgChanges")
     *     SubscribableChannel orgs();
     * }
     *
     * 这里的关键信息是，对于要公开的每个自定义 input 通道，使用 @Input 注解标记一个返回 SubscribableChannel
     * 类的方法。如果想要为发布的消息定义 output 通道，可以在将要调用的方法上使用 @OutputChannel。在 output
     * 通道的情况下，定义的方法将返回一个 MessageChannel 类而不是与 input 通道一起使用的 SubscribableChannel
     * 类。如下所示。
     *
     * @OutputChannel("outboundOrg")
     * MessageChannel outboundOrg();
     *
     * 定义完自定义 input 通道之后，接下来就需要在许可证服务中修改两样东西来使用它。首先，需要修改许可证服务，以
     * 将自定义 input 通道名称映射到 Kafka 主题。
     *
     * spring:
     *   cloud:
     *     stream:
     *       bindings:
     *         inboundOrgChanges:
     *           destination: orgChangeTopic
     *           content-type: application/json
     *           group: licensingGroup
     *
     * 要使用自定义 input 通道，需要将定义的 CustomChannels 接口注入将要使用它来处理消息的类中。对于分布式缓存
     * 示例，这里已经将处理传入消息的代码移到了 OrganizationChangeHandler 类。如下所示。
     *
     * @EnableBinding(CustomChannels.class)
     * public class OrganizationChangeHandler {
     *
     *     @StreamListener("inboundOrgChanges")
     *     public void loggerSink(OrganizationChangeModel orgChange) {
     *          // ...
     *     }
     *
     * }
     *
     * 这里展示了与定义的 inboundOrgChanges 通道一起使用的消息处理代码。
     *
     *
     *
     * 3、将其全部汇集在一起：在收到消息时清除缓存
     *
     * 到目前为止，不需要对组织服务做任何事。该服务被设置为在组织被添加、更新或删除时发布一条消息。这里需要做的就
     * 是构建出 OrganizationChangeHandler 类。如下所示是完整代码。
     *
     * @EnableBinding(CustomChannels.class)
     * public class OrganizationChangeHandler {
     *
     *     @Autowired
     *     private OrganizationRedisRepository organizationRedisRepository;
     *
     *     private static final Logger logger = LoggerFactory.getLogger(OrganizationChangeHandler.class);
     *
     *     @StreamListener("inboundOrgChanges")
     *     public void loggerSink(OrganizationChangeModel orgChange) {
     *         logger.debug("Received a message of type " + orgChange.getType());
     *         switch(orgChange.getAction()){
     *             case "GET":
     *                 logger.debug("Received a GET event from the organization service for
     *                 organization id {}", orgChange.getOrganizationId());
     *                 break;
     *             case "SAVE":
     *                 logger.debug("Received a SAVE event from the organization service for
     *                 organization id {}", orgChange.getOrganizationId());
     *                 break;
     *             case "UPDATE":
     *                 logger.debug("Received a UPDATE event from the organization service for
     *                 organization id {}", orgChange.getOrganizationId());
     *                 organizationRedisRepository.deleteOrganization(orgChange.getOrganizationId());
     *                 break;
     *             case "DELETE":
     *                 logger.debug("Received a DELETE event from the organization service for
     *                 organization id {}", orgChange.getOrganizationId());
     *                 organizationRedisRepository.deleteOrganization(orgChange.getOrganizationId());
     *                 break;
     *             default:
     *                 logger.error("Received an UNKNOWN event from the organization service of type {}",
     *                 orgChange.getType());
     *                 break;
     *         }
     *     }
     *
     * }
     */
    public static void main(String[] args) {

    }

}
