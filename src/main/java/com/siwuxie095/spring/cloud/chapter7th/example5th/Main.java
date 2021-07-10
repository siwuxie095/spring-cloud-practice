package com.siwuxie095.spring.cloud.chapter7th.example5th;

/**
 * @author Jiajing Li
 * @date 2021-06-16 21:44:19
 */
@SuppressWarnings("all")
public class Main {

    /**
     * JSON Web Token 与 OAuth2
     *
     * OAuth2 是一个基于令牌的验证框架，但具有讽刺意味的是，它并没有为如何定义其规范中的令牌提供任何标准。为了矫正
     * OAuth2 令牌标准的缺陷，一个名为 JSON Web Token（JWT）的新标准脱颖而出。JWT 是因特网工程任务组（Internet
     * Engineering Task Force，IETF）提出的开放标准（RFC-7519），旨在为 OAuth2 令牌提供标准结构。JWT 令牌具
     * 有如下特点。
     * （1）小巧：JWT 令牌编码为 Base64，可以通过 URL、HTTP 首部或 HTTP POST 参数轻松传递。
     * （2）密码签名：JWT 令牌由颁发它的验证服务器签名。这意味着可以保证令牌没有被篡改。
     * （3）自包含：由于 JWT 令牌是密码签名的，接收该服务的微服务可以保证令牌的内容是有效的，因此，不需要调用验证
     * 服务来确认令牌的内容，因为令牌的签名可以被接收微服务确认，并且内容（如令牌和用户信息的过期时间）可以被接收
     * 微服务检查。
     * （4）可扩展：当验证服务生成一个令牌时，它可以在令牌被密封之前在令牌中放置额外的信息。接收服务可以解密令牌净
     * 荷，并从它里面检索额外的上下文。
     *
     * Spring Cloud Security 为 JWT 提供了开箱即用的支持。但是，要使用和消费 JWT 令牌，OAuth2 验证服务和受验
     * 证服务保护的服务必须以不同的方式配置。这个配置并不困难，下面来看一下不一样的地方。
     *
     *
     *
     * 1、修改验证服务以颁发 JWT 令牌
     *
     * 对于要受 OAuth2 保护的验证服务和两个微服务（许可证服务和组织服务），需要在它们的 Maven pom.xml 文件中添加
     * 一个新的 Spring Security 依赖项，以包含 JWT OAuth2 库。这个新的依赖项是：
     *
     * <dependency>
     *   <groupId>org.springframework.security</groupId>
     *   <artifactId>spring-security-jwt</artifactId>
     * </dependency>
     *
     * 添加完 Maven 依赖项之后，需要先告诉验证服务如何生成和翻译 JWT 令牌。
     *
     * 为此，将要在验证服务中创建一个名为 JWTTokenStoreConfig 的新配置类。如下所示。
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
     *     // 用于从出示给服务的令牌中读取数据
     *     @Bean
     *     // @Primary 注解用于告诉 Spring，如果有多个特定类型的 bean（在本例中是 DefaultTokenService），
     *     // 那么就使用被 @Primary 标注的 bean 类型进行自动注入
     *     @Primary
     *     public DefaultTokenServices tokenServices() {
     *         DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
     *         defaultTokenServices.setTokenStore(tokenStore());
     *         defaultTokenServices.setSupportRefreshToken(true);
     *         return defaultTokenServices;
     *     }
     *
     *     // 在 JWT 和 OAuth2 服务器之间充当翻译
     *     @Bean
     *     public JwtAccessTokenConverter jwtAccessTokenConverter() {
     *         JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
     *         // 定义将用于签署令牌的签名密钥
     *         converter.setSigningKey(serviceConfig.getJwtSigningKey());
     *         return converter;
     *     }
     *
     *     @Bean
     *     public TokenEnhancer jwtTokenEnhancer() {
     *         return new JWTTokenEnhancer();
     *     }
     *
     * }
     *
     * JWTTokenStoreConfig 类用于定义 Spring 将如何管理 JWT 令牌的创建、签名和翻译。因为 tokenServices() 将
     * 使用 Spring Security 的默认令牌服务实现，所以这里的工作是固定的。
     *
     * 这里要关注的是 jwtAccessTokenConverter() 方法，它定义了令牌将如何被翻译。关于这个方法，需要注意的最重要的
     * 一点是，其中正在设置将要用于签署令牌的签名密钥。
     *
     * 对于本例，将使用一个对称密钥，这意味着验证服务和受验证服务保护的服务必须要在所有服务之间共享相同的密钥。该密
     * 钥只不过是存储在验证服务 Spring Cloud Config 条目中的随机字符串值。这个签名密钥的实际值是：
     *
     * signing.key: "345345fsdgsf5345"
     *
     * 注意：Spring Cloud Security 支持对称密钥加密和使用公钥/私钥的非对称加密。这里不打算使用公钥/私钥创建 JWT。
     * 遗憾的是，关于 JWT、Spring Security 和公私钥的文档很少。如果你对实现上面讨论的内容感兴趣，强烈建议你查看
     * baeldung.com，它非常好地解释了 JWT 和公钥/私钥如何创建。
     *
     * 在 JWTTokenStoreConfig 中，定义了如何创建和签名 JWT 令牌。现在，需要将它挂钩到整个 OAuth2 服务中。之前
     * 使用 OAuth2Config 类来定义 OAuth2 服务的配置，并创建了用于服务的验证管理器，以及应用程序名称和密钥。接下
     * 来，将使用一个名为 JWTOAuth2Config 的新类替换 OAuth2Config 类。如下所示。
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
     *     @Override
     *     public void configure(AuthorizationServerEndpointsConfigurer endpoints)
     *     throws Exception {
     *         TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
     *         tokenEnhancerChain.setTokenEnhancers(
     *                 Arrays.asList(jwtTokenEnhancer, jwtAccessTokenConverter));
     *
     *         // JWTTokenStoreConfig 中创建的令牌存储将在这里注入
     *         endpoints.tokenStore(tokenStore)
     *                 // 这是钩子，用于告诉 Spring Security OAuth2 代码使用 JWT
     *                 .accessTokenConverter(jwtAccessTokenConverter)
     *                 .authenticationManager(authenticationManager)
     *                 .userDetailsService(userDetailsService);
     *     }
     *
     *     @Override
     *     public void configure(ClientDetailsServiceConfigurer clients)
     *     throws Exception {
     *         clients.inMemory()
     *                 .withClient("eagleeye")
     *                 .secret("thisissecret")
     *                 .authorizedGrantTypes("refresh_token", "password", "client_credentials")
     *                 .scopes("webclient", "mobileclient");
     *     }
     *
     * }
     *
     * 现在，如果重新构建验证服务并重新启动它，应该会返回一个基于 JWT 的令牌。
     *
     * 实际的令牌本身并不是直接作为 JSON 返回的。相反，JSON 体使用 Base64 进行了编码。如果你对 JWT 令牌的内容感
     * 兴趣，可以使用在线工具来解码令牌。推荐使用一个叫 Stormpath 的公司的在线工具，这个工具是一个在线的 JWT 解
     * 码器：https://www.jsonwebtoken.io/（或 https://jwt.io/）。
     *
     * 注意：了解 JWT 令牌已签名但未加密非常重要。任何在线 JWT 工具都可以解码 JWT 令牌并公开其内容。之所以提到这
     * 一点，是因为 JWT 规范允许开发人员扩展令牌，并向令牌添加额外的信息。不要在 JWT 令牌中暴露敏感信息或个人身份
     * 信息（Personally Identifiable Information，PII）。
     *
     *
     *
     * 2、在微服务中使用 JWT
     *
     * 到目前为止，已经拥有了创建 JWT 令牌的 OAuth2 验证服务。下一步就是配置许可证服务和组织服务以使用 JWT。这很
     * 简单，只需要做两件事。
     * （1）将 spring-security-jwt 依赖项添加到许可证服务和组织服务的 pom.xml 文件。
     * （2）在许可证服务和组织服务中创建 JWTTokenStoreConfig 类。这个类几乎与验证服务使用的类相同。
     *
     * 还需要做最后一项工作。因为许可证服务调用组织服务，所以需要确保 OAuth2 令牌被传播。
     *
     * 这项工作通常是通过 OAuth2RestTemplate 类完成的，但是 OAuth2RestTemplate 类并不传播基于 JWT 的令牌。
     * 为了确保许可证服务能够做到这一点，需要添加一个自定义的 RestTemplate bean 来完成这个注入。这个自定义的
     * RestTemplate 可以在许可证服务的 Application 类中找到。如下所示。
     *
     *     @Primary
     *     @Bean
     *     public RestTemplate getCustomRestTemplate() {
     *         RestTemplate template = new RestTemplate();
     *         List interceptors = template.getInterceptors();
     *         if (interceptors == null) {
     *             // UserContextInterceptor 会将 Authorization 首部注入每个 REST 调用
     *             template.setInterceptors(
     *                     Collections.singletonList(new UserContextInterceptor()));
     *         } else {
     *             // UserContextInterceptor 会将 Authorization 首部注入每个 REST 调用
     *             interceptors.add(new UserContextInterceptor());
     *             template.setInterceptors(interceptors);
     *         }
     *         return template;
     *     }
     *
     * 在之前的代码中，定义了一个使用 ClientHttpRequestInterceptor 的自定义 RestTemplate bean。
     *
     * ClientHttpRequestInterceptor 是一个 Spring 类，它允许在基于 REST 的调用之前挂钩要执行的功能。这个拦截
     * 器类是之前定义的 UserContextInterceptor 类的变体。如下所示。
     *
     * public class UserContextInterceptor implements ClientHttpRequestInterceptor {
     *
     *     @Override
     *     public ClientHttpResponse intercept(
     *             HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
     *             throws IOException {
     *
     *         HttpHeaders headers = request.getHeaders();
     *         headers.add(UserContext.CORRELATION_ID,
     *                 UserContextHolder.getContext().getCorrelationId());
     *         // 将授权令牌添加到 HTTP 首部
     *         headers.add(UserContext.AUTH_TOKEN,
     *                 UserContextHolder.getContext().getAuthToken());
     *
     *         return execution.execute(request, body);
     *     }
     *
     * }
     *
     * UserContextInterceptor 使用了之前定义的几个实用工具类。记住，每个服务都使用一个自定义 servlet 过滤器
     * （名为 UserContextFilter）来从 HTTP 首部解析出验证令牌和关联 ID。
     *
     * 在这段代码中，使用已解析的 UserContext.AUTH_TOKEN 值来填入传出的 HTTP 调用。
     *
     * 就是这样。有了这些功能部件，现在就可以调用许可证服务（或组织服务），并将 Base64 编码的 JWT 添加到 HTTP
     * Authorizationt 首部中，其值为 Bearer<<JWT-Token>>，服务将正确地读取和确认 JWT 令牌。
     *
     *
     *
     * 3、扩展 JWT 令牌
     *
     * 如果你仔细观察上面返回的 JWT 令牌，那么就会注意到 EagleEye 的 organizationId 字段。这不是标准的 JWT
     * 令牌字段，而是额外的字段，是在创建 JWT 令牌时通过注入新字段添加的。
     *
     * 通过向验证服务添加一个 Spring OAuth2 令牌增强器类，可以很容易地扩展 JWT 令牌。
     *
     * 这个类是 JWTTokenEnhancer，如下所示。
     *
     * // 需要扩展 TokenEnhancer 类
     * public class JWTTokenEnhancer implements TokenEnhancer {
     *
     *     @Autowired
     *     private OrgUserRepository orgUserRepo;
     *
     *     // getOrgId() 方法基于用户名查找用户的组织 ID
     *     private String getOrgId(String userName) {
     *         UserOrganization orgUser = orgUserRepo.findByUserName(userName);
     *         return orgUser.getOrganizationId();
     *     }
     *
     *     // 要进行增强，需要覆盖 enhance() 方法
     *     @Override
     *     public OAuth2AccessToken enhance(OAuth2AccessToken accessToken,
     *     OAuth2Authentication authentication) {
     *         Map<String, Object> additionalInfo = new HashMap<>();
     *         String orgId =  getOrgId(authentication.getName());
     *
     *         additionalInfo.put("organizationId", orgId);
     *
     *         // 所有附加的属性都放在 HashMap 中，并设置在传入该方法的 accessToken 变量上
     *         ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
     *         return accessToken;
     *     }
     *
     * }
     *
     * 需要做的最后一件事是告诉 OAuth2 服务使用 JWTTokenEnhancer 类。首先，需要为 JWTTokenEnhancer 类公开
     * 一个 Spring bean。通过在 JWTTokenStoreConfig 类中添加一个 bean 定义来实现这一点：
     *
     *     @Bean
     *     public TokenEnhancer jwtTokenEnhancer() {
     *         return new JWTTokenEnhancer();
     *     }
     *
     * 一旦将 JWTTokenEnhancer 作为 bean 公开，那么就可以将它挂钩到 JWTOAuth2Config 类中。
     *
     * 这一点在 JWTOAuth2Config 类的 configure() 方法中完成。如下所示。
     *
     * @Configuration
     * public class JWTOAuth2Config extends AuthorizationServerConfigurerAdapter {
     *
     *     // ...
     *
     *     // 自动装配在 TokenEnhancer 类中
     *     @Autowired
     *     private TokenEnhancer jwtTokenEnhancer;
     *
     *     @Override
     *     public void configure(AuthorizationServerEndpointsConfigurer endpoints)
     *     throws Exception {
     *         // Spring OAuth 允许开发人员挂钩多个令牌增强器，
     *         // 因此将令牌增强器添加到 TokenEnhancerChain 类中
     *         TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
     *         tokenEnhancerChain.setTokenEnhancers(
     *                 Arrays.asList(jwtTokenEnhancer, jwtAccessTokenConverter));
     *
     *         endpoints.tokenStore(tokenStore)
     *                 .accessTokenConverter(jwtAccessTokenConverter)
     *                 // 将令牌增强器挂钩到传入 configure() 方法的 endpoints 参数
     *                 .tokenEnhancer(tokenEnhancerChain)
     *                 .authenticationManager(authenticationManager)
     *                 .userDetailsService(userDetailsService);
     *     }
     *
     *     // ...
     *
     * }
     *
     * 到目前为止，已将自定义字段添加到 JWT 令牌中。接下来的问题是，如何从 JWT 令牌中解析自定义字段？
     *
     *
     *
     * 4、从 JWT 令牌中解析自定义字段
     *
     * 这里将转到 Zuul 网关，以说明如何解析 JWT 令牌中的自定义字段。具体来说，将修改之前介绍的 TrackingFilter
     * 类，以从流经网关的 JWT 令牌中解码 organizationId 字段。
     *
     * 要完成这一点，将要引入一个 JWT 解析器库，并添加到 Zuul 服务器的 pom.xml 文件中。有多个令牌解析器可供使用，
     * 这里选择 JJWT 库来进行解析。这个库的 Maven 依赖项是：
     *
     * <dependency>
     *   <groupId>io.jsonwebtoken</groupId>
     *   <artifactId>jjwt</artifactId>
     *   <version>0.7.0</version>
     * </dependency>
     *
     * 添加完 JJWT 库后，可以向 TrackingFiler 类添加一个名为 getOrganizationId() 的新方法。如下所示。
     *
     *     private String getOrganizationId() {
     *
     *         String result = "";
     *         if (filterUtils.getAuthToken() != null) {
     *             // 从 HTTP 首部 Authorization 解析出令牌
     *             String authToken = filterUtils.getAuthToken().replace("Bearer ", "");
     *             try {
     *                 // 传入用于签署令牌的签名密钥，使用 JWTS 类解析令牌
     *                 Claims claims = Jwts.parser()
     *                         .setSigningKey(serviceConfig.getJwtSigningKey().getBytes("UTF-8"))
     *                         .parseClaimsJws(authToken).getBody();
     *                         // 从令牌中提取出 organizationId
     *                 result = (String) claims.get("organizationId");
     *             } catch (Exception e) {
     *                 e.printStackTrace();
     *             }
     *         }
     *         return result;
     *     }
     *
     * 实现了 getOrganizationId() 方法之后，就将 System.out.println 添加到 TrackingFilter 的 run() 方法
     * 中，以打印从流经 Zuul 网关的 JWT 令牌中解析出来的 organizationId。
     *
     * 接下来，就来调用任何启用网关的 REST 端点。这里使用如下 GET 方法调用：
     *
     * http://localhost:5555/api/licensing/v1/organizations/e254f8c-c442-4ebe-a82a-e2fc1d1ff78a
     * /licenses/f3831f8c-c338-4ebe-a82a-e2fc1d1ff78a
     *
     * 记住，在进行这个调用时，仍然需要创建所有 HTTP 表单参数和 HTTP 授权首部，来包含 Authorization 首部和 JWT
     * 令牌。
     *
     * 从输出结果可以看到，Zuul 服务从流经的 JWT 令牌中解析出了组织 ID。
     */
    public static void main(String[] args) {

    }

}
