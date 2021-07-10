package com.siwuxie095.spring.cloud.chapter7th.example4th;

/**
 * @author Jiajing Li
 * @date 2021-06-15 08:40:53
 */
@SuppressWarnings("all")
public class Main {

    /**
     * 使用 OAuth2 保护组织服务
     *
     * 一旦通过 OAuth2 验证服务注册了一个应用程序，并且建立了拥有角色的个人用户账户，就可以开始探索如何使用 OAuth2
     * 来保护资源了。虽然创建和管理 OAuth2 访问令牌是 OAuth2 服务器的职责，但在 Spring 中，定义哪些用户角色有权
     * 执行哪些操作是在单个服务级别上发生的。
     *
     * 要创建受保护资源，需要执行以下操作：
     * （1）将相应的 Spring Security 和 OAuth2 jar 添加到要保护的服务中；
     * （2）配置服务以指向 OAuth2 验证服务；
     * （3）定义谁可以访问服务。
     *
     * 下面从一个最简单的例子开始，将组织服务创建为受保护资源，并确保它只能由已通过验证的用户来调用。
     *
     *
     *
     * 1、将 Spring Security 和 OAuth2 jar 添加到各个服务
     *
     * 与通常的 Spring 微服务一样，必须要向组织服务的 Maven organization-service/pom.xml 文件添加几个依赖项。
     * 在这里，需要添加两个依赖项：Spring Cloud Security 和 Spring Security OAuth2。Spring Cloud Security
     * jar 是核心的安全 jar，它包含框架代码、注解定义和用于在 Spring Cloud 中实现安全性的接口。Spring Security
     * OAuth2 依赖项包含实现 OAuth2 验证服务所需的所有类。这两个依赖项的 Maven 条目是：
     *
     * <dependency>
     *   <groupId>org.springframework.cloud</groupId>
     *   <artifactId>spring-cloud-security</artifactId>
     * </dependency>
     * <dependency>
     *   <groupId>org.springframework.security.oauth</groupId>
     *   <artifactId>spring-security-oauth2</artifactId>
     * </dependency>
     *
     *
     *
     * 2、配置服务以指向 OAuth2 验证服务
     *
     * 记住，一旦将组织服务创建为受保护资源，每次调用服务时，调用者必须将包含 OAuth2 访问令牌的 Authentication
     * HTTP 首部包含到服务中。然后，受保护资源必须调用该 OAuth2 服务来查看令牌是否有效。
     *
     * 在组织服务的 application.yml 文件中以 security.oauth2.resource.userInfoUri 属性定义回调 URL。下面
     * 是组织服务的 application.yml 文件中使用的回调配置：
     *
     * security:
     *   oauth2:
     *     resource:
     *       userInfoUri: http://localhost:8901/auth/user
     *
     * 正如从 security.oauth2.resource.userInfoUri 属性看到的，回调 URL 是 /auth/ user 端点。
     *
     * 最后，还需要告知组织服务它是受保护资源。同样，这一点可以通过向组织服务的引导类添加一个 Spring Cloud 注解
     * 来实现。组织服务的引导类代码如下所示。
     *
     * @SpringBootApplication
     * @EnableEurekaClient
     * @EnableCircuitBreaker
     * // @EnableResourceServer 注解用于告诉微服务，它是一个受保护资源
     * @EnableResourceServer
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
     * @EnableResourceServer 注解告诉 Spring Cloud 和 Spring Security，该服务是受保护资源。
     *
     * @EnableResourceServer 强制执行一个过滤器，该过滤器会拦截对该服务的所有传入调用，检查传入调用的 HTTP 首
     * 部中是否存在 OAuth2 访问令牌，然后调用 security.oauth2.resource.userInfoUri 中定义的回调 URL 来查
     * 看令牌是否有效。一旦获悉令牌是有效的，@EnableResourceServer 注解也会应用任何访问控制规则，以控制什么人
     * 可以访问服务。
     *
     *
     *
     * 3、定义谁可以访问服务
     *
     * 现在已经准备好开始围绕服务定义访问控制规则了。
     *
     * 要定义访问控制规则，需要扩展 ResourceServerConfigurerAdapter 类并覆盖 configure() 方法。在组织服务中，
     * 即 ResourceServerConfiguration 类。访问规则的范围可以从极其粗粒度（任何已通过验证的用户都可以访问整个服
     * 务）到非常细粒度（只有具有此角色的应用程序，才允许通过 DELETE 方法访问此 URL）。
     *
     * 这里不会讨论 Spring Security 访问控制规则的各种组合，只是看一些更常见的例子。这些例子包括保护资源以便：
     * （1）只有已通过验证的用户才能访问服务 URL；
     * （2）只有具有特定角色的用户才能访问服务 URL。
     *
     *
     * 3.1、通过验证用户保护服务
     *
     * 接下来要做的第一件事就是保护组织服务，使它只能由已通过验证的用户访问。
     *
     * 如下代码展示了如何将此规则构建到 ResourceServerConfiguration 类中。
     *
     * // 这个类必须使用 @Configuration 注解进行标记。ResourceServiceConfiguration 类
     * // 需要扩展 ResourceServerConfigurerAdapter
     * @Configuration
     * public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
     *
     *     // 所有访问规则都是在覆盖的 configure() 方法中定义的
     *     @Override
     *     public void configure(HttpSecurity http) throws Exception {
     *         // 所有访问规则都是通过传入方法的 HttpSecurity 对象配置的
     *         http.authorizeRequests()
     *                 .anyRequest()
     *                 .authenticated();
     *     }
     *
     * }
     *
     * 所有的访问规则都将在 configure() 方法中定义。这里将使用由 Spring 传入的 HttpSecurity 类来定义规则。在
     * 本例中，将限制对组织服务中所有 URL 的访问，仅限已通过身份验证的用户才能访问。
     *
     * 如果在访问组织服务时没有在 HTTP 首部中提供 OAuth2 访问令牌，将会收到 HTTP 响应码 401 以及一条指示需要对
     * 服务进行完整验证的消息。
     *
     * 接下来，将使用 OAuth2 访问令牌调用组织服务。要获取访问令牌，需将 access_token 字段的值从对 /auth/oauth
     * /token 端点调用所返回的 JSON 调用结果中剪切出来，并在对组织服务的调用中粘贴使用它。记住，在调用组织服务时，
     * 需要添加一个名为 Authorization 的 HTTP 首部，其值为 Bearer access_token。
     *
     * 这可能是使用 OAuth2 保护端点的最简单的用例之一。接下来，将在此基础上进行构建，并将对特定端点的访问限制在特
     * 定角色。
     *
     *
     * 3.2、通过特定角色保护服务
     *
     * 在接下来的示例中，将锁定组织服务的 DELETE 调用，仅限那些具有 ADMIN 访问权限的用户。之前已经创建了两个可以
     * 访问 EagleEye 服务的用户账户，即 john.carnell 和 william.woodward。john.carnell 账户拥有 USER 角色，
     * 而 william.woodward 账户拥有 USER 和 ADMIN 角色。
     *
     * 如下代码展示了如何创建 configure() 方法来限制对 DELETE 端点的访问，使得只有那些已通过验证并具有 ADMIN
     * 角色的用户才能访问。
     *
     * @Configuration
     * public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
     *
     *
     *     @Override
     *     public void configure(HttpSecurity http) throws Exception {
     *         http.authorizeRequests()
     *                 // antMatchers() 方法允许开发人员限制对受保护的 URL 和 HTTP DELETE 动词的调用
     *                 .antMatchers(HttpMethod.DELETE, "/v1/organizations/**")
     *                 // hasRole() 方法是一个允许访问的角色列表，该列表由逗号分隔
     *                 .hasRole("ADMIN")
     *                 .anyRequest()
     *                 .authenticated();
     *     }
     *
     * }
     *
     * 在这段代码中，将服务中以 /v1/organizations 开头的端点的 DELETE 调用限制为 ADMIN 角色：
     *
     * .authorizeRequests()
     * .antMatchers(HttpMethod.DELETE, "/v1/organizations/**")
     * .hasRole("ADMIN")
     *
     * antMatcher() 方法可以使用一个以逗号分隔的端点列表。这些端点可以使用通配符风格的符号来定义想要访问的端点。
     * 例如，如果要限制 DELETE 调用，而不管 URL 名称中的版本如何，那么可以使用 * 来代替 URL 定义中的版本号。
     *
     * 授权规则定义的最后一部分仍然定义了服务中的其他端点都需要由已通过验证的用户来访问：
     *
     * .anyRequest()
     * .authenticated();
     *
     * 现在，如果要为用户 john.carnell（密码为 password1）获取一个 OAuth2 令牌，并试图调用组织服务的 DELETE
     * 端点（http://localhost:8085/v1/organizations/e254f8c-c442-4ebe-a82a-e2fc1d1ff78a），那么将会收
     * 到 HTTP 状态码 401，以及一条指示访问被拒绝的错误消息。由调用返回的 JSON 文本将是：
     *
     * {
     *    "error": "access_denied",
     *    "error_description": "Access is denied"
     * }
     *
     * 如果使用 william.woodward 用户账户（密码：password2）及其 OAuth2 令牌尝试完全相同的调用，会看到返回一
     * 个成功的调用（HTTP 状态码 204 —— Not Content），并且该组织将被组织服务删除。
     *
     * 到目前为止，已经研究了两个简单示例，它们使用 OAuth2 调用和保护单个服务（组织服务）。然而，通常在微服务环境
     * 中，将会有多个服务调用用来执行一个事务。在这些类型的情况下，需要确保 OAuth2 访问令牌在服务调用之间传播。
     *
     *
     *
     * 4、传播 OAuth2 访问令牌
     *
     * 为了演示在服务之间传播 OAuth2 令牌，现在来看一下如何使用 OAuth2 保护许可证服务。记住，许可证服务调用组织
     * 服务查找信息。问题在于，如何将 OAuth2 令牌从一个服务传播到另一个服务？
     *
     * 这里将创建一个简单的示例，使用许可证服务调用组织服务，两个服务都在 Zuul 网关后面运行。
     *
     * 如下展示了一个已通过验证的用户的 OAuth2 令牌如何流经 Zuul 网关、许可证服务然后到达组织服务的基本流程。
     * （1）用户：用户拥有 OAuth2 令牌，并在 EagleEye Web 客户端上进行操作。
     * （2）EagleEye Web 应用程序：EagleEye Web 应用程序调用许可证服务（在 Zuul 网关后面），
     * 并将用户的 OAuth2 令牌添加到 HTTP 首部 Authorization 中。
     * （3）Zuul 网关：Zuul 网关定位许可证服务，并使用 Authorization 首部转发调用。
     * （4）许可证服务：许可证服务使用验证服务确认用户的令牌，并将令牌传播到组织服务。
     * （5）组织服务：组织服务同样使用验证服务确认用户的令牌。
     *
     * PS：必须在整个调用链中携带 OAuth2 令牌。
     *
     * 在如上流程中发生了以下活动。
     * （1）用户已经向 OAuth2 服务器进行了验证，并向 EagleEye Web 应用程序发出调用。用户的 OAuth2 访问令牌存
     * 储在用户的会话中。EagleEye Web 应用程序需要检索一些许可数据，并对许可证服务的 REST 端点进行调用。作为许
     * 可证服务的 REST 端点的一部分，EagleEye Web 应用程序将通过 HTTP 首部 Authorization 添加 OAuth2 访问
     * 令牌。许可证服务只能在 Zuul 服务网关后面访问。
     * （2）Zuul 将查找许可证服务端点，然后将调用转发到其中一个许可证服务的服务器。服务网关需要从传入的调用中复制
     * HTTP 首部 Authorization，并确保 HTTP 首部 Authorization 被转发到新端点。
     * （3）许可证服务将接收传入的调用。由于许可证服务是受保护资源，它将使用 EagleEye 的 OAuth2 服务来确认令牌，
     * 然后检查用户的角色是否具有适当的权限。作为其工作的一部分，许可证服务会调用组织服务。在执行这个调用时，许可
     * 证服务需要将用户的 OAuth2 访问令牌传播到组织服务。
     * （4）当组织服务接收到该调用时，它将再次使用 HTTP 首部 Authorization 的令牌，并使用 EagleEye OAuth2
     * 服务器来确认令牌。
     *
     * 实现这些流程需要做两件事。第一件事是需要修改 Zuul 服务网关，以将 OAuth2 令牌传播到许可证服务。在默认情况
     * 下，Zuul 不会将敏感的 HTTP 首部（如 Cookie、Set-Cookie 和 Authorization）转发到下游服务。要让 Zuul
     * 传播 HTTP 首部 Authorization，需要在 Zuul 服务网关的 application.yml 或 Spring Cloud Config 数据
     * 存储中设置以下配置：
     *
     * zuul.sensitiveHeaders: Cookie,Set-Cookie
     *
     * 这一配置是黑名单，它包含 Zuul 不会传播到下游服务的敏感首部。在上述黑名单中没有 Authorization 值就意味着
     * Zuul 将允许它通过。如果根本没有设置 zuul.sensitiveHeaders 属性，Zuul 将自动阻止三个值被传播（Cookie、
     * Set-Cookie 和 Authorization）。
     *
     *
     * PS：Zuul 的其他 OAuth2 功能呢？
     *
     * Zuul 可以自动传播下游的 OAuth2 访问令牌，并通过使用 @EnableOAuth2Sso 注解来针对 OAuth2 服务的传入请
     * 求进行授权。这里特意没有使用这种方法，因为这里的目标是，在不增加其他复杂性（或调试）的情况下，展示 OAuth2
     * 如何工作的基础知识。虽然 Zuul 服务网关的配置并不复杂，但它会在本已经拥有许多内容的情况下添加更多内容。如
     * 果你有兴趣让 Zuul 服务网关参与单点登录（Single Sign On，SSO），Spring Cloud Security 文档中有一个
     * 简短而全面的教程，它涵盖了 Spring 服务器的建立。
     *
     * 需要做的第二件事就是将许可证服务配置为 OAuth2 资源服务，并建立所需的服务授权规则。
     *
     * 最后，需要做的就是修改许可证服务中调用组织服务的代码。这里需要确保将 HTTP 首部 Authorization 注入应用程
     * 序对组织服务的调用中。如果没有 Spring Security，那么开发人员必须编写一个 servlet 过滤器以从传入的许可证
     * 服务调用中获取 HTTP 首部，然后手动将它添加到许可证服务中的每个出站服务调用中。Spring OAuth2 提供了一个
     * 支持 OAuth2 调用的新 REST 模板类 OAuth2RestTemplate。要使用 OAuth2RestTemplate 类，需要先将它公开
     * 为一个可以被自动装配到调用另一个受 OAuth2 保护的服务的服务的 bean。可以在 Application 类中执行上述操作：
     *
     *     @Bean
     *     public OAuth2RestTemplate oauth2RestTemplate(OAuth2ClientContext oauth2ClientContext,
     *                                                  OAuth2ProtectedResourceDetails details) {
     *         return new OAuth2RestTemplate(details, oauth2ClientContext);
     *     }
     *
     * 要实际查看 OAuth2RestTemplate 类的使用，可以查看 OranizationRestTemplateClient 类。如下所示。
     *
     * @Component
     * public class OrganizationRestTemplateClient {
     *
     *     // OAuth2RestTemplate 是标准 RestTemplate 的增强式替代品，可处理 OAuth2 访问令牌的传播
     *     @Autowired
     *     OAuth2RestTemplate restTemplate;
     *
     *     private static final Logger logger =
     *             LoggerFactory.getLogger(OrganizationRestTemplateClient.class);
     *
     *     public Organization getOrganization(String organizationId) {
     *         logger.debug("In Licensing Service.getOrganization: {}",
     *                 UserContext.getCorrelationId());
     *
     *         // 调用组织服务的方式与标准的 RestTemplate 完全相同
     *         ResponseEntity<Organization> restExchange =
     *                 restTemplate.exchange(
     *                         "http://zuulserver:5555/api/organization/v1/organizations/{organizationId}",
     *                         HttpMethod.GET,
     *                         null, Organization.class, organizationId);
     *
     *         return restExchange.getBody();
     *     }
     *
     * }
     */
    public static void main(String[] args) {

    }

}
