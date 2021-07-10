package com.siwuxie095.spring.cloud.chapter7th.example3rd;

/**
 * @author Jiajing Li
 * @date 2021-06-14 19:57:35
 */
@SuppressWarnings("all")
public class Main {

    /**
     * 从小事做起：使用 Spring 和 OAuth2 来保护单个端点
     *
     * 为了了解如何建立 OAuth2 的验证和授权功能，这里将实现 OAuth2 密码授权类型。要实现这一授权，将执行以下操作。
     * （1）建立一个基于 Spring Cloud 的 OAuth2 验证服务。
     * （2）注册一个伪 EagleEye UI 应用程序作为一个已授权的应用程序，它可以通过 OAuth2 服务验证和授权用户身份。
     * （3）使用 OAuth2 密码授权来保护 EagleEye 服务。这里不会为 EagleEye 构建 UI，而是使用 POSTMAN 模拟登
     * 录的用户对 EagleEye OAuth2 服务进行验证。
     * （4）保护许可证服务和组织服务，使它们只能被已通过验证的用户调用。
     *
     *
     *
     * 1、建立 EagleEye OAuth2 验证服务
     *
     * 就像这里所有的例子一样，OAuth2 验证服务将是另一个 Spring Boot 服务。验证服务将验证用户凭据并颁发令牌。每
     * 当用户尝试访问由验证服务保护的服务时，验证服务将确认 OAuth2 令牌是否已由其颁发并且尚未过期。
     *
     * 开始时，需要完成以下两件事。
     * （1）添加引导类所需的适当 Maven 构建依赖项。
     * （2）添加一个将作为服务的入口点的引导类。
     *
     * 要建立 OAuth2 验证服务器，需要在 authentication-service/pom.xml 文件中添加以下 Spring Cloud 依赖项：
     *
     * <dependency>
     *   <groupId>org.springframework.cloud</groupId>
     *   <artifactId>spring-cloud-security</artifactId>
     * </dependency>
     *
     * <dependency>
     *   <groupId>org.springframework.security.oauth</groupId>
     *   <artifactId>spring-security-oauth2</artifactId>
     * </dependency>
     *
     * （1）第一个依赖项 spring-cloud-security 引入了通用 Spring 和 Spring Cloud 安全库。
     * （2）第二个依赖项 spring-security-oauth2 拉取了 Spring OAuth2 库。
     *
     * 既然已经定义完Maven依赖项，那么就可以在引导类上进行工作。如下是 Application 类的代码。
     *
     * @SpringBootApplication
     * @RestController
     * @EnableResourceServer
     * // 用于告诉 Spring Cloud，该服务将作为 OAuth2 服务
     * @EnableAuthorizationServer
     * public class Application {
     *
     *     @RequestMapping(value = {"/user"}, produces = "application/json")
     *     public Map<String, Object> user(OAuth2Authentication user) {
     *         Map<String, Object> userInfo = new HashMap<>();
     *         userInfo.put("user",
     *                 user.getUserAuthentication().getPrincipal());
     *         userInfo.put("authorities",
     *                 AuthorityUtils.authorityListToSet(
     *                         user.getUserAuthentication().getAuthorities()));
     *         return userInfo;
     *     }
     *
     *     public static void main(String[] args) {
     *         SpringApplication.run(Application.class, args);
     *     }
     *
     * }
     *
     * 在这段代码中，要注意的第一样东西是 @EnableAuthorizationServer 注解。这个注解告诉 Spring Cloud，该服务
     * 将用作 OAuth2 服务，并添加几个基于 REST 的端点，这些端点将在 OAuth2 验证和授权过程中使用。
     *
     * 在这段代码中，看到的第二件事是添加了一个名为 /user（映射到 /auth/user ）的端点。当试图访问由 OAuth2 保护
     * 的服务时，将会用到这个端点。此端点由受保护服务调用，以确认 OAuth2 访问令牌，并检索访问受保护服务的用户所分
     * 配的角色。
     *
     *
     *
     * 2、使用 OAuth2 服务注册客户端应用程序
     *
     * 此时，已经有了一个验证服务，但尚未在验证服务器中定义任何应用程序、用户或角色。
     *
     * 这里可以从已通过验证服务注册 EagleEye 应用程序开始。为此，将在验证服务中创建一个名为 OAuth2Config 的类。
     *
     * 这个类将定义通过 OAuth2 验证服务注册哪些应用程序。需要注意的是，不能只因为应用程序通过 OAuth2 服务中注册
     * 过，就认为该服务能够访问任何受保护资源。
     *
     *
     * PS：验证与授权
     *
     * 开发人员混淆术语验证（authentication）和授权（authorization）的含义。验证是用户通过提供凭据来证明他们
     * 是谁的行为。授权决定是否允许用户做他们想做的事情。例如，Jim 可以通过提供用户 ID 和密码来证明他的身份，但
     * 是他可能没有被授权查看敏感数据，如工资单数据。出于这里讨论的目的，必须在授权发生之前对用户进行验证。
     *
     *
     * OAuth2Config 类定义了 OAuth2 服务知道的应用程序和用户凭据。如下所示。
     *
     * // 继承 AuthorizationServerConfigurerAdapter 类，并使用 @Configuration 注解标注这个类
     * @Configuration
     * public class OAuth2Config extends AuthorizationServerConfigurerAdapter {
     *
     *     @Autowired
     *     private AuthenticationManager authenticationManager;
     *
     *     @Autowired
     *     private UserDetailsService userDetailsService;
     *
     *     // 覆盖 configure() 方法。这定义了哪些客户端将注册到服务。
     *     @Override
     *     public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
     *         clients.inMemory()
     *                 .withClient("eagleeye")
     *                 .secret("thisissecret")
     *                 .authorizedGrantTypes("refresh_token", "password", "client_credentials")
     *                 .scopes("webclient", "mobileclient");
     *     }
     *
     *     // 该方法定义了 AuthenticationServerConfigurer 中使用的不同组件。这段代码告诉 Spring 使用
     *     // Spring 提供的默认验证管理器和用户详细信息服务
     *     @Override
     *     public void configure(AuthorizationServerEndpointsConfigurer endpoints)
     *     throws Exception {
     *         endpoints
     *                 .authenticationManager(authenticationManager)
     *                 .userDetailsService(userDetailsService);
     *     }
     *
     * }
     *
     * 在这段代码中，要注意的第一件事是，这个类扩展了 Spring 的 AuthenticationServerConfigurer 类，然后使用
     * @Configuration 注解对这个类进行了标记。AuthenticationServerConfigurer 类是 Spring Security 的核
     * 心部分，它提供了执行关键验证和授权功能的基本机制。对于 OAuth2Config 类，这里将要覆盖两个方法。第一个方法
     * 是 configure()，它用于定义通过验证服务注册了哪些客户端应用程序。configure() 方法接受一个名为 clients
     * 的 ClientDetailsServiceConfigurer 类型的参数。
     *
     * 下面来更详细地了解一下 configure() 方法中的代码。在这个方法中做的第一件事是注册哪些客户端应用程序允许访问
     * 由 OAuth2 服务保护的服务。这里使用了最广泛的术语 "访问"（access），因为这里通过检查调用服务的用户是否有
     * 权采取他们正在尝试的操作，控制了客户端应用程序的用户以后可以做什么。
     *
     * clients.inMemory()
     *     .withClient("eagleeye")
     *     .secret("thisissecret")
     *     .authorizedGrantTypes("password", "client_credentials")
     *     .scopes("webclient", "mobileclient");
     *
     * 对于应用程序的信息，ClientDetailsServiceConfigurer 类支持两种不同类型的存储：内存存储和 JDBC 存储。对
     * 本例来说，将使用 clients.inMemory() 存储。
     *
     * withClient() 和 secret() 这两个方法提供了注册的应用程序的名称（eagleeye）以及密钥（thisissecret），
     * 该密钥在 EagleEye 应用程序调用 OAuth2 服务器以接收 OAuth2 访问令牌时提供。
     *
     * 下一个方法是 authorizedGrantTypes()，它被传入一个以逗号分隔的授权类型列表，这些授权类型将由 OAuth2 服
     * 务支持。在这个服务中，将支持密码授权类型和客户端凭据授权类型。而 scopes() 方法用于定义调用应用程序在请求
     * OAuth2 服务器获取访问令牌时可以操作的范围。例如，ThoughtMechanix 可能提供同一应用程序的两个不同版本：基
     * 于 Web 的应用程序和基于手机的应用程序。在这些应用程序中都可以使用相同的客户端名称和密钥来请求对 OAuth2 服
     * 务器保护的资源的访问。然而，当应用程序请求一个密钥时，它们需要定义它们所操作的特定作用域。通过定义作用域，
     * 可以编写特定于客户端应用程序所工作的作用域的授权规则。
     *
     * 例如，可能有一个用户使用基于 Web 的客户端和手机应用程序来访问 EagleEye 应用程序。EagleEye 应用程序的每
     * 个版本都：
     * （1）提供相同的功能；
     * （2）是一个 "受信任的应用程序"，ThoughtMechanix 既拥有前端应用程序，也拥有终端用户服务。
     *
     * 因此，这里将使用相同的应用程序名称和密钥来注册 EagleEye 应用程序，但是 Web 应用程序只使用 "webclient"
     * 作用域，而手机版本的应用程序则使用 "mobileclient" 作用域。通过使用作用域，可以在受保护的服务中定义授权
     * 规则，该规则可以根据登录的应用程序限制客户端应用程序可以执行的操作。这与用户拥有的权限无关。例如，可能希望
     * 根据用户是使用公司网络中的浏览器，还是使用移动设备上的应用程序进行浏览，来限制用户可以看到哪些数据。在处理
     * 敏感客户信息（如健康记录或税务信息）时，基于数据访问机制限制数据的做法是很常见的。
     *
     * 到目前为止，已经使用 OAuth2 服务器注册了一个应用程序 EagleEye。然而，因为使用的是密码授权，所以需要在开
     * 始之前为这些用户创建用户账户和密码。
     *
     *
     *
     * 3、配置 EagleEye 用户
     *
     * 这里已经定义并存储了应用程序级的密钥名和密钥。现在要创建个人用户凭据及其所属的角色。用户角色将用于定义一组
     * 用户可以对服务执行的操作。
     *
     * Spring 可以从内存数据存储、支持 JDBC 的关系数据库或 LDAP 服务器中存储和检索用户信息（个人用户的凭据和分
     * 配给用户的角色）。
     *
     * 注意：这里希望在定义上谨慎一些。Spring 的 OAuth2 应用程序信息可以存储在内存或关系数据库中。Spring 用户
     * 凭据和安全角色可以存储在内存数据库、关系数据库或 LDAP（活动目录）服务器中。因为这里主要目的是学习 OAuth2，
     * 为了保持简单，将使用内存数据存储。
     *
     * 对于这里的代码示例，将使用内存数据存储来定义用户角色。
     *
     * 这里将定义两个用户账户，即 john.carnell 和 william.woodward。john.carnell 账户将拥有 USER 角色，
     * 而 william.woodward 账户将拥有 ADMIN 角色。
     *
     * 要配置 OAuth2 服务器以验证用户 ID，必须创建一个新类 WebSecurityConfigurer。如下所示。
     *
     * // 扩展核心 Spring Security 的 WebSecurityConfigurerAdapter
     * @Configuration
     * public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {
     *
     *     // AuthenticationManagerBean 被 Spring Security 用来处理验证
     *     @Override
     *     @Bean
     *     public AuthenticationManager authenticationManagerBean() throws Exception {
     *         return super.authenticationManagerBean();
     *     }
     *
     *     // Spring Security 使用 UserDetailsService 处理返回的用户信息，
     *     // 这些用户信息将由 Spring Security 返回
     *     @Override
     *     @Bean
     *     public UserDetailsService userDetailsServiceBean() throws Exception {
     *         return super.userDetailsServiceBean();
     *     }
     *
     *     // configure() 方法是定义用户、密码和角色的地方
     *     @Override
     *     protected void configure(AuthenticationManagerBuilder auth) throws Exception {
     *         auth
     *                 .inMemoryAuthentication()
     *                 .withUser("john.carnell").password("password1").roles("USER")
     *                 .and()
     *                 .withUser("william.woodward").password("password2").roles("USER", "ADMIN");
     *     }
     *
     * }
     *
     * 像 Spring Security 框架的其他部分一样，要创建用户（及其角色），要从扩展 WebSecurityConfigurerAdapter
     * 类并使用 @Configuration 注解标记它开始。Spring Security 的实现方式类似于将乐高积木搭在一起来制造玩具车
     * 或模型。因此，需要为 OAuth2 服务器提供一种验证用户的机制，并返回正在验证的用户的用户信息。这通过在 Spring
     * WebSecurityConfigurerAdapter 实现中定义 authenticationManagerBean() 和 userDetailsServiceBean()
     * 两个 bean 来完成。
     *
     * 这两个 bean 通过使用父类 WebSecurityConfigurerAdapter 中的默认验证 authenticationManagerBean() 和
     * userDetailsServiceBean() 方法来公开。
     *
     * 而这两个 bean 也被注入到 OAuth2Config 类中的 configure(AuthorizationServerEndpointsConfigurer
     * endpoints) 方法中。同时，这两个 bean 用于配置 /auth/oauth/token 和 /auth/user 端点。
     *
     *
     *
     * 4、验证用户
     *
     * 此时，已经拥有足够多的基本 OAuth2 服务器功能来执行应用程序，并且能够执行密码授权流程的用户验证。现在将通过
     * 使用 POSTMAN 发送 POST 请求到 http://localhost:8901/auth/oauth/token 端点并提供应用程序名称、密钥、
     * 用户 ID 和密码来模拟用户获取 OAuth2 令牌。
     *
     * 首先，需要使用应用程序名称和密钥设置 POSTMAN。这里将使用基本验证将这些元素传递到 OAuth2 服务器端点。
     *
     * 但是，这里还没有准备好执行调用来获取令牌。一旦配置了应用程序名称和密钥，就需要在服务中传递以下信息作为 HTTP
     * 表单参数。
     * （1）grant_type：正在执行的 OAuth2 授权类型。在本例中，将使用密码（password）授权。
     * （2）scope：应用程序作用域。因为这里在注册应用程序时只定义了两个合法作用域（webclient 和 mobileclient），
     * 因此传入的值必须是这两个作用域之一。
     * （3）username：用户登录的名称。
     * （4）password：用户登录的密码。
     *
     * 与一般的 REST 调用不同，这个列表中的参数不会作为 JSON 体传递。OAuth2 标准期望传递给令牌生成端点的所有参数
     * 都是 HTTP 表单参数。
     *
     * 如下展示了为 OAuth2 调用配置的 HTTP 表单参数。
     * （1）grant_type：password
     * （2）scope：webclient
     * （3）username：john.carnell
     * （4）password：password1
     *
     * 如下是从 /auth/oauth/token 调用返回的 JSON 净荷。
     *
     * {
     *     "access_token": "e9decabc-165b-4677-9190-2e0bf8341e0b",
     *     "token_type": "bearer",
     *     "refresh_token": "22d5225d-c346-4bcd-82ec-82095a355bc5",
     *     "expires_in": 42040,
     *     "scope": "webclient"
     * }
     *
     * 返回的净荷包含以下五个属性。
     * （1）access_token：OAuth2 令牌，它将随用户对受保护资源的每个服务调用一起出示。
     * （2）token_type：令牌的类型。OAuth2 规范允许定义多个令牌类型，最常用的令牌类型是
     * 不记名令牌（bearer token）。
     * （3）refresh_token：包含一个可以提交回 OAuth2 服务器的令牌，以便在访问令牌过期
     * 后重新颁发一个访问令牌。
     * （4）expires_in：这是 OAuth2 访问令牌过期前的秒数。在 Spring 中，授权令牌过期
     * 的默认值是 12 h。
     * （5）scope：此 OAuth2 令牌的有效作用域。
     *
     * 有了有效的 OAuth2 访问令牌，就可以使用验证服务中创建的 /auth/user 端点来检索与令牌相关联的用户的信息了。
     * 后续所有受保护资源都将调用验证服务的 /auth/user 端点来确认令牌并检索用户信息。
     *
     * PS：调用 /auth/user 端点时，要注意 OAuth2 访问令牌是如何作为 HTTP 首部传入的。
     *
     * 对 /auth/user 端点发出 HTTP GET 请求。在任何时候调用 OAuth2 保护的端点（包括 OAuth2 的 /auth/user
     * 端点），都需要传递 OAuth2 访问令牌。为此，要始终创建一个名为 Authorization 的 HTTP 首部，并附有 Bearer
     * XXXXX 的值。比如，在这次调用中，HTTP 首部的值是 Bearer e9decabc-165b-4677-9190-2e0bf8341e0b。传入
     * 的访问令牌是之前调用 /auth/oauth/token 端点时返回的访问令牌。
     *
     * 如果 OAuth2 访问令牌有效，/auth/user 端点就会返回关于用户的信息，包括分配给他们的角色。例如，从 /auth
     * /user 调用返回的结果可以看出，用户 john.carnell 拥有 USER 角色。
     *
     * 注意：Spring 将前缀 ROLE_ 分配给用户角色，因此 ROLE_USER 意味着 john.carnell 拥有 USER 角色。
     */
    public static void main(String[] args) {

    }

}
