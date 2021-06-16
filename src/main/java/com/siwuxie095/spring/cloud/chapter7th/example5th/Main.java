package com.siwuxie095.spring.cloud.chapter7th.example5th;

/**
 * @author Jiajing Li
 * @date 2021-06-16 21:44:19
 */
@SuppressWarnings("all")
public class Main {

    /**
     * JavaScript Web Tokens 和 OAuth2
     *
     * OAuth2 一个基于令牌的认证框架，但具有讽刺意味的是，它没有提供任何标准来定义其规范中的令牌。为了改进
     * OAuth2 令牌的不足，一个称为 JavaScript Web Tokens（JWT）的新标准出现了。JWT 是一个由互联网工程
     * 任务组（IETF）提出的开放标准（RFC-7519），试图为 OAuth2 令牌提供标准的结构。JWT 令牌是：
     * （1）小：JWT 令牌以 Base64 方式编码，可以很容易地通过一个 URL，HTTP 头或 HTTP POST 参数传递。
     * （2）加密的签名：JWT 令牌由发出它的认证服务器签名。这意味着可以保证令牌没有被篡改。
     * （3）自包含的：因为一个 JWT 令牌被加密签名，接收服务的微服务可以保证令牌的内容是有效的。没有必要回
     * 调认证服务来验证令牌的内容，因为令牌的签名可以验证，而内容（如令牌到期时间呾用户信息）可以通过接收
     * 的微服务检验。
     * （4）可扩展：当认证服务生成令牌时，它可以在令牌被密封之前在令牌中放置附加信息。接收服务可以解密令牌
     * 有效负载并从中检索附加的上下文。
     *
     * Spring Cloud Security 支持 JWT 开箱即用。但是，要使用和消费 JWT 令牌，必须使用不同的方式配置认
     * 证服务和受认证服务保护的服务。配置并不难，下面来看看更改。
     *
     *
     *
     * 1、修改认证服务来发布 JavaScript Web Tokens
     *
     * 对于认证服务和将受 OAuth2 保护的两个微服务（许可和组织服务），你将需要向它们的 Maven pom.xml 文件
     * 添加 Spring Security 依赖来包括 JWT OAuth2 库。这个新依赖是：
     *
     *         <dependency>
     *             <groupId>org.springframework.security</groupId>
     *             <artifactId>spring-security-jwt</artifactId>
     *         </dependency>
     *
     * 在加入 Maven 依赖后，首先你需要告诉你的认证服务如何生成和解析 JWT 令牌。要做到这一点，你要为认证服
     * 务创建一个称为 JWTTokenStoreConfig 的新配置类。如下所示。
     *
     * @Configuration
     * public class JWTTokenStoreConfig {
     *
     *     @Autowired
     *     private ServiceConfig serviceConfig;
     *
     *     @Bean
     *     public TokenStore tokenStore() {
     *         return new JwtTokenStore(jwtAccessTokenConverter());
     *     }
     *
     *     @Bean
     *     @Primary
     *     public DefaultTokenServices tokenServices() {
     *         DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
     *         defaultTokenServices.setTokenStore(tokenStore());
     *         defaultTokenServices.setSupportRefreshToken(true);
     *         return defaultTokenServices;
     *     }
     *
     *
     *     @Bean
     *     public JwtAccessTokenConverter jwtAccessTokenConverter() {
     *         JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
     *         converter.setSigningKey(serviceConfig.getJwtSigningKey());
     *         return converter;
     *     }
     *
     *     @Bean
     *     public TokenEnhancer jwtTokenEnhancer() {
     *         return new JWTTokenEnhancer();
     *     }
     * }
     *
     * JWTTokenStoreConfig 类用于定义 Spring 将如何管理 JWT 令牌的创建、签名和转换。
     *
     * tokenServices() 方法将使用 Spring Security 的默认令牌服务实现，所以这里的工作是死记硬背。
     *
     * jwtAccessTokenConverter() 方法是这里想要关注的。它定义了令牌将如何被转换。关于这个方法要注意的最
     * 重要的一点是，你将设置用于签名的签名密钥。
     *
     * 在这个例子中，你要使用一个对称密钥，这意味着认证服务和受认证服务保护的服务必须在所有服务之间共享相同
     * 的密钥。密钥只不过是存储在认证服务 Spring Cloud Config 条目中的一个随机字符串值。签名密钥的实际
     * 值是：
     *
     * signing.key: "345345fsdgsf5345"
     *
     * 注意：Spring Cloud Security 支持使用公钥/私钥的对称密钥加密和非对称加密。这里不打算通过使用公钥
     * /私钥来设置 JWT。不幸的是，JWT、Spring Security 和公钥/私钥只存在少量的正式文档。如果你对如何做
     * 到这一点有兴趣，可以参考：https://www.baeldung.com/spring-security-oauth-jwt。
     *
     * 在 JWTTokenStoreConfig 类中，定义了 JWT 令牌是如何被签名和创建的。现在需要将此挂钩到整个 OAuth2
     * 服务中。之前你使用 OAuth2Config 类定义 OAuth2 服务的配置。你设置了将由你的服务使用的认证管理器，
     * 以及应用程序名称和密钥。这里你将用一个称为 JWTOAuth2Config 的新类取代 OAuth2Config 类。如下所示。
     *
     * @Configuration
     * public class JWTOAuth2Config extends AuthorizationServerConfigurerAdapter {
     *
     *     @Autowired
     *     private AuthenticationManager authenticationManager;
     *
     *     @Autowired
     *     private UserDetailsService userDetailsService;
     *
     *     @Autowired
     *     private TokenStore tokenStore;
     *
     *     @Autowired
     *     private DefaultTokenServices tokenServices;
     *
     *     @Autowired
     *     private JwtAccessTokenConverter jwtAccessTokenConverter;
     *
     *     @Autowired
     *     private TokenEnhancer jwtTokenEnhancer;
     *
     *
     *
     *     @Override
     *     public void configure(AuthorizationServerEndpointsConfigurer endpoints)
     *     throws Exception {
     *         TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
     *         tokenEnhancerChain.setTokenEnhancers(
     *         Arrays.asList(jwtTokenEnhancer, jwtAccessTokenConverter));
     *
     *         endpoints.tokenStore(tokenStore)                             //JWT
     *                 .accessTokenConverter(jwtAccessTokenConverter)       //JWT
     *                 .tokenEnhancer(tokenEnhancerChain)                   //JWT
     *                 .authenticationManager(authenticationManager)
     *                 .userDetailsService(userDetailsService);
     *     }
     *
     *
     *
     *     @Override
     *     public void configure(ClientDetailsServiceConfigurer clients)
     *     throws Exception {
     *
     *         clients.inMemory()
     *                 .withClient("eagleeye")
     *                 .secret("thisissecret")
     *                 .authorizedGrantTypes("refresh_token", "password", "client_credentials")
     *                 .scopes("webclient", "mobileclient");
     *     }
     * }
     *
     * 现在，如果你重建你的认证服务并重新启动它，你会看到一个返回的 JWT 令牌。
     *
     * 实际的令牌本身不会直接返回为 JavaScript。相反，JavaScript 的报文体使用 Base64 编码进行编码。如果
     * 你有兴趣看到 JWT 令牌的内容，可以使用在线的工具解码令牌。推荐使用一家名为 Stormpath 的公司的在线工
     * 具：https://www.jsonwebtoken.io/（或 https://jwt.io/），是一个在线的解码器。
     *
     * 注意:理解 JWT 令牌被签名，但没有加密是非常重要的。任何在线 JWT 工具都可以解码 JWT 令牌并公开其内容。
     * 之所以提出这一点，是因为 JWT 规范允许扩展令牌并向令牌添加附加信息。在你的 JWT 令牌里，不要暴露敏感
     * 或个人可识别信息（PII）。
     *
     *
     *
     * 2、在微服务中消费 JavaScript Web Tokens
     *
     * 现在你的 OAuth2 认证服务创建 JWT 令牌。下一步是配置你的许可服务和组织服务以使用 JWT。这是一个需要你
     * 做两件事的小事：
     * （1）向许可服务和组织服务的 pom.xml 文件添加 spring-security-jwt 依赖。
     * （2）在许可服务和组织服务中创建一个 JWTTokenStoreConfig 类。这个类与使用的认证服务几乎是完全相同的
     * 类，具体可参考上面的类。
     *
     * 你需要做最后一件工作。由于许可服务调用组织服务，所以需要确保 OAuth2 令牌被传递。这通常是通过
     * OAuth2RestTemplate 类完成的，但是，OAuth2RestTemplate 类不传递 JWT 令牌。为了确保许可
     * 服务做到这一点，你需要添加一个自定义的 RestTemplate bean， 它将为你执行注入。这个自定义
     * RestTemplate 类可以在 Application 类中找到。
     *
     *     @Primary
     *     @Bean
     *     public RestTemplate getCustomRestTemplate() {
     *         RestTemplate template = new RestTemplate();
     *         List interceptors = template.getInterceptors();
     *         if (interceptors == null) {
     *             template.setInterceptors(Collections.singletonList(new UserContextInterceptor()));
     *         } else {
     *             interceptors.add(new UserContextInterceptor());
     *             template.setInterceptors(interceptors);
     *         }
     *
     *         return template;
     *     }
     *
     * 在这里，你定义了一个将使用 ClientHttpRequestInterceptor 类的自定义 RestTemplate bean。
     *
     * ClientHttpRequestInterceptor 类是一个 Spring 类，它允许你在执行 REST 调用之前与方法挂钩。这个
     * 拦截器类是 UserContextInterceptor 类的变体。如下所示。
     *
     * public class UserContextInterceptor implements ClientHttpRequestInterceptor {
     *
     *     private static final Logger logger = LoggerFactory.getLogger(UserContextInterceptor.class);
     *     @Override
     *     public ClientHttpResponse intercept(
     *             HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
     *             throws IOException {
     *
     *         HttpHeaders headers = request.getHeaders();
     *         headers.add(UserContext.CORRELATION_ID, UserContextHolder.getContext().getCorrelationId());
     *         headers.add(UserContext.AUTH_TOKEN, UserContextHolder.getContext().getAuthToken());
     *
     *         return execution.execute(request, body);
     *     }
     *
     * }
     *
     * 记住，你的每一个服务使用自定义的 Servlet 过滤器（称为 UserContextFilter）从 HTTP 头解析出的认证
     * 令牌和关联 ID。而且，你使用了已经解析过的 UserContext.AUTH_TOKEN 值来填充传出的 HTTP 调用。
     *
     * 有了这些，就足够了。现在你可以调用许可服务（或组织服务），将经过 Base64 编码的 JWT 放置在你的 HTTP
     * 头 Authorization 字段（它的值为 Bearer <<JWT-Token>>），并且你的服务将正确读取和验证 JWT 令牌。
     *
     *
     *
     * 3、扩展JWT Token
     *
     * 通过将一个 Spring OAuth2 令牌增强类添加到认证服务中，可以轻松地扩展 JWT 令牌。该类的源代码可以在
     * JWTTokenEnhancer.java 类中找到。如下所示。
     *
     * public class JWTTokenEnhancer implements TokenEnhancer {
     *
     *     @Autowired
     *     private OrgUserRepository orgUserRepo;
     *
     *     private String getOrgId(String userName){
     *         UserOrganization orgUser = orgUserRepo.findByUserName( userName );
     *         return orgUser.getOrganizationId();
     *     }
     *
     *     @Override
     *     public OAuth2AccessToken enhance(OAuth2AccessToken accessToken,
     *     OAuth2Authentication authentication) {
     *         Map<String, Object> additionalInfo = new HashMap<>();
     *         String orgId =  getOrgId(authentication.getName());
     *
     *         additionalInfo.put("organizationId", orgId);
     *
     *         ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
     *         return accessToken;
     *     }
     *
     * }
     *
     * 你需要做的最后一件事是告诉你的 OAuth2 服务使用你的 JWTTokenEnhancer 类。首先需要为
     * JWTTokenEnhancer 类暴露为一个 Spring bean。通过向 JWTTokenStoreConfig 类中添加
     * bean 定义来实现这一点：
     *
     *     @Bean
     *     public TokenEnhancer jwtTokenEnhancer() {
     *         return new JWTTokenEnhancer();
     *     }
     *
     * 一旦你将 JWTTokenEnhancer 暴露为 bean，就可以将它引入到 JWTOAuth2Config 类中。这是在类的
     * configure() 方法中完成的。
     *
     * 此时，你已经可以将一个自定义字段添加到 JWT 令牌中了。你的下一个问题应该是：如何从 JWT 令牌中解
     * 析自定义的字段。
     *
     *
     *
     * 4、解析来自自定义字段的 JavaScript token
     *
     * 这里将把你的 Zuul 网关作为如何从自定义的字段中解析出 JWT 令牌的示例。具体来说，你将修改之前引入的
     * TrackingFilter 类来解码流经网关的 JWT 令牌的 organizationId 字段。
     *
     * 为此你要拉取一个 JWT 解析器库并添加到 Zuul 服务器的 pom.xml 文件。有多个令牌解析器是可用的，这里
     * 选择了 JJWT 库（https://github.com/jwtk/jjwt）进行解析。库的 Maven 依赖是：
     *
     *         <dependency>
     *             <groupId>io.jsonwebtoken</groupId>
     *             <artifactId>jjwt</artifactId>
     *             <version>0.7.0</version>
     *         </dependency>
     *
     * 一旦添加了 JJWT 库，你可以为你的 TrackingFilter 类添加一个称为 getOrganizationId() 的新方法。
     * 如下所示。
     *
     *     private String getOrganizationId(){
     *
     *         String result="";
     *         if (filterUtils.getAuthToken()!=null){
     *
     *             String authToken = filterUtils.getAuthToken().replace("Bearer ","");
     *             try {
     *                 Claims claims = Jwts.parser()
     *                         .setSigningKey(serviceConfig.getJwtSigningKey().getBytes("UTF-8"))
     *                         .parseClaimsJws(authToken).getBody();
     *                 result = (String) claims.get("organizationId");
     *             }
     *             catch (Exception e){
     *                 e.printStackTrace();
     *             }
     *         }
     *         return result;
     *     }
     *
     * 一 旦 getOrganizationId() 的功能实现，向 TrackingFilter 类的 run() 方法添加 System.out.println
     * 来打印 organizationId，它解析自流经 Zuul 网关的 JWT 令牌，因此，你可以调用任何启用网关的 REST 端点。
     * 这里以 GET 方式调用 http://localhost:5555/api/licensing/v1/organizations
     * /e254f8c-c442-4ebe-a82a-e2fc1d1ff78a/licenses/f3831f8c-c338-4ebe-a82a-e2fc1d1ff78a。记住，
     * 在进行此调用时，仍然需要设置所有 HTTP 表单参数和 HTTP authorization 头，以包括 Authorization 头
     * 和令牌。
     */
    public static void main(String[] args) {

    }

}
