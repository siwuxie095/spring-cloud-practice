package com.siwuxie095.spring.cloud.chapter4th.example6th;

/**
 * @author Jiajing Li
 * @date 2021-05-31 21:57:25
 */
@SuppressWarnings("all")
public class Main {

    /**
     * 使用服务发现来查找服务
     *
     * 现在已经有了通过 Eureka 注册的组织服务。还可以让许可证服务调用该组织服务，而不必直接知晓任何组织服务的位置。
     * 许可证服务将通过 Eureka 来查找组织服务的实际位置。
     *
     * 为了达成目的，这里将研究三个不同的 Spring/Netflix 客户端库，服务消费者可以使用它们来和 Ribbon 进行交互。
     * 从最低级别到最高级别，这些库包含了不同的与 Ribbon 进行交互的抽象层次。这里将要探讨的库包括：
     * （1）Spring DiscoveryClient；
     * （2）启用了 RestTemplate 的 Spring DiscoveryClient；
     * （3）Netflix Feign 客户端。
     *
     * 这里将介绍这些客户端，并在许可证服务的上下文中介绍它们的用法。在开始详细介绍客户端的细节之前，这里已在代码中
     * 编写了一些便利的类和方法，以便可以使用相同的服务端点来处理不同的客户端类型。
     *
     * 首先，修改了 LicenseServiceController 以包含许可证服务的新路由。这个新路由允许指定要用于调用服务的客户
     * 端的类型。这是一个辅助路由，因此，当探索通过 Ribbon 调用组织服务的各种不同方法时，可以通过单个路由来尝试
     * 每种机制。LicenseServiceController 类中新路由的代码如下所示。
     *
     *     // clientType 确定 Spring REST 要使用的客户端的类型
     *     @RequestMapping(value = "/{licenseId}/{clientType}", method = RequestMethod.GET)
     *     public License getLicensesWithClient(@PathVariable("organizationId") String organizationId,
     *                                          @PathVariable("licenseId") String licenseId,
     *                                          @PathVariable("clientType") String clientType) {
     *         return licenseService.getLicense(organizationId,licenseId, clientType);
     *     }
     *
     * 在上述代码中，该路由上传递的 clientType 参数决定了将在代码示例中使用的客户端类型。可以在此路由上传递的具体
     * 类型包括：
     * （1）Discovery：使用 DiscoveryClient 和标准的 Spring RestTemplate 类来调用组织服务；
     * （2）Rest：使用增强的 Spring RestTemplate 来调用基于 Ribbon 的服务；
     * （3）Feign：使用 Netflix 的 Feign 客户端库来通过 Ribbon 调用服务。
     *
     * 注意：因为这里对这三种类型的客户端使用同一份代码，所以可能会看到代码中出现某些客户端的注解，即使在某些情况下
     * 并不需要它们。例如，可以在代码中同时看到 @EnableDiscoveryClient 和 @EnableFeignClients 注解，即使运
     * 行的代码只解释了其中一种客户端类型。通过这种方式，就可以为示例共用一份代码。下面会在遇到它们的时候指出这些冗
     * 余和代码。
     *
     * 在 LicenseService 类添加了一个名为 retrieveOrgInfo() 的简单方法，该方法将根据传递到路由的 clientType
     * 类型进行解析，以用于查找组织服务实例。LicenseService 类上的 getLicense() 方法将使用 retrieveOrgInfo()
     * 方法从 Postgres 数据库中检索组织数据。如下代码展示了 getLicense() 方法。
     *
     *     public License getLicense(String organizationId,
     *                               String licenseId,
     *                               String clientType) {
     *         License license = licenseRepository
     *                 .findByOrganizationIdAndLicenseId(organizationId, licenseId);
     *
     *         Organization org = retrieveOrgInfo(organizationId, clientType);
     *
     *         return license
     *                 .withOrganizationName(org.getName())
     *                 .withContactName(org.getContactName())
     *                 .withContactEmail(org.getContactEmail())
     *                 .withContactPhone(org.getContactPhone())
     *                 .withComment(config.getExampleProperty());
     *     }
     *
     * PS：licenses/clients 包中可以找到使用 Spring DiscoveryClient、Spring RestTemplate
     * 或 Feign 库构建的客户端。
     *
     *
     *
     * 1、使用 Spring DiscoveryClient 查找服务实例
     *
     * Spring DiscoveryClient 提供了对 Ribbon 和 Ribbon 中缓存的注册服务的最低层次访问。
     *
     * 使用 DiscoveryClient，可以查询通过 Ribbon 注册的所有服务以及这些服务对应的 URL。
     *
     * 下面将创建一个简单的示例，使用 DiscoveryClient 从 Ribbon 中检索组织服务 URL，并使用标准 RestTemplate
     * 类调用该服务。要开始使用 DiscoveryClient，需要先使用 @EnableDiscoveryClient 注解来标注 Application
     * 类，如下所示。
     *
     * @SpringBootApplication
     * // 激活 Spring DiscoveryClient
     * @EnableDiscoveryClient
     * public class Application {
     *
     *     public static void main(String[] args) {
     *         SpringApplication.run(Application.class, args);
     *     }
     *
     * }
     *
     * @EnableDiscoveryClient 注解是 Spring Cloud 的触发器，其作用是使应用程序能够使用 DiscoveryClient 和
     * Ribbon 库。
     *
     * 如下所示，现在来看看如何通过 Spring DiscoveryClient 调用组织服务。
     *
     * @Component
     * public class OrganizationDiscoveryClient {
     *
     *     // DiscoveryClient 被自动注入这个类
     *     @Autowired
     *     private DiscoveryClient discoveryClient;
     *
     *     public Organization getOrganization(String organizationId) {
     *         RestTemplate restTemplate = new RestTemplate();
     *         // 获取组织服务的所有实例的列表
     *         List<ServiceInstance> instances =
     *                 discoveryClient.getInstances("organizationservice");
     *
     *         if (instances.size()==0) {
     *             return null;
     *         }
     *         // 检索要调用的服务端点
     *         String serviceUri = String.format("%s/v1/organizations/%s",
     *                 instances.get(0).getUri().toString(),
     *                 organizationId);
     *         System.out.println("!!!! SERVICE URI:  " + serviceUri);
     *
     *         // 使用标准的 Spring REST 模板类去调用服务
     *         ResponseEntity< Organization > restExchange =
     *                 restTemplate.exchange(
     *                         serviceUri,
     *                         HttpMethod.GET,
     *                         null, Organization.class, organizationId);
     *
     *         return restExchange.getBody();
     *     }
     *
     * }
     *
     * 在这段代码中，首先感兴趣的是 DiscoveryClient。这是用于与 Ribbon 交互的类。要检索通过 Eureka 注册的所有
     * 组织服务实例，可以使用 getInstances() 方法传入要查找的服务的关键字，以检索 ServiceInstance 对象的列表。
     *
     * ServiceInstance 类用于保存关于服务的特定实例（包括它的主机名、端口和 URI）的信息。
     *
     * 这里使用列表中的第一个 ServiceInstance 去构建目标 URL，此 URL 可用于调用服务。一旦获得目标 URL，就可以
     * 使用标准的 Spring RestTemplate 来调用组织服务并检索数据。
     *
     *
     * PS：DiscoveryClient 与实际运用
     *
     * 通过介绍 DiscoveryClient，完成了使用 Ribbon 来构建服务消费者的过程。然而，在实际运用中，只有在服务需要
     * 查询 Ribbon 以了解哪些服务和服务实例已经通过它注册时，才应该直接使用 DiscoveryClient。上述代码存在以下
     * 几个问题。
     * （1）没有利用 Ribbon 的客户端负载均衡：尽管通过直接调用 DiscoveryClient 可以获得服务列表，但是要调用哪
     * 些返回的服务实例就成为了开发人员的责任。
     * （2）开发人员做了太多的工作：现在，开发人员必须构建一个用来调用服务的 URL。尽管这是一件小事，但是编写的代
     * 码越少意味着需要调试的代码就越少。
     *
     * 善于观察的 Spring 开发人员可能已经注意到，上述代码中直接实例化了 RestTemplate 类。这与正常的 Spring
     * REST 调用相反，通常情况下，开发人员会利用 Spring 框架，通过 @Autowired 注解将 RestTemplate 注入使用
     * RestTemplate 的类中。
     *
     * 这里实例化了 RestTemplate 类，是因为一旦在应用程序类中通过 @EnableDiscoveryClient 注解启用了 Spring
     * DiscoveryClient，由 Spring 框架管理的所有 RestTemplate 都将注入一个启用了 Ribbon 的拦截器，这个拦截
     * 器将改变使用 RestTemplate 类创建 URL 的行为。直接实例化 RestTemplate 类可以避免这种行为。
     *
     * 总而言之，有更好的机制来调用支持 Ribbon 的服务。
     *
     *
     *
     * 2、使用带有 Ribbon 功能的 Spring RestTemplate 调用服务
     *
     * 接下来，将看到如何使用带有 Ribbon 功能的 RestTemplate 的示例。这是通过 Spring 与 Ribbon 进行交互的更
     * 为常见的机制之一。要使用带有 Ribbon 功能的 RestTemplate 类，需要使用 Spring Cloud 注解 @LoadBalanced
     * 来定义 RestTemplate bean 的构造方法。对于许可证服务，可以在 Application 类中找到用于创建 RestTemplate
     * bean 的方法。
     *
     * 如下代码展示了使用 getRestTemplate() 方法来创建支持 Ribbon 的 Spring RestTemplate bean。
     *
     * @SpringBootApplication
     * public class Application {
     *
     *     // @LoadBalanced 注解告诉 Spring Cloud 创建一个支持 Ribbon 的 RestTemplate 类
     *     @LoadBalanced
     *     @Bean
     *     public RestTemplate getRestTemplate(){
     *         return new RestTemplate();
     *     }
     *
     *     public static void main(String[] args) {
     *         SpringApplication.run(Application.class, args);
     *     }
     *
     * }
     *
     * 注意：在 Spring Cloud 的早期版本中，RestTemplate 类默认自动支持 Ribbon。但是，自从 Spring Cloud 发
     * 布 Angel 版本之后，Spring Cloud 中的 RestTemplate 就不再支持 Ribbon。如果要将 Ribbon 和 RestTemplate
     * 一起使用，则必须使用 @LoadBalanced 注解进行显式标注。
     *
     * 既然已经定义了支持 Ribbon 的 RestTemplate 类，任何时候想要使用 RestTemplate bean 来调用服务，就只需
     * 要将它自动装配到使用它的类中。
     *
     * 除了在定义目标服务的 URL 上有一点小小的差异，使用支持 Ribbon 的 RestTemplate 类几乎和使用标准的 Rest
     * Template 类一样。这里将使用要调用的服务的 Eureka 服务 ID 来构建目标 URL，而不是在 RestTemplate 调用
     * 中使用服务的物理位置。
     *
     * 可以通过如下代码来了解这一差异。
     *
     * @Component
     * public class OrganizationRestTemplateClient {
     *
     *     @Autowired
     *     RestTemplate restTemplate;
     *
     *     public Organization getOrganization(String organizationId){
     *         // 在使用支持 Ribbon 的 RestTemplate 时，使用 Eureka 服务 ID 来构建目标 URL
     *         ResponseEntity<Organization> restExchange =
     *                 restTemplate.exchange(
     *                         "http://organizationservice/v1/organizations/{organizationId}",
     *                         HttpMethod.GET,
     *                         null, Organization.class, organizationId);
     *
     *         return restExchange.getBody();
     *     }
     *
     * }
     *
     * 这段代码看起来和前面的例子有些类似，但是它们有两个关键的区别。首先，Spring（Cloud）DiscoveryClient 不
     * 见了；其次，你可能会对 restTemplate.exchange() 调用中使用的 URL 感到奇怪：
     *
     * restTemplate.exchange(
     * ➥  "http://organizationservice/v1/organizations/{organizationId}",
     * ➥  HttpMethod.GET,
     * ➥  null, Organization.class, organizationId);
     *
     * URL中的服务器名称与通过 Eureka 注册的组织服务的应用程序 ID —— organizationervice 相匹配：
     *
     * http://{applicationid}/v1/organizations/{organizationId}
     *
     * 启用 Ribbon 的 RestTemplate 将解析传递给它的 URL，并使用传递的内容作为服务器名称，该服务器名称作为从
     * Ribbon 查询服务实例的键。实际的服务位置和端口与开发人员完全抽象隔离。
     *
     * 此外，通过使用 RestTemplate 类，Ribbon 将在所有服务实例之间轮询负载均衡所有请求。
     *
     *
     *
     * 3、使用 Netflix Feign 客户端调用服务
     *
     * Netflix 的 Feign 客户端库是 Spring 启用 Ribbon 的 RestTemplate 类的替代方案。Feign 库采用不同的方
     * 法来调用 REST 服务，方法是让开发人员首先定义一个 Java 接口，然后使用 Spring Cloud 注解来标注接口，以
     * 映射 Ribbon 将要调用的基于 Eureka 的服务。Spring Cloud 框架将动态生成一个代理类，用于调用目标 REST
     * 服务。除了编写接口定义，开发人员不需要编写其他调用服务的代码。
     *
     * 要在许可证服务中允许使用 Feign 客户端，需要向许可证服务的 Application 类添加一个新注解 @EnableFeign
     * Clients。如下所示。
     *
     * @SpringBootApplication
     * // 需要使用 @EnableFeignClients 以在代码中启用 Feign 客户端
     * @EnableFeignClients
     * public class Application {
     *
     *     public static void main(String[] args) {
     *         SpringApplication.run(Application.class, args);
     *     }
     *
     * }
     *
     * 既然已经在许可证服务中启用了 Feign 客户端，那么就来看一个 Feign 客户端接口定义，它可以用来调用组织服务上
     * 的端点。如下代码展示了一个接口定义示例。
     *
     * // 使用 @FeignClient 注解标识服务
     * @FeignClient("organizationservice")
     * public interface OrganizationFeignClient {
     *
     *     // 使用 @RequestMapping 注解来定义端点的路径和动作
     *     @RequestMapping(
     *             method= RequestMethod.GET,
     *             value="/v1/organizations/{organizationId}",
     *             consumes="application/json")
     *     // 使用 @PathVariable 来定义传入端点的参数
     *     Organization getOrganization(@PathVariable("organizationId") String organizationId);
     *
     * }
     *
     * 这里通过使用 @FeignClient 注解来开始这个 Feign 示例，并将这个接口代表的服务的应用程序 ID 传递给它。接
     * 下来，在这个接口中定义一个 getOrganization() 方法，该方法可以由客户端调用以触发组织服务。
     *
     * 定义 getOrganization() 方法的方式看起来就像在 Spring 控制器类中公开一个端点一样。
     *
     * 首先，为 getOrganization() 方法定义一个 @RequestMapping 注解，该注解映射 HTTP 动词以及将在组织服务
     * 中公开的端点。其次，使用 @PathVariable 注解将 URL 上传递的组织 ID 映射到调用的方法的 organizationId
     * 参数。调用组织服务的返回值将被自动映射到 Organization 类，这个类被定义为 getOrganization() 方法的返
     * 回值类型。
     *
     * 要使用 OrganizationFeignClient 类，开发人员需要做的只是自动装配并使用它。Feign 客户端代码将为开发人员
     * 承担所有的编码工作。
     *
     *
     * PS：错误处理
     *
     * 在使用标准的 Spring RestTemplate 类时，所有服务调用的 HTTP 状态码都将通过 ResponseEntity 类的
     * getStatusCode() 方法返回。通过 Feign 客户端，任何被调用的服务返回的 HTTP 状态码 4xx ~ 5xx 都将
     * 映射为 FeignException。FeignException 包含可以被解析为特定错误消息的 JSON 体。
     *
     * Feign 为开发人员提供了编写错误解码器类的功能，该类可以将错误映射回自定义的异常类。有关编写错误解码器
     * 的内容超出了这里的范围，可以在 Feign GitHub 存储库中找到与此相关的示例。如下：
     * https://github.com/OpenFeign/feign/wiki/Custom-error-handling
     */
    public static void main(String[] args) {

    }

}
