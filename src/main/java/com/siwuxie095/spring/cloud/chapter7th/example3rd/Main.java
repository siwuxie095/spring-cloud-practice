package com.siwuxie095.spring.cloud.chapter7th.example3rd;

/**
 * @author Jiajing Li
 * @date 2021-06-14 19:57:35
 */
@SuppressWarnings("all")
public class Main {

    /**
     * 从小事做起：使用 Spring 和 OAuth2 保护一个单独的端点
     *
     * 了解如何创建 OAuth2 的身份验证和授权部分，你要实现 OAuth2 密码授权类型。要实现此授权，你需要做以下事情：
     * （1）创建一个基于 Spring Cloud 的 OAuth2 认证服务。
     * （2）注册一个仿制的 EagleEyeUI 应用程序作为一个授权的应用程序，使用 OAuth2 服务可以进行身份验证和授权
     * 用户身份。
     * （3）使用 OAuth2 密码授权来保护你的 EagleEye 服务。你不会为 EagleEye 构建 UI，所以你会使用 POSTMAN
     * 模拟一个用户登录，用你的 EagleEye OAuth2 服务来进行身份验证。
     * （4）保护许可和组织服务，以便它们只能由经过身份验证的用户调用。
     *
     *
     *
     * 1、配置 EagleEye OAuth2 认证服务
     *
     * 和其他服务一样，你的 OAuth2 认证服务将是另一个 Spring Boot 服务。认证服务将对用户凭据进行身份验证并发
     * 出令牌。每当用户试图通过认证服务访问受保护的服务，认证服务将验证它发布的 OAuth2 令牌并确认它没有过期。
     *
     * 开始之前，你要做两件事：
     * （1）为你的引导类引入需要的适当的 Maven 构建依赖。
     * （2）引导类将作为服务的入口点。
     *
     * 为建立一个 OAuth2 认证服务器，你需要在 authentication-service/pom.xml 文件添加以下 Spring Cloud
     * 依赖：
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
     * 第一个依赖是 spring-cloud-security，它引入了传统的 Spring 和 Spring Cloud security 库。第二个依
     * 赖是 spring-security-oauth2，从 Spring OAuth2 库拉取。
     *
     * 现在，已经定义了 Maven 依赖，你可以在引导类继续工作。如下所示。
     *
     * @SpringBootApplication
     * @RestController
     * @EnableResourceServer
     * @EnableAuthorizationServer
     * public class Application {
     *
     *     @RequestMapping(value = { "/user" }, produces = "application/json")
     *     public Map<String, Object> user(OAuth2Authentication user) {
     *         Map<String, Object> userInfo = new HashMap<>();
     *         userInfo.put("user", user.getUserAuthentication().getPrincipal());
     *         userInfo.put("authorities", AuthorityUtils.authorityListToSet(
     *         user.getUserAuthentication().getAuthorities()));
     *         return userInfo;
     *     }
     *
     *
     *     public static void main(String[] args) {
     *         SpringApplication.run(Application.class, args);
     *     }
     *
     * }
     *
     * 这里要注意的第一件事是 @EnableAuthorizationServer 注解。这个注解告诉 Spring Cloud，这个服务将被作
     * 为一个 OAuth2 服务和添加一些基于 REST 的端点，这些端点将被用于 OAuth2 认证和授权处理。
     *
     * 第二件事是你会看到一个称为 /user（它映射到/auth/user）端点的加入。后续当你试图访问受 OAuth2 保护的服
     * 务，你将会使用这个端点。这个端点被受保护的服务调用来验证 OAuth2 访问令牌和检索用户访问受保护服务的指定
     * 角色。
     *
     *
     *
     * 2、在 OAuth2 服务注册客户端应用
     *
     * 此时，你拥有一个认证服务，但尚未在身份验证服务器中定义任何的应用程序、用户或角色。你可以开始在你的认证服务
     * 注册 EagleEye 应用。为了这样做，你将在你的认证服务创建一个称为 OAuth2Config 的额外类。
     *
     * 这个类将定义哪些应用程序在你的 OAuth2 认证服务注册。需要注意的是，仅仅因为一个应用程序在你的 OAuth2 服
     * 务被注册，但这并不意味着服务可以访问任意受保护的资源。
     *
     *
     * PS：关于认证与授权
     *
     * 开发人员经常会 "混合搭配" 术语认证和授权的含义。认证是用户通过提供凭证来证明他们是谁的行为。授权决定用户
     * 是否被允许做他们想做的事情。例如，用户 Jim 可以通过提供用户 ID 和密码来证明自己的身份，但他可能没有权限
     * 查看诸如工资数据之类的敏感数据。为了这里讨论的目的，在授权发生之前必须对用户进行身份验证。
     *
     * OAuth2Config 类可以让 OAuth2 服务知道的应用程序和用户凭证。如下所示。
     *
     * @Configuration
     * public class OAuth2Config extends AuthorizationServerConfigurerAdapter {
     *
     *     @Autowired
     *     private AuthenticationManager authenticationManager;
     *
     *     @Autowired
     *     private UserDetailsService userDetailsService;
     *
     *     @Override
     *     public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
     *         clients.inMemory()
     *                 .withClient("eagleeye")
     *                 .secret("thisissecret")
     *                 .authorizedGrantTypes("refresh_token", "password", "client_credentials")
     *                 .scopes("webclient", "mobileclient");
     *     }
     *
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
     * 这里注意到的第一件事是你扩展了 Spring 的 AuthenticationServerConfigurer 类，然后使用 @Configuration
     * 注解标记类。这个 AuthenticationServerConfigurer 类是 Spring 安全的核心部分。它提供了实现密钥认证和授权
     * 功能的基本机制。对于 OAuth2Config 类你将要重写两个方法。第一个方法，configure()，被用于定义什么客户端应用
     * 程序在你的认证服务注册。这个 configure() 方法需要一个称为 clients，类型为 ClientDetailsServiceConfigurer
     * 的参数。
     *
     * 下面开始更详细的了解 configure() 方法。你在这个方法的第一件事就是注册客户端应用程序，它可以访问受 OAuth2
     * 服务保护的服务。这里用了最广泛的术语 "访问"，因为你可以通过检查被调用服务的用户是否被授权采取他们要采取的行
     * 动来控制客户端应用程序的用户稍后可以做什么：
     *
     *         clients.inMemory()
     *                 .withClient("eagleeye")
     *                 .secret("thisissecret")
     *                 .authorizedGrantTypes("refresh_token", "password", "client_credentials")
     *                 .scopes("webclient", "mobileclient");
     *
     * ClientDetailsServiceConfigurer 类支持应用程序信息的两种不同的存储类型：内存存储和 JDBC 存储。在这个
     * 例子中，你将使用 clients.inMemory() 内存存储。
     *
     * 这两个称为 withClient() 和 secret() 的方法提供应用程序（EagleEye）的名称，该应用程序连同一把密钥（密
     * 码，thisissecret）一起注册，当 EagleEye 应用程序调用 OAuth2 服务器来接收一个 OAuth2 访问令牌时密钥
     * 将被提交。
     *
     * 下一个方法，authorizedGrantTypes()，传递的是一个逗号分隔的授权批准类型的列表，会在你的 OAuth2 服务
     * 支持列表。在你的服务中，你将支持密码和客户凭证授权。scopes() 方法用于定义当他们询问你的 OAuth2 服务器
     * 访问令牌时，调用的应用程序可以运行的边界。
     *
     * 例如，Thoughtmechanix 可以提供相同的应用程序的两个不同的版本，一个 Web 的应用程序和移动端应用程序。这
     * 些应用程序可以使用相同的客户端名称和密钥，通过 OAuth2 服务器请访问受保护的资源。然而，当应用程序需要一
     * 个密钥时，他们需要定义它们所运行的特定范围。通过定义作用域，你可以编写特定于客户端应用程序正在工作的范围
     * 的授权规则。
     *
     * 例如，你可能有一个用户既可以访问 EagleEye 应用的 Web 客户端应用程序，也可以访问移动端应用程序。应用程
     * 序的每个版本都做以下事情：
     * （1）提供相同的功能。
     * （2）是一个 "受信任的应用程序"，ThoughtMechanix 同时拥有 EagleEye 的前端应用程序和后端用户服务。
     *
     * 因此，你将使用相同的应用名称和密钥注册 EagleEye 应用，而 Web 应用程序只会在 "webclient" 范围使用，而
     * 移动端应用程序将在 "mobileclient" 范围使用。通过使用作用域，你可以在受保护的服务中定义授权规则，这些规
     * 则可以限制应用程序客户根据他们登录的应用程序可以采取什么操作。这将不考虑用户拥有什么权限。例如，你可能希
     * 望根据用户在公司的内部网络中使用浏览器而不是在移动设备上浏览应用程序来限制用户可以看到哪些数据。在处理敏
     * 感客户信息（例如健康记录或税务信息）时，基于数据访问机制限制数据的实践是常见的。
     *
     * 此时，你已经注册了一个单独的应用程序，EagleEye，以及 OAuth2 服务器。但是，由于你使用的是密码授权，所以
     * 在开始之前，你需要为这些用户设置用户账户和密码。
     *
     *
     *
     * 3、配置 EagleEye 用户
     *
     * 你已经定义并存储了应用程序名称和密钥。现在你将创建个人用户凭证和它们所属亍的角色。用户角色将用于定义一组
     * 用户可以使用一个服务所做的操作。
     *
     * Spring 可以从内存数据存储、JDBC 支持的关系数据库或 LDAP 服务器中存储和检索用户信息（个人用户的凭证和
     * 分配给用户的角色）。
     *
     * 注意：就定义而言，这里要注意一些事项。Spring 的 OAuth2 应用信息可以将它的数据存储在内存或关系数据库中。
     * Spring 用户凭证和安全角色可以存储在内存数据库、关系数据库或 LDAP（活动目录）服务器中。因为这里的主要目
     * 的是了解 OAuth2，为保持事情简单，你将使用一个内存数据存储。
     *
     * 这里你要使用一个内存数据存储定义用户角色。你要定义两个用户账户：john.carnell 和 william.woodward。
     * john.carnell 将拥有 USER 角色，william.woodward 将拥有 ADMIN 角色。
     *
     * 为了配置你的 OAuth2 服务器进行用户 ID 认证，你需要创建一个新的类：WebSecurityConfigurer。如下所示。
     *
     * @Configuration
     * public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {
     *
     *     @Override
     *     @Bean
     *     public AuthenticationManager authenticationManagerBean() throws Exception {
     *         return super.authenticationManagerBean();
     *     }
     *
     *     @Override
     *     @Bean
     *     public UserDetailsService userDetailsServiceBean() throws Exception {
     *         return super.userDetailsServiceBean();
     *     }
     *
     *
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
     * 与 Spring 安全框架的其他部分一样，要创建用户（以及他们的角色），首先扩展 WebSecurityConfigurerAdapter
     * 类并用 @Configuration 注解标记它。Spring 安全的实现方式类似于如何将乐高积木组合起来构建一个玩具汽车或模
     * 型。因此，你需要为 OAuth2 服务器提供对用户进行身份验证和返回认证用户的用户信息的机制。这是通过在 Spring
     * WebSecurityConfigurerAdapter 实现中定义两个 bean 完成的： authenticationManagerBean() 和
     * userDetailsServiceBean()。 通过使用父类 WebSecurityConfigurerAdapter 默认的验证方法
     * authenticationManagerBean() 和 userDetailsServiceBean() 来暴露这两个 bean。
     *
     * 这些 bean 会被注入到在 OAuth2Config 类中的 configure(AuthorizationServerEndpointsConfigurer
     * endpoints) 方法：
     *
     *     @Override
     *     public void configure(AuthorizationServerEndpointsConfigurer endpoints)
     *     throws Exception {
     *         endpoints
     *                 .authenticationManager(authenticationManager)
     *                 .userDetailsService(userDetailsService);
     *     }
     *
     * 这两个 bean 用于配置下面将看到的 /auth/oauth/token 和 /auth/user 端点。
     *
     *
     *
     * 4、用户认证
     *
     * 此刻，你有足够的基于 OAuth2 服务器的功能来执行密码授权流程的应用和用户认证。现在，你将模拟用户通过使用
     * POSTMAN 提交 http://localhost:8901/auth/oauth/token 端点，并提供应用名称、密钥、用户 ID 和密码，
     * 获得一个 OAuth2 令牌。
     *
     * 首先，你需要在 POSTMAN 设置应用程序名称和密钥。你将传递这些用于基本身份验证的 OAuth2 服务器端点的元素。
     *
     * 但是，你还没有准备发起调用来获取令牌。一旦配置了应用程序名称和密钥，就需要将服务中的下列信息作为 HTTP
     * 表单参数传递：
     * （1）grant_type：OAuth2 授权你执行的类型。在本例中，你将使用密码授权。
     * （2）scope：应用范围。因为在注册应用程序时只定义了两个合法作用域（webclient 和 mobileclient），传入
     * 的值必须是这两个范围中的一个。
     * （3）username：登录用户的名称。
     * （4）password：用户登录密码。
     *
     * 与其他 REST 调用不同，该列表中的参数不会作为 JavaScript 报文体传入。OAuth2 标准希望所有的参数通过
     * HTTP 表单参数传递到令牌生成的端点。
     *
     * 当请求 /auth/oauth/token 端点后，返回的 JSON 包含了五个属性：
     * （1）access_token：OAuth2 令牌随用户向一个受保护资源发出的每个服务调用一起出现。
     * （2）token_type：令牌类型。OAuth2 规范允许你定义多个令牌类型。最常用的令牌类型是 bearer 令牌。在这
     * 里中，不涉及任何其他令牌类型。
     * （3）refresh_token：包含一个令牌，在令牌过期后它可以返回到 OAuth2 服务器来重新发布一个令牌。
     * （4）expires_in：这是 OAuth2 访问令牌过期前的秒数。Spring 中授权令牌过期的默认值是 12 小时。
     * （5）scope：OAuth2 令牌的有效范围。
     *
     * 现在，你有一个有效的 OAuth2 访问令牌，可以使用你在你的认证服务中创建的 /auth/user 端点，来检索与令牌
     * 相关的用户信息。后续将要被保护资源的任何服务都将调用身份验证服务的/auth/user 端点来验证令牌并检索用户
     * 信息。
     *
     * 调用 /auth/user 端点时，注意 OAuth2 访问令牌是如何作为 HTTP 头传递的。
     *
     * 你以 GET 方式发出了对 /auth/user 端点的 HTTP 请求。然而，任何时候你需要通过 OAuth2 访问令牌，调用
     * 一个受 OAuth2 保护的端点（包括 OAuth2 端点 /auth/user）。要做到这一点，通常需要创建一个称为
     * Authorization 的 HTTP 头，其值为 Bearer XXXXX（XXXXX 即为访问令牌）。传入的访问令牌 XXXXX 是在
     * 你调用 /auth/oauth/token 端点时返回。
     *
     * 如果 OAuth2 访问令牌是有效的，/auth/user 端点将返回关于用户的信息，包括分配给他们的角色。
     *
     * 注意：Spring 将前缀 ROLE_ 分配给用户的角色，所以 ROLE_USER 意味着 john.carnell 具有 USER 角色。
     */
    public static void main(String[] args) {

    }

}
