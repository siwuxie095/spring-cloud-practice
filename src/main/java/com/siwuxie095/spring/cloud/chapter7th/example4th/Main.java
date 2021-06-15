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
     * 一旦你在你的 OAuth2 认证服务已注册一个应用，并创建单独的用户账户和角色，你可以开始探索如何使用 OAuth2
     * 保护资源。虽然 OAuth2 访问令牌的创建和管理是 OAuth2 服务器的职责，但是在 Spring 中，用户角色的定义有
     * 权在单个服务级别上执行操作。
     *
     * 要建立受保护的资源，你需要采取以下操作：
     * （1）向你保护的服务添加合适的 Spring Security 和 OAuth2 JAR 包。
     * （2）配置服务指向你的 OAuth2 认证服务。
     * （3）定义哪些资源可以访问服务。
     *
     * 下面先举一个最简单的例子：通过组织服务来设置受保护的资源，并确保它只能由经过身份验证的用户调用。
     *
     *
     *
     * 1、为单独的服务添加 Spring Security 和 OAuth2 JAR 包
     *
     * 像往常使用 Spring 微服务一样，你需要向组织服务的 Maven organization-service/pom.xml 文件添加一对
     * 依赖。两个依赖被添加：Spring Cloud Security 和 Spring Security OAuth2。Spring Cloud Security
     * JAR 包是核心安全 JAR 包。它们包含在 Spring Cloud 中实现安全的框架代码、注解定义和接口。Spring
     * Security OAuth2 依赖包含实现认证服务所需的所有类。这两个 Maven 依赖项是：
     *
     *         <dependency>
     *             <groupId>org.springframework.cloud</groupId>
     *             <artifactId>spring-cloud-security</artifactId>
     *         </dependency>
     *         <dependency>
     *             <groupId>org.springframework.security.oauth</groupId>
     *             <artifactId>spring-security-oauth2</artifactId>
     *         </dependency>
     *
     *
     *
     * 2、配置服务指向 OAuth2 认证服务
     *
     * 请记住，一旦你将组织服务设置为受保护的资源，每次对服务发出调用时，调用方必须将服务的 OAuth2 访问令牌包
     * 含在 HTTP 头 Authentication 字段中。然后，你的受保护资源必须回调 OAuth2 服务，以确定令牌是否有效。
     *
     * 你在你的组织服务的 application.yml 文件的 security.oauth2.resource.userInfoUri 属性定义回调的
     * URL。这是用在组织服务的 application.yml 文件的回调配置。
     *
     * security:
     *   oauth2:
     *     resource:
     *        userInfoUri: http://localhost:8901/auth/user
     *
     * 从 security.oauth2.resource.userInfoUri 属性中可以看到，回调 URL 是指向 /auth/user 端点的。
     *
     * 最后，你还需要告诉组织服务它是受保护的资源。同样，通过向组织服务的引导类添加一个 Spring Cloud 注解来
     * 实现这一点。组织服务的引导代码可以在 Application 类中找到。
     *
     * @SpringBootApplication
     * @EnableEurekaClient
     * @EnableCircuitBreaker
     * @EnableResourceServer
     * public class Application {
     *     @Bean
     *     public Filter userContextFilter() {
     *         UserContextFilter userContextFilter = new UserContextFilter();
     *         return userContextFilter;
     *     }
     *
     *     public static void main(String[] args) {
     *         SpringApplication.run(Application.class, args);
     *     }
     * }
     *
     * @EnableResourceServer 注解告诉 Spring Cloud 和 Spring Security，该服务是受保护的资源。
     * @EnableResourceServer 强制执行一个过滤器，拦截向服务发出的所有调用，检查在传入调用的 HTTP
     * 头中是否存在 OAuth2 访问令牌，然后回调到在 security.oauth2.resource.userInfoUri 中定义
     * 的回调 URL，看看令牌是否有效。一旦它知道令牌是有效的，@EnableResourceServer 注解也适用于
     * 任何访问控制规则，即谁可以访问服务。
     *
     *
     *
     * 3、定义哪些资源可以访问服务
     *
     * 现在可以开始定义围绕服务的访问控制规则了。
     *
     * 定义访问控制规则，你需要扩展一个 Spring ResourceServerConfigurerAdapter 类并重写类的 configure()
     * 方法。在组织服务中，即是你的 ResourceServerConfiguration 类。访问规则可以从极粗粒度（任何经过身份验
     * 证的用户可以访问整个服务）到细粒度（只有具有此角色的应用程序，才允许通过 DELETE 访问此 URL）。
     *
     * 这里讨论了 Spring Security 的访问控制规则的每一个排列，但是可以看看几个比较常见的例子。这些示例包括保
     * 护资源以便：
     * （1）只有经过身份验证的用户才能访问服务 URL。
     * （2）只有具有特定角色的用户才能访问服务 URL。
     *
     *
     * 3.1、通过已身份验证的用户保护服务
     *
     * 你要做的第一件事是保护组织服务，以便它只能由经过身份验证的用户访问。下面的代码展示了如何将此规则构建到
     * ResourceServerConfiguration 类中。
     *
     * @Configuration
     * public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
     *
     *
     *     @Override
     *     public void configure(HttpSecurity http) throws Exception{
     *         http.authorizeRequests()
     *                 .anyRequest()
     *                 .authenticated();
     *     }
     * }
     *
     * 所有的访问规则将在 configure() 方法定义。你将使用 Spring 传入的 HttpSecurity 类来定义规则。在本例中，
     * 你将组织服务中对任何 URL 的访问限制为仅允许通过身份验证的用户访问。
     *
     * 如果你不使用 HTTP 标头中的 OAuth2 访问令牌访问组织服务，那么你将获得 HTTP 401 响应代码，以及一条消息，
     * 表明需要对服务进行完整的身份验证。
     *
     * 接下来，你将调用带有 OAuth2 访问令牌的组织服务。为了得到一个访问令牌，你想要剪贴从  /auth/oauth/token
     * 端点的调用返回的 access_token 字段的值，并在组织服务调用中使用它。记住，当调用组织服务时，需要添加一个
     * 名为 Authorization 的 HTTP 头，并带有持有者的 access_token 值。
     *
     * 这可能是使用 OAuth2 保护端点的最简单用例之一。接下来，你将在此基础上，将特定端点的访问限制为特定的角色。
     *
     *
     * 3.2、通过特定角色保护服务
     *
     * 在下一个示例中，你将对组织服务上的 DELETE 调用锁定为只有具有 ADMIN 访问权限的用户。之前配置 EagleEye
     * 用户，你创建了两个用户账户可以访问 EagleEye 服务：john.carnell 和 william.woodward。john.carnell
     * 账户具有分配给它的 USER 角色。william.woodward 账户具有分配给它的 USER 角色和 ADMIN 角色。
     *
     * 下面的代码显示了如何设置 configure() 方法来对通过身份验证的、有管理员角色的用户限制访问 DELETE 端点。
     *
     * @Configuration
     * public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
     *
     *
     *     @Override
     *     public void configure(HttpSecurity http) throws Exception{
     *         http.authorizeRequests()
     *                 .antMatchers(HttpMethod.DELETE, "/v1/organizations/**")
     *                 .hasRole("ADMIN")
     *                 .anyRequest()
     *                 .authenticated();
     *     }
     * }
     *
     * 在这里中，你对 ADMIN 角色限制了以 /v1/organizations 开始的任何服务端点的 DELETE 调用。
     *
     * antMatcher() 方法可以使用逗号分隔的端点列表。这些端点可以使用通配符来定义要访问的端点。例如，如果你想
     * 限制任何 DELETE 调用，不管 URL 名称中的版本是什么，你可以在 URL 定义中使用 * 代替版本号。
     *
     * 授权规则定义的最后一部分仍然定义服务中的任何其他端点都需要由经过身份验证的用户访问。
     *
     * 现在，如果你要获得用户 john.carnell 的一个 OAuth2 令牌（密码：password1）和尝试调用组织服务的
     * DELETE 端点（http://localhost:8085/v1/organizations/e254f8c-c442-4ebe-a82ae2fc1d1ff78a），
     * 你将在调用时获得 401 HTTP 状态代码，并显示拒绝访问的错误消息。你的调用返回的 JSON 文本是：
     *
     * {
     *  "error": "access_denied",
     *  "error_description": "Access is denied"
     * }
     *
     * 如果你使用用户账户 william.woodward（密码：password2）和它的 OAuth2 令牌尝试完全相同的调用，你将看
     * 到一个成功的调用返回（HTTP 状态代码 204，而不是内容），该组织将被组织服务删除。
     *
     * 此刻，已经看到了使用 OAuth2 调用和保护一个单独的服务（组织服务）的两个简单示例。然而，往往在微服务环境，
     * 你会有多个服务调用执行一个事务。在这些情况下，你需要确保 OAuth2 访问令牌在服务调用之间传递。
     *
     *
     *
     * 4、传递 OAuth2 访问令牌
     *
     * 为展示服务之间传递一个 OAuth2 令牌，现在来看如何使用 OAuth2 保护你的许可服务。记住，许可服务调用组织
     * 服务来查找信息。问题是，如何将 OAuth2 令牌从一个服务传递到另一个服务？
     *
     * 你将创建一个简单的示例，在这里你将使用许可服务调用组织服务。服务是在 Zuul 网关运行。
     *
     * OAuth2 令牌会贯穿整个调用链，流程如下：
     * （1）用户已经在 OAuth2 服务器完成了验证并向 EagleEye Web 应用发起调用。用户的 OAuth2 访问令牌存储
     * 在用户会话中。EagleEye Web 应用需要获取一些许可数据，并向许可服务 REST 端点发起调用。作为调用许可
     * REST 端点的一部分，EagleEye Web 应用将通过 HTTP 头的 "Authorization" 添加 OAuth2 访问令牌。
     * （2）Zuul 查找许可服务端点，然后转发调用到一个许可服务的服务器。服务网关需要从传入的调用复制 HTTP 头
     * 的 "Authorization"，并确保将 HTTP 头的 "Authorization" 转发到新端点。
     * （3）许可服务将收到传入的调用。因为许可服务是一个受保护的资源，许可服务将使用 EagleEye 的 OAuth2 服
     * 务验证令牌，并检查用户的角色是否有适当的权限。作为其工作的一部分，许可服务调用组织服务。在执行此调用时，
     * 许可服务需要将用户的 OAuth2 访问令牌传递到组织服务。
     * （4）当组织服务收到调用，它将再次取得 HTTP 头的 "Authorization"，并由 EagleEye 的 OAuth2 服务器
     * 验证该令牌。
     *
     * 要实现这些流程，你需要做两件事。首先，你需要修改你的 Zuul 服务网关来传递 OAuth2 令牌到许可服务。默认
     * 情况下，Zuul 不会转发敏感的 HTTP 头（如：Cookie， Set-Cookie 和 Authorization）到下游许可服务。
     * 为允许 Zuul 传递 HTTP 头的 "Authorization"，你需要在 Zuul 服务网关的 application.yml 文件或
     * Spring Cloud Config 数据存储设置以下配置：
     *
     * zuul.sensitiveHeaders: Cookie,Set-Cookie
     *
     * 这个配置是敏感的头信息黑名单列表，Zuul 将阻止它们被传递到下游服务。在前面的列表缺少 Authorization 值，
     * 意味着 Zuul 将允许它通过。如果你根本不设置 zuul.sensitiveHeaders 属性，Zuul 将自动阻止所有者三个值
     * 被传递。
     *
     *
     * PS：关于 Zuul 的其它 OAuth2 能力
     *
     * Zuul 可以自动向下游传递 OAuth2 访问令牌和通过使用 @EnableOAuth2Sso 注解授权对 OAuth2 服务传入的请
     * 求。这里故意没有使用这种方法，因为这里的目标是在不增加另一个复杂度（或调试）的情况下显示 OAuth2 的工作
     * 原理。而 Zuul 服务网关配置不能过于复杂。（SSO，即 单点登录）
     *
     *
     * 你需要做的下一件事是将许可服务配置为 OAuth2 资源服务，并设置服务所需的任何授权规则。这里不打算详细讨论
     * 许可服务配置，具体可参考上面组织服务的配置。
     *
     * 最后，你需要做的只是修改许可服务中的代码如何调用组织服务。你需要确保 HTTP 头 "Authorization" 被注入
     * 到调用了组织服务的应用程序。如果没有 Spring Security，你必须编写一个 servlet 过滤器来捕获传入的许可
     * 服务调用中的 HTTP 头，然后手动将其添加到许可服务中的每个出站服务调用中。Spring OAuth2 提供了一个支持
     * OAuth2 调用的新 REST 模板类。这类被称为 OAuth2RestTemplate。要使用 OAuth2RestTemplate 类，你首
     * 先需要将它暴露为 bean，它可以自动连接到另一个受 OAuth2 保护的服务的服务调用中。你要在 Application
     * 类中这样做：
     *
     *     @Bean
     *     public OAuth2RestTemplate oauth2RestTemplate(OAuth2ClientContext oauth2ClientContext,
     *                                                  OAuth2ProtectedResourceDetails details) {
     *         return new OAuth2RestTemplate(details, oauth2ClientContext);
     *     }
     *
     * 要查看实戓中的 OAuth2RestTemplate 类，可以在 OrganizationRestTemplateClient 中查看。下面的代码
     * 显示了 OAuth2RestTemplate 如何自动连接到这个类中。
     *
     * @Component
     * public class OrganizationRestTemplateClient {
     *     @Autowired
     *     OAuth2RestTemplate restTemplate;
     *
     *     private static final Logger logger =
     *     LoggerFactory.getLogger(OrganizationRestTemplateClient.class);
     *
     *     public Organization getOrganization(String organizationId){
     *         logger.debug("In Licensing Service.getOrganization: {}",
     *         UserContext.getCorrelationId());
     *
     *         ResponseEntity<Organization> restExchange =
     *                 restTemplate.exchange(
     *                         "http://zuulserver:5555/api/organization/v1/organizations/{organizationId}",
     *                         HttpMethod.GET,
     *                         null, Organization.class, organizationId);
     *
     *         return restExchange.getBody();
     *     }
     * }
     */
    public static void main(String[] args) {

    }

}
