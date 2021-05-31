package com.siwuxie095.spring.cloud.chapter4th.example6th;

/**
 * @author Jiajing Li
 * @date 2021-05-31 21:57:25
 */
@SuppressWarnings("all")
public class Main {

    /**
     * 使用服务发现查找服务
     *
     * 组织服务已注册到 Eureka。你还可以在不直接知道组织服务位置的情况下，使用许可服务调用组织服务。许可
     * 服务将使用 Eureka 查找组织服务的物理位置。
     *
     * 为了便于理解，这里将研究三种不同的 Spring/Netflix 客户端库，其中服务消费者可以与 Ribbon 交互。
     * 这些库将从最底下的抽象层转移到与最高级别的 Ribbon 交互。这里将探究的库包括：
     * （1）Spring Discovery client
     * （2）Spring Discovery client enabled RestTemplate
     * （3）Netflix Feign client
     *
     * 下面了解一下这些客户端，并查看它们在许可服务中的使用情况。在开始讨论客户端的细节之前，在代码中编写
     * 了一些便利类和方法，这样你就可以使用相同的服务端点与不同的客户端类型进行交互。
     *
     * 首先，这里修改了 LicenseServiceController 包括许可服务的新路由。这个新路由将允许你指定要调用服
     * 务的客户端类型。这是一个辅助路由，因此在探究通过 Ribbon 调用组织服务的每一种不同方法时，你可以通过
     * 一条路由尝试每个机制。
     *
     * 如下显示 LicenseServiceController 类中新路由的代码。
     *
     *     @RequestMapping(value="/{licenseId}/{clientType}",method = RequestMethod.GET)
     *     public License getLicensesWithClient( @PathVariable("organizationId") String organizationId,
     *                                           @PathVariable("licenseId") String licenseId,
     *                                           @PathVariable("clientType") String clientType) {
     *         return licenseService.getLicense(organizationId,licenseId, clientType);
     *     }
     *
     * 这段代码，由路由传递的 clientType 参数会限制将要在代码示例中使用的客户端类型。在路由这部分内容中，
     * 你可以了解的具体类型包括：
     * （1）发现：使用发现客户端和一个标准的 Spring RestTemplate 类调用组织服务。
     * （2）Rest：使用增强 Spring RestTemplate 调用基于 Ribbon 的服务。
     * （3）Feign：使用 Netflix 的 Feign 客户端库通过 Ribbon 调用服务。
     *
     * 注意：因为这里对所有这三种类型的客户端都使用相同的代码，所以你可能会看到某些客户端的注解，即使它们
     * 看起来不需要。例如，你会在代码中看到 @EnableDiscoveryClient 和 @EnableFeignClients 注解，
     * 即使这个版本只能解释其中一个客户类型。因此这里可以用一个代码库作为例子。每当遇到这些冗余和代码时，
     * 都会调用它们。
     *
     * 在 LicenseService 类，添加了一个简单的方法称为 retrieveOrgInfo()，它将解析基于 clientType
     * 传递到路由的客户端类型，该方法将被用于查找一个组织服务实例。LicenseService 类的 getLicense()
     * 方法，将使用 retrieveOrgInfo() 从 Postgres 数据库检索组织的数据。
     *
     *     public License getLicense(String organizationId,String licenseId, String clientType) {
     *         License license = licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId);
     *
     *         Organization org = retrieveOrgInfo(organizationId, clientType);
     *
     *         return license
     *                 .withOrganizationName( org.getName())
     *                 .withContactName( org.getContactName())
     *                 .withContactEmail( org.getContactEmail() )
     *                 .withContactPhone( org.getContactPhone() )
     *                 .withComment(config.getExampleProperty());
     *     }
     *
     * 你可以在 clients 包中找到使用 Spring DiscoveryClient、Spring RestTemplate 或 Feign 库创
     * 建的每一个客户端。
     *
     *
     *
     * 1、使用 Spring Discovery Client 查找服务实例
     *
     * Spring DiscoveryClient 提供了对 Ribbon 及其内部注册的服务的最低访问级别。使用
     * DiscoveryClient，可以用 Ribbon 客户端查询已注册的所有服务和及其相应的 URLs。
     *
     * 接下来，你将构建一个使用 DiscoveryClient 从 Ribbon 取回一个组织服务的 URL 然后
     * 使用标准 RestTemplate 类调用该服务的简单例子。开始使用 DiscoveryClient，你首先
     * 需要使用 @EnableDiscoveryClient 注解注释 Application 类，如下所示。
     *
     * @SpringBootApplication
     * @EnableDiscoveryClient
     * public class Application {
     *
     *     public static void main(String[] args) {
     *         SpringApplication.run(Application.class, args);
     *     }
     *
     * }
     *
     * @EnableDiscoveryClient 注解是 Spring Cloud 让应用程序使用 DiscoveryClient 和 Ribbon 库
     * 的触发器。
     *
     * 现在，可以看看通过 Spring DiscoveryClient 调用组织服务的代码的实现，如下所示。
     *
     * @Component
     * public class OrganizationDiscoveryClient {
     *
     *     @Autowired
     *     private DiscoveryClient discoveryClient;
     *
     *     public Organization getOrganization(String organizationId) {
     *         RestTemplate restTemplate = new RestTemplate();
     *         List<ServiceInstance> instances = discoveryClient.getInstances("organizationservice");
     *
     *         if (instances.size()==0) {
     *             return null;
     *         }
     *         String serviceUri = String.format("%s/v1/organizations/%s",instances.get(0).getUri()
     *         .toString(), organizationId);
     *         System.out.println("!!!! SERVICE URI:  " + serviceUri);
     *
     *         ResponseEntity< Organization > restExchange =
     *                 restTemplate.exchange(
     *                         serviceUri,
     *                         HttpMethod.GET,
     *                         null, Organization.class, organizationId);
     *
     *         return restExchange.getBody();
     *     }
     * }
     *
     * 代码中感兴趣的第一项是 DiscoveryClient。你将使用这个类与 Ribbon 交互。检索已经在 Eureka 注册
     * 的组织服务的所有实例，你可以使用 getInstances() 方法，通过你要找的服务的键检索 ServiceInstance
     * 对象列表。
     *
     * ServiceInstance 类被用于存储一个包含主机名，端口和 URI 指定服务实例的信息。
     *
     * 这里你首先使用 ServiceInstance 类列表中建立一个目标 URL，然后可以用来调用你的服务。一旦你有一
     * 个目标的 URL，你可以使用一个标准的 Spring RestTemplate 调用你的组织服务和检索数据。
     *
     *
     * PS：现实中的 DiscoveryClient
     *
     * 这里将通过介绍 DiscoveryClient 来完成用 Ribbon 创建服务消费者的讨论。现实的情况是，当你的服务
     * 需要查询 Ribbon 来了解什么服务和服务实例被注册时，你应该直接使用 DiscoveryClient。这段代码有几
     * 个问题，包括以下几点：
     * （1）你没有利用 Ribbon 的客户端负载平衡。通过直接调用 DiscoveryClient，你得到一个服务列表，但
     * 它会成为你的责任，选择那一个服务实例返回是你要调用的。
     * （2）你做的工作太多了。现在，您必须构建将用于调用服务的 URL。这是一件小事，但是你可以避免编写的每
     * 一段代码都是你不得不调试的一部分代码。
     *
     * 细心的 Spring 开发人员可能已经注意到，你在代码直接实例化 RestTemplate 类。这是与正常的 Spring
     * REST 调用是对立的，通常你会在 Spring 框架通过使用 @Autowired 注解注入 RestTemplate 类。
     *
     * 你在这里实例化 RestTemplate 类，因为一旦你通过 @EnableDiscoveryClient 注解在应用类中启用
     * Spring DiscoveryClient，通过 Spring 框架管理所有的 RestTemplates 将有一个启用 Ribbon
     * 的拦截器注入到框架中，它将改变 URLs 在 RestTemplate 类如何被创建。直接实例化 RestTemplate
     * 类允许你避免这种行为。
     *
     * 总之，有更好的机制来调用带 Ribbon 的服务。
     *
     *
     *
     * 2、使用 Ribbon-aware Spring RestTemplate 调用服务
     *
     * 接下来，看一个例子，如何使用 RestTemplate，即 Ribbon-aware。这是通过 Spring 与 Ribbon 交互
     * 的更常见的机制之一。为了使用一个 Ribbon-aware RestTemplate 类，你需要定义一个 RestTemplate
     * bean，并使用 Spring Cloud 的 @LoadBalanced 注解注释构造方法。对亍许可服务，这种方法被用于创
     * 建 RestTemplate bean，在 Application 可看到其源代码。
     *
     * 如下显示了 getRestTemplate() 方法，它将创建支持 Ribbon 的 Spring RestTemplate bean。
     *
     * @SpringBootApplication
     * public class Application {
     *
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
     * 注意:在 Spring Cloud 的早期版本，RestTemplate 类自动支持 Ribbon。这是默认行为。然而，从
     * Spring Cloud 的 Angel 版本，在 Spring Cloud 的 RestTemplate 不再支持 Ribbon。如果你
     * 想在 RestTemplate 使用 Ribbon，你必须明确使用 @LoadBalanced 注解来注释。
     *
     * 现在，支持 Ribbon 的 RestTemplate 被定义为 bean，任何时候你想用 RestTemplate bean 调用
     * 一个服务，你只需要将其自动注入类就可使用它。
     *
     * 使用支持 Ribbon 的 RestTemplate 类几乎非常像一个标准的 Spring RestTemplate 类，除了一个
     * 小的差异，即目标服务的 URL 如何被定义。RestTemplate 调用不是使用服务的物理位置，你要使用你
     * 想调用服务在 Eureka 注册的服务 ID 创建目标 URL。
     *
     * 如下可以看到这个差异。
     *
     * @Component
     * public class OrganizationRestTemplateClient {
     *
     *     @Autowired
     *     RestTemplate restTemplate;
     *
     *     public Organization getOrganization(String organizationId){
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
     * 除了两个关键区别外，这段代码应该与前一个示例有点类似。首先，Spring Cloud DiscoveryClient 不见
     * 了。第二，URL 在 restTemplate.exchange() 调用被使用，看起来有些奇怪。
     *
     * URL 中的服务器名称相匹配应用程序 ID，这是你在 Eureka 注册组织服务使用的 organizationservice
     * 键。
     *
     * 启用 Ribbon 的 RestTemplate 将解析传递进来的 URL，通过传入的服务名称作为键用 Ribbon 查询一个
     * 服务实例。实际的服务位置和端口完全从开发人员那里抽象出来。
     *
     * 此外，使用 RestTemplate 类，Ribbon 将轮询 robin，负载均衡所有服务实例之间的所有请求。
     *
     *
     *
     * 3、使用 Netflix Feign client 调用服务
     *
     * Netflix 的 Feign 客户端库是一种替代 Spring Ribbon-enabled RestTemplate 类的选择。Feign
     * 库采用一种不同的方法，通过开发人员首先定义一个 Java 接口并使用 Spring Cloud 注解注释接口来映
     * 射 Ribbon 将调用基于 Eureka 的服务， 调用一个 REST 服务。Spring Cloud 框架动态生成一个代理
     * 类，它将被用于调用目标 REST 服务。除了接口定义之外，没有编写调用服务的代码。
     *
     * 为了使许可服务启用 Feign 客户端，你需要在许可服务的 Application 类添加一个新注解，
     * 即 @EnableFeignClients。如下显示了此代码。
     *
     * @SpringBootApplication
     * @EnableFeignClients
     * public class Application {
     *
     *     public static void main(String[] args) {
     *         SpringApplication.run(Application.class, args);
     *     }
     *
     * }
     *
     * 现在你已经在你的许可服务中启用 Feign 客户端，接下来看看一个 Feign client 接口定义，它可以被用
     * 于调用组织服务的一个端点。如下显示了一个示例。
     *
     * @FeignClient("organizationservice")
     * public interface OrganizationFeignClient {
     *
     *     @RequestMapping(
     *             method= RequestMethod.GET,
     *             value="/v1/organizations/{organizationId}",
     *             consumes="application/json")
     *     Organization getOrganization(@PathVariable("organizationId") String organizationId);
     *
     * }
     *
     * 你用 @FeignClient 注解开始 Feign 示例，并定义你想表示的接口的服务应用程序 ID。下一步，在你的
     * 接口，你将定义一个方法 getOrganization()，在客户端调用组织服务时被调用。
     *
     * 你如何定义 getOrganization() 方法看起来就像你如何在一个 Spring 控制器类暴露一个端点。首先，
     * 你将使用 @RequestMapping 注解定义 getOrganization() 方法，该注解映射 HTTP 动词和暴露在
     * 组织服务调用的端点。其次，你使用 @PathVariable 注解，将在 URL 中传递的组织 ID 映射到方法调用
     * 的 organizationId 参数。调用组织服务的返回值将被自动映射到 Organization 类，该类被定义为
     * getOrganization() 方法的返回值。
     *
     * 使用 OrganizationFeignClient 类，所有你需要做的是自动装配和使用它。Feign 客户端代码会为你
     * 处理所有的编码工作。
     *
     *
     * PS：对错误的处理
     *
     * 当你使用标准的 Spring RestTemplate 类，所有服务调用的 HTTP 状态码将通过 ResponseEntity
     * 类的 getStatusCode() 方法返回。对于 Feign 客户端，任何 HTTP 4xx–5xx 状态码返回通过被调用
     * 的服务将被映射到一个 FeignException。FeignException 将包含一个 JSON 报文体，它将被解析为
     * 具体的错误信息。
     *
     * Feign 为你提供编写错误解码器类的能力，它将返回的错误映射为一个自定义异常类。编写这个解码器超出
     * 了这里的范围，但你可以在 Feign GitHub 仓库找到示例。如下：
     * https://github.com/OpenFeign/feign/wiki/Custom-error-handling
     */
    public static void main(String[] args) {

    }

}
