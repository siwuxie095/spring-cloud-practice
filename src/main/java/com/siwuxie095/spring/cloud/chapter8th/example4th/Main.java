package com.siwuxie095.spring.cloud.chapter8th.example4th;

/**
 * @author Jiajing Li
 * @date 2021-06-21 07:33:10
 */
@SuppressWarnings("all")
public class Main {

    /**
     * 编写简单的消息生产者和消费者
     *
     * 现在已经了解完 Spring Cloud Stream 中的基本组件，接下来看一个简单的 Spring Cloud Stream 示例。对于
     * 第一个例子，这里将要从组织服务传递一条消息到许可证服务。在许可证服务中，唯一要做的事情就是将日志消息打印到
     * 控制台。
     *
     * 另外，在这个例子中，因为只有一个 Spring Cloud Stream 发射器（消息生产者）和接收器（消息消费者），所以
     * 这里将要采用 Spring Cloud 提供的一些便捷方式，让在组织服务中建立发射器以及在许可证服务中建立接收器变得
     * 更简单。
     *
     *
     *
     * 1、在组织服务中编写消息生产者
     *
     * 首先修改组织服务，以便每次添加、更新或删除组织数据时，组织服务将向 Kafka 主题（topic）发布一条消息，指示
     * 组织更改事件已经发生。如下过程突出了消息生产者，并构建在通用 Spring Cloud Stream 架构之上。
     * （1）组织客户端调用组织服务的 REST 端点，更新了数据。
     * （2）组织服务将在内部使用 SimpleSourceBean 名称的 bean 来发布消息。
     * （3）output 是 Spring Cloud Stream 通道的名称，它将映射到 Kafka 主题（orgChangeTopic）。
     * （4）与 Kafka 服务器绑定的 Spring Cloud Stream 类和配置。
     *
     * PS：当组织服务数据发生变化时，它会向 Kafka 发布消息。
     *
     * 需要做的第一件事就是在组织服务的 Maven pom.xml 文件中设置 Maven 依赖项。在 pom.xml 中，需要添加两个
     * 依赖项：一个用于核心 Spring Cloud Stream 库，另一个用于包含 Spring Cloud Stream Kafka 库。
     *
     * <dependency>
     *   <groupId>org.springframework.cloud</groupId>
     *   <artifactId>spring-cloud-stream</artifactId>
     * </dependency>
     * <dependency>
     *   <groupId>org.springframework.cloud</groupId>
     *   <artifactId>spring-cloud-starter-stream-kafka</artifactId>
     * </dependency>
     *
     * 定义完 Maven 依赖项，就需要告诉应用程序它将绑定到 Spring Cloud Stream 消息代理。这可以通过使用
     * @EnableBinding 注解来标注组织服务的引导类 Application 来完成。如下所示。
     *
     * @SpringBootApplication
     * @EnableEurekaClient
     * @EnableCircuitBreaker
     * @EnableBinding(Source.class)
     * public class Application {
     *
     *     @Bean
     *     public Filter userContextFilter() {
     *         UserContextFilter userContextFilter = new UserContextFilter();
     *         return userContextFilter;
     *     }
     *
     *     public static void main(String[] args) {
     *         SpringApplication.run(Application.class, args);
     *     }
     *
     * }
     *
     * 这段代码中，@EnableBinding 注解告诉 Spring Cloud Stream 希望将服务绑定到消息代理。@EnableBinding
     * 注解中的 Source.class 告诉 Spring Cloud Stream，该服务将通过在 Source 类上定义的一组通道与消息代理
     * 进行通信。记住，通道位于消息队列之上。Spring Cloud Stream 有一个默认的通道集，可以配置它们来与消息代理
     * 进行通信。
     *
     * 到目前为止，还没有告诉 Spring Cloud Stream 希望将组织服务绑定到什么消息代理。这里很快就会讲到这一点。
     * 现在，可以继续实现将要发布消息的代码。
     *
     * 消息发布的代码可以在 SimpleSourceBean 中找到。如下所示。
     *
     * @Component
     * public class SimpleSourceBean {
     *
     *     private Source source;
     *
     *     private static final Logger logger = LoggerFactory.getLogger(SimpleSourceBean.class);
     *
     *     @Autowired
     *     public SimpleSourceBean(Source source){
     *         this.source = source;
     *     }
     *
     *     public void publishOrgChange(String action, String orgId){
     *         logger.debug("Sending Kafka message {} for Organization Id: {}", action, orgId);
     *         OrganizationChangeModel change =  new OrganizationChangeModel(
     *                 OrganizationChangeModel.class.getTypeName(),
     *                 action,
     *                 orgId,
     *                 UserContext.getCorrelationId());
     *
     *         source.output().send(MessageBuilder.withPayload(change).build());
     *     }
     *
     * }
     *
     * 这里将 Spring Cloud Source 类注入代码中。记住，所有与特定消息主题的通信都是通过称为通道的 Spring Cloud
     * Stream 结构来实现的。通道由一个 Java 接口类表示。这里使用的是 Source 接口。Source 是 Spring Cloud
     * 定义的一个接口，它公开了一个名为 output() 的方法。当服务只需要发布到单个通道时，Source 接口是一个很方便
     * 的接口。output() 方法返回一个 MessageChannel 类型的类。MessageChannel 代表了如何将消息发送给消息代理。
     * 后续将介绍如何使用自定义接口来公开多个消息传递通道。
     *
     * 消息的实际发布发生在 publishOrgChange() 方法中。此方法构建一个 Java POJO，名为 OrganizationChangeModel。
     * 这里就不展示 OrganizationChangeModel 的代码了，因为这个类只是一个包含三个数据元素的 POJO。如下：
     * （1）动作（action）：这是触发事件的动作。这里在消息中包含了这个动作，以便让消息消费者在处理事件的过程中有
     * 更多的上下文。
     * （2）组织 ID（organization ID）：这是与事件关联的组织 ID。
     * （3）关联 ID（correlation ID）：这是触发事件的服务调用的关联 ID。应该始终在事件中包含关联 ID，因为它对
     * 跟踪和调试流经服务的消息流有极大的帮助。
     *
     * 当准备好发布消息时，可使用从 source.output() 返回的 MessageChannel 的 send() 方法：
     *
     * source.output().send(MessageBuilder.withPayload(change).build());
     *
     * send() 方法接收一个 Spring Message 类。这里使用一个名为 MessageBuilder 的 Spring 辅助类来接收
     * OrganizationChangeModel 类的内容，并将它转换为 Spring Message 类。
     *
     * 这就是发送消息所需的所有代码。然而，到目前为止，这一切都感觉有点儿像魔术，因为还没有看到如何将组织服务绑定
     * 到一个特定的消息队列，更不用说实际的消息代理。上述的这一切都是通过配置来完成的。如下代码展示了这一配置，它
     * 将服务的 Spring Cloud Stream Source 映射到 Kafka 消息代理以及 Kafka 中的消息主题。此配置信息可以位
     * 于服务的 application.yml 文件中，也可以位于服务的 Spring Cloud Config 条目中。
     *
     * spring:
     *   cloud:
     *     stream:
     *       bindings:
     *         output:
     *             destination:  orgChangeTopic
     *             content-type: application/json
     *       kafka:
     *         binder:
     *           zkNodes: localhost
     *           brokers: localhost
     *
     * 这里的配置看起来很密集，但很简单。配置属性 spring.stream.bindings.output 将 source.output() 通道映
     * 射到要与之通信的消息代理上的主题 orgChangeTopic 。它还告诉 Spring Cloud Stream，发送到此主题的消息应
     * 该被序列化为 JSON。Spring Cloud Stream 可以以多种格式序列化消息，包括 JSON、XML 以及 Apache 基金会
     * 的 Avro 格式。
     *
     * 配置属性 spring.stream.bindings.kafka 告诉 Spring Cloud Stream，将服务绑定到 Kafka。子属性告诉
     * Spring Cloud Stream，Kafka 消息代理和运行着 Kafka 的 Apache ZooKeeper 服务器的网络地址。
     *
     * 这里已经编写完通过 Spring Cloud Stream 发布消息的代码，并通过配置来告诉 Spring Cloud Stream 它将使用
     * Kafka 作为消息代理，那么接下来看看，组织服务中消息的发布实际发生在哪里。这项工作将在 OrganizationService
     * 类完成。如下所示。
     *
     * @Service
     * public class OrganizationService {
     *
     *     @Autowired
     *     private OrganizationRepository orgRepository;
     *
     *     @Autowired
     *     SimpleSourceBean simpleSourceBean;
     *
     *     public Organization getOrg(String organizationId) {
     *         return orgRepository.findById(organizationId);
     *     }
     *
     *     public void saveOrg(Organization org){
     *         org.setId(UUID.randomUUID().toString());
     *
     *         orgRepository.save(org);
     *         simpleSourceBean.publishOrgChange("SAVE", org.getId());
     *     }
     *
     *     // ...
     *
     * }
     *
     * PS：应该在消息中放置什么数据
     *
     * 从团队中听到的一个最常见的问题是，当他们第一次开始消息之旅时，应该在消息中放置多少数据。答案是，这取决于你
     * 的应用程序。正如你可能注意到的，在这里的所有示例中，只返回已更改的组织记录的组织 ID。从来没有把数据更改的
     * 副本放在消息中。在这里的例子中（以及笔者在电话通信领域中遇到的许多问题），执行的业务逻辑对数据的变化非常敏
     * 感。这里使用基于系统事件的消息来告诉其他服务，数据状态已经发生了变化，但是这里总是强制其他服务重新到主服务
     * 器（拥有数据的服务）上来检索数据的新副本。这种方法在执行时间方面是昂贵的，但它也保证了始终拥有最新的数据副
     * 本。在从源系统读取数据之后，所使用的数据依然可能会发生变化，但这比在队列中盲目地消费信息的可能性要小得多。
     *
     * 要仔细考虑要传递多少数据。开发人员迟早会遇到这样一种情况：传递的数据已经过时了。这些数据可能是陈旧的，因为
     * 出现某种问题导致它在消息队列待了太长时间，或者之前包含数据的消息失败了，并且消息中传入的数据现在处于不一致
     * 的状态（因为应用程序依赖于消息的状态，而不是底层数据存储中的实际状态）。如果要在消息中传递状态，还要确保在
     * 消息中包含日期时间戳或版本号，以便使用数据的服务可以检查传递的数据，并确保它不会比服务已拥有的数据副本更旧
     * （记住，数据可以不按顺序进行检索）。
     *
     *
     *
     * 2、在许可证服务中编写消息消费者
     *
     * 到目前为止，已经修改了组织服务，以便在组织服务更改组织数据时向 Kafka 发布消息。任何对组织数据感兴趣的服务，
     * 都可以在不需要由组织服务显式调用的情况下作出反应。这还意味着开发人员可以轻松地添加新的功能，可以让它们监听
     * 消息队列中的消息来对组织服务中的更改作出反应。现在换一个角度，看看服务如何使用 Spring Cloud Stream 来消
     * 费消息。
     *
     * 对于本示例，将使用许可证服务消费组织服务发布的消息。如下过程展示了将许可证服务融入 Spring Cloud Stream
     * 架构中的什么地方。
     * （1）变更消息进入 Kafka 的 orgChangeTopic 主题中。
     * （2）Spring Cloud Stream 类和配置。
     * （3）将使用默认的 input 通道和自定义通道（inboudOrgChanges）来传递传入的消息。
     * （4）OrganizationChangeHandler 类处理每个传入的消息。
     *
     * PS：当一条消息进入 Kafka 的 orgChangeTopic 时，许可证服务将作出响应。
     *
     * 首先，还是需要将 Spring Cloud Stream 依赖项添加到许可证服务的 pom.xml 文件中。与之前的组织服务类似，
     * 需要添加以下两个依赖项。
     *
     * <dependency>
     *   <groupId>org.springframework.cloud</groupId>
     *   <artifactId>spring-cloud-stream</artifactId>
     * </dependency>
     * <dependency>
     *   <groupId>org.springframework.cloud</groupId>
     *   <artifactId>spring-cloud-starter-stream-kafka</artifactId>
     * </dependency>
     *
     * 接下来，需要告诉许可证服务，它需要使用 Spring Cloud Stream 绑定到消息代理。像组织服务一样，这里将使用
     * @EnableBinding 注解来标注许可证服务引导类 Application。许可证服务和组织服务之间的区别在于传递给
     * @EnableBinding 注解的值，如下所示。
     *
     * @SpringBootApplication
     * @EnableEurekaClient
     * @EnableCircuitBreaker
     * @EnableBinding(Sink.class)
     * public class Application {
     *
     *     // ...
     *
     *
     *     @StreamListener(Sink.INPUT)
     *     public void loggerSink(OrganizationChangeModel orgChange) {
     *         logger.debug("Received an event for organization id {}", orgChange.getOrganizationId());
     *     }
     *
     *     public static void main(String[] args) {
     *         SpringApplication.run(Application.class, args);
     *     }
     *
     * }
     *
     * 因为许可证服务是消息的消费者，所以将会把值 Sink.class 传递给 @EnableBinding 注解。这告诉 Spring Cloud
     * Stream 使用默认的 Spring Sink 接口。与 Spring Cloud Steam Source 接口类似，Spring Cloud Stream
     * 在 Sink 接口上公开了一个默认的通道，名为 input，它用于监听通道上的传入消息。
     *
     * 定义了想要通过 @EnableBinding 注解来监听消息之后，就可以编写代码来处理来自 input 通道的消息。为此，要
     * 使用 Spring Cloud Stream 的 @StreamListener 注解。
     *
     * @StreamListener 注解告诉 Spring Cloud Stream，每次从 input 通道接收消息，就会执行 loggerSink()
     * 方法。Spring Cloud Stream 将自动把从通道中传出的消息反序列化为一个名为 OrganizationChangeModel
     * 的 Java POJO。
     *
     * 同样，消息代理的主题到 input 通道的实际映射是在许可证服务的配置中完成的。对于许可证服务，其配置如下所示，
     * 可以在许可证服务的 application.yml 文件中找到。
     *
     * spring:
     *   cloud:
     *     stream:
     *       bindings:
     *         input:
     *           destination: orgChangeTopic
     *           content-type: application/json
     *           group: licensingGroup
     *       kafka:
     *         binder:
     *           zkNodes: localhost
     *           brokers: localhost
     *
     * 这里的配置类似于组织服务的配置。然而，上述配置有两个关键的不同之处。首先，现在有一个名为 input 的通道定义
     * 在 spring.cloud.stream.bindings 属性下。这个值映射到 Application 类中定义的 Sink.INPUT 通道，它的
     * 属性将 input 通道映射到 orgChangeTopic。其次，看到这里引入了一个名为 spring.cloud.stream.bindings
     * .input.group 的新属性。group 属性定义将要消费消息的消费者组的名称。
     *
     * 消费者组的概念是这样的：开发人员可能拥有多个服务，每个服务都有多个实例侦听同一个消息队列，但是只需要服务实
     * 例组中的一个服务实例来消费和处理消息。group 属性标识服务所属的消费者组。只要服务实例具有相同的组名，Spring
     * Cloud Stream 和底层消息代理将保证，只有消息的一个副本会被属于该组的服务实例所使用。对于许可证服务，group
     * 属性值将会是 licensingGroup。
     *
     * 如下阐述了如何使用消费者组来强制跨多个服务消费的消息只被消费一次。
     * （1）消息从组织服务进入 orgChangeTopic。
     * （2）消息恰好只由一个许可证服务实例消费，因为它们都共享同一个消费者组（licensingGroup）。
     * （3）同一消息被不同的服务（服务实例 X）消费，X 服务有不同的消费组。
     *
     *
     *
     * 3、在实际操作中查看消息服务
     *
     * 现在，每当添加、更新或删除记录时，组织服务就将向 orgChangeTopic 发布消息，并且许可证服务从同一主题接收消
     * 息。通过更新组织服务记录并观察控制台，可以看到来自许可证服务的相应日志消息，以此来查看这段代码的实际操作。
     *
     * 要更新组织服务记录，这里将在组织服务上发送 PUT 请求来更新组织的联系电话号码。将要用来执行更新的端点是 http:
     * //localhost:5555/api/organization/v1/organizations/e254f8c-c442-4ebe-a82a-e2fc1d1ff78a，要发
     * 送到端点的 PUT 调用的请求体是：
     *
     * {
     *     "contactEmail": "mark.balster@custcrmco.com",
     *     "contactName": "Mark Balster",
     *     "contactPhone": "823-555-2222",
     *     "id": "e254f8c-c442-4ebe-a82a-e2fc1d1ff78a",
     *     "name": "customer-crm-co"
     * }
     *
     * 一旦组织服务调用完成，就应该在运行服务的控制台窗口中看到输出对应的结果。
     *
     * 现在已经有两个通过消息传递相互通信的服务。Spring Cloud Stream 充当了这些服务的中间人。从消息传递的角度来
     * 看，这些服务对彼此一无所知。它们使用消息传递代理来作为中介，并使用 Spring Cloud Stream 作为消息传递代理
     * 的抽象层进行通信。
     */
    public static void main(String[] args) {

    }

}
