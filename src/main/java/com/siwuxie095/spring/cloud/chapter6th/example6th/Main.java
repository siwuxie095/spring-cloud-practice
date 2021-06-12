package com.siwuxie095.spring.cloud.chapter6th.example6th;

/**
 * @author Jiajing Li
 * @date 2021-06-12 21:57:12
 */
@SuppressWarnings("all")
public class Main {

    /**
     * 创建 pre 类型 Zuul 过滤器生成关联 ID
     *
     * 在 Zuul 构建过滤器是一个非常简单的活动。首先，你要创建一个 Zuul 预过滤器，称为 TrackingFilter，
     * 它将检查所有传入到网关的请求并确定在请求里是否有一个称为 tmx-correlation-id 的存在于 HTTP 头。
     * tmx-correlation-id 头将包含一个独特的 GUID（全球通用的 ID），它被用于跟踪跨多个微服务的一个
     * 用户请求。
     *
     * 如果在 HTTP 头不存在 tmx-correlation-id，TrackingFilter 过滤器将生成和设置关联 ID。如果关联
     * ID 已经存在，Zuul 不会为关联 ID 做任何事情。关联 ID 的存在意味着这个特定服务调用是执行用户请求的
     * 服务调用链的一部分。在这种情况下，TrackingFilter 类将什么也不做。
     *
     * 下面看看 TrackingFilter 的实现。
     *
     * @Component
     * public class TrackingFilter extends ZuulFilter {
     *     private static final int      FILTER_ORDER =  1;
     *     private static final boolean  SHOULD_FILTER=true;
     *     private static final Logger logger = LoggerFactory.getLogger(TrackingFilter.class);
     *
     *     @Autowired
     *     FilterUtils filterUtils;
     *
     *     @Override
     *     public String filterType() {
     *         return FilterUtils.PRE_FILTER_TYPE;
     *     }
     *
     *     @Override
     *     public int filterOrder() {
     *         return FILTER_ORDER;
     *     }
     *
     *     @Override
     *     public boolean shouldFilter() {
     *         return SHOULD_FILTER;
     *     }
     *
     *     private boolean isCorrelationIdPresent(){
     *         if (filterUtils.getCorrelationId() !=null){
     *             return true;
     *         }
     *
     *         return false;
     *     }
     *
     *     private String generateCorrelationId(){
     *         return java.util.UUID.randomUUID().toString();
     *     }
     *
     *     @Override
     *     public Object run() {
     *
     *         if (isCorrelationIdPresent()) {
     *             logger.debug("tmx-correlation-id found in tracking filter: {}. ",
     *             filterUtils.getCorrelationId());
     *         }
     *         else{
     *             filterUtils.setCorrelationId(generateCorrelationId());
     *             logger.debug("tmx-correlation-id generated in tracking filter: {}.",
     *             filterUtils.getCorrelationId());
     *         }
     *
     *         RequestContext ctx = RequestContext.getCurrentContext();
     *         logger.debug("Processing incoming request for {}.",
     *         ctx.getRequest().getRequestURI());
     *         return null;
     *     }
     * }
     *
     * 在 Zuul 实现一个过滤器，你必须继承 ZuulFilter 类，然后覆盖四个方法：filterType()，filterOrder()，
     * shouldFilter() 和 run()。前三个方法描述你将在 Zuul 构建什么类型的过滤器，它与其它类型的过滤器相比
     * 应该以什么顺序运行，以及它是否应该是活动的。最后一种方法，run()，包含过滤器将实现的业务逻辑。
     *
     * 你已经实现了一个称为 FilterUtils 的类。这个类用于封装所有过滤器所使用的公共功能。
     *
     * 这里不讨论整个 FilterUtils 类，但在这里其讨论关键方法：getCorrelationId() 和 setCorrelationId()。
     * 下面的代码显示 FilterUtils 类的 getCorrelationId() 方法的代码。
     *
     *     public String getCorrelationId(){
     *         RequestContext ctx = RequestContext.getCurrentContext();
     *
     *         if (ctx.getRequest().getHeader(CORRELATION_ID) !=null) {
     *             return ctx.getRequest().getHeader(CORRELATION_ID);
     *         }
     *         else{
     *             return  ctx.getZuulRequestHeaders().get(CORRELATION_ID);
     *         }
     *     }
     *
     * 需要注意的关键点是，你首先查看 tmx-correlation-id 是否已在传入请求的 HTTP 头中设置。你使用 ctx
     * .getRequest().getHeader(CORRELATION_ID) 调用来完成此操作。
     *
     * 注意：在传统的 Spring MVC 或 Spring Boot 服务，RequestContext 应该是 org.springframework
     * .web.servletsupport.RequestContext 类型。然而，Zuul 提供了一个专用的 RequestContext，它有
     * 几个用于访问 Zuul 特定值的额外方法。这个请求上下文是 com.netflix.zuul.context 包的一部分。
     *
     * 如果它不存在，你再检查 ZuulRequestHeaders。Zuul 不允许你直接添加或修改一个传入的请求的 HTTP
     * 请求头。如果已经添加了 tmx-correlation-id，然后尝试在过滤器后面再次访问它，它作为 ctx
     * .getRequestHeader() 调用部分将不可用。你可能还记得，在前面 TrackingFilter 类的 run() 方法
     * 里，你使用以下代码片段完成了这一工作。
     *
     *         else{
     *             filterUtils.setCorrelationId(generateCorrelationId());
     *             logger.debug("tmx-correlation-id generated in tracking filter: {}.",
     *             filterUtils.getCorrelationId());
     *         }
     *
     * 使用 FilterUtils 的 setCorrelationId() 方法设置 tmx-correlation-id。
     *
     *     public void setCorrelationId(String correlationId){
     *         RequestContext ctx = RequestContext.getCurrentContext();
     *         ctx.addZuulRequestHeader(CORRELATION_ID, correlationId);
     *     }
     *
     * 在 FilterUtils 类的 setCorrelationId() 方法，当你想对 HTTP 请求头添加值的时候，你用 RequestContext
     * 的 addZuulRequestHeader() 方法。该方法将为 HTTP 头维护一个单独 的 map，当请求在 Zuul 服务器流经过滤器
     * 时被添加。当目标服务由你的 Zuul 服务器调用时，在 ZuulRequestHeader 的 map 里面包含的数据将被合并。
     *
     *
     *
     * 1、在服务调用中使用关联 ID
     *
     * 现在你已经保证关联 ID 已被添加到流经 Zuul 的每个微服务调用，你如何确保：
     * （1）被调用的微服务易于访问关联 ID。
     * （2）任何下游服务调用微服务也可能将关联 ID 传播到下游调用。
     *
     * 为了实现这一点，你要在你的每个微服务中创建三个类。这些类将一起从传入的 HTTP 请求中读取关联 ID（以
     * 及你之后添加的其他信息），将它映射到一个类以易于理解，且通过应用程序中的业务逻辑方便使用，并确保关
     * 联 ID 传播到下游的任何服务调用。
     *
     * 许可服务通过 Zuul 里的路由被调用，并使用了一组公共类，以便将关联 ID 传播到下游服务调用。过程如下：
     * （1）当对许可服务的调用通过 Zuul 网关时，TrackingFilter 将为进入 Zuul 的任何调用在
     * 传入的 HTTP 头中注入一个关联 ID。
     * （2）UserContextFilter 类是一个自定义的 HTTP Servlet 过滤器。它映射一个关联 ID 到
     * UserContext 类。UserContext 类被用于存储调用里后续使用的本地线程存储的值。
     * （3）许可服务业务逻辑需要执行对组织服务的调用。
     * （4）RestTemplate 被用于调用组织服务。RestTemplate 将使用自定义的 Spring 拦截器类
     * （UserContextInterceptor）注入关联 ID 到外部调用，作为一个 HTTP 头。
     *
     *
     * PS：重复的代码 vs. 共享库
     *
     * 你是否应该在微服务使用公共库的主题是微服务设计是一个很难界定的问题。微服务纯粹主义者会告诉你，你不
     * 应该在你的服务使用一个自定义的框架，因为它在你的服务中引入了人为的依赖。业务逻辑的修改或 bug 可能
     * 导致对所有服务进行大规模的重构。另一方面，其他的微服务从业者会说，一个纯粹的方法是不切实际的，某些
     * 情况下存在是有意义的（像前面的 UserContextFilter 示例），创建一个公共库并在服务间共享它。
     *
     * 这里认为是有妥协的。在处理基础设施风格的任务时，公共库很好。如果你开始共享面向业务的类，那么你就是
     * 在自找麻烦，因为你正在打破服务之间的界限。
     *
     * 这里的代码示例似乎打破了这里的建议，因为如果你查看本章中的所有服务，它们（UserContextFilter，
     * UserContext 和 UserContextInterceptor 类）都有自己的副本。在这里使用一个非共享方法的原因
     * 是不想通过创建一个共享库而使在这里的代码示例变得复杂，因为共享库将需要发布到第三方的 Maven 仓
     * 库。因此，在服务的 utils 包中的所有类在所有服务中共享。
     *
     *
     * 1.1、USERCONTEXTFILTER：拦截传入 HTTP 请求
     *
     * 你将要创建的第一个类是 UserContextFilter 类。这个类是一个 HTTP Servlet 过滤器，用来拦截进入
     * 服务的所有 HTTP 请求，并从 HTTP 请求映射关联 ID（和一些其它的值）到 UserContext 类。如下所示。
     *
     * @Component
     * public class UserContextFilter implements Filter {
     *     private static final Logger logger = LoggerFactory.getLogger(UserContextFilter.class);
     *
     *     @Override
     *     public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
     *     FilterChain filterChain)
     *             throws IOException, ServletException {
     *         HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
     *
     *         UserContextHolder.getContext()
     *         .setCorrelationId(httpServletRequest.getHeader(UserContext.CORRELATION_ID) );
     *         UserContextHolder.getContext()
     *         .setUserId(httpServletRequest.getHeader(UserContext.USER_ID));
     *         UserContextHolder.getContext()
     *         .setAuthToken(httpServletRequest.getHeader(UserContext.AUTH_TOKEN));
     *         UserContextHolder.getContext()
     *         .setOrgId(httpServletRequest.getHeader(UserContext.ORG_ID));
     *
     *         logger.debug("Special Routes Service Incoming Correlation id: {}",
     *         UserContextHolder.getContext().getCorrelationId());
     *
     *         filterChain.doFilter(httpServletRequest, servletResponse);
     *     }
     *
     *     @Override
     *     public void init(FilterConfig filterConfig) throws ServletException {}
     *
     *     @Override
     *     public void destroy() {}
     * }
     *
     * 最后，UserContextFilter 被用于映射 HTTP 头中你感兴趣的值到一个 UserContext 类。
     *
     *
     * 1.2、USERCONTEXT：使服务易于访问 HTTP 头
     *
     * UserContext 类被用于存储通过微服务正在处理的单独的服务客户端请求 HTTP 头的值。它由一个 getter
     * 和 setter 方法组成，用来从 java.lang.ThreadLocal 检索和存储值。如下所示。
     *
     * @Component
     * public class UserContext {
     *     public static final String CORRELATION_ID = "tmx-correlation-id";
     *     public static final String AUTH_TOKEN     = "tmx-auth-token";
     *     public static final String USER_ID        = "tmx-user-id";
     *     public static final String ORG_ID         = "tmx-org-id";
     *
     *     private String correlationId= new String();
     *     private String authToken= new String();
     *     private String userId = new String();
     *     private String orgId = new String();
     *
     *     public String getCorrelationId() { return correlationId;}
     *     public void setCorrelationId(String correlationId) {
     *         this.correlationId = correlationId;
     *     }
     *
     *     public String getAuthToken() {
     *         return authToken;
     *     }
     *
     *     public void setAuthToken(String authToken) {
     *         this.authToken = authToken;
     *     }
     *
     *     public String getUserId() {
     *         return userId;
     *     }
     *
     *     public void setUserId(String userId) {
     *         this.userId = userId;
     *     }
     *
     *     public String getOrgId() {
     *         return orgId;
     *     }
     *
     *     public void setOrgId(String orgId) {
     *         this.orgId = orgId;
     *     }
     *
     * }
     *
     * 现在 UserContext 类仅仅是一个存储从传入的 HTTP 请求获取的值的 POJO 对象。你使用一个称为
     * UserContextHolder 的类在 ThreadLocal 变量存储 UserContext 对象，通过线程处理用户的请
     * 求，调用任何方法可以访问该对象。如下所示。
     *
     * public class UserContextHolder {
     *     private static final ThreadLocal<UserContext> userContext =
     *     new ThreadLocal<UserContext>();
     *
     *     public static final UserContext getContext(){
     *         UserContext context = userContext.get();
     *
     *         if (context == null) {
     *             context = createEmptyContext();
     *             userContext.set(context);
     *
     *         }
     *         return userContext.get();
     *     }
     *
     *     public static final void setContext(UserContext context) {
     *         Assert.notNull(context,
     *         "Only non-null UserContext instances are permitted");
     *         userContext.set(context);
     *     }
     *
     *     public static final UserContext createEmptyContext(){
     *         return new UserContext();
     *     }
     * }
     *
     * 1.3、自定义 RESTTEMPLATE 和 USERCONTEXTINTECEPTOR：确保关联 ID 的获取和向前传播
     *
     * 最后的代码部分，将看的是 UserContextInterceptor 类。这个类是用来注入关联 ID 到任何输出的基于
     * HTTP 的服务请求，该请求从 RestTemplate 实例中执行。这样做是为了确保你可以在服务调用之间建立联
     * 系。
     *
     * 为此你要使用一个 Spring 拦截器，它被注入 RestTemplate 类。如下所示。
     *
     * public class UserContextInterceptor implements ClientHttpRequestInterceptor {
     *     @Override
     *     public ClientHttpResponse intercept(
     *             HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
     *             throws IOException {
     *
     *         HttpHeaders headers = request.getHeaders();
     *         headers.add(UserContext.CORRELATION_ID,
     *         UserContextHolder.getContext().getCorrelationId());
     *         headers.add(UserContext.AUTH_TOKEN,
     *         UserContextHolder.getContext().getAuthToken());
     *
     *         return execution.execute(request, body);
     *     }
     * }
     *
     * 为了使用 UserContextInterceptor，你需要定义一个 RestTemplate bean 并且将
     * UserContextInterceptor 添加给它。要做到这一点，你要在 Application 类中添加
     * 你自己的 RestTemplate bean 定义。下面的代码显示了添加到该类的方法。
     *
     *     @LoadBalanced
     *     @Bean
     *     public RestTemplate getRestTemplate(){
     *         RestTemplate template = new RestTemplate();
     *         List interceptors = template.getInterceptors();
     *         if (interceptors == null) {
     *             template.setInterceptors(
     *             Collections.singletonList(new UserContextInterceptor()));
     *         } else {
     *             interceptors.add(new UserContextInterceptor());
     *             template.setInterceptors(interceptors);
     *         }
     *
     *         return template;
     *     }
     *
     * 使用 bean 定义，任何时候你使用 @Autowired 注解将为一个类注入 RestTemplate，你可以使用已创建
     * 的、与 UserContextInterceptor 绑定的 RestTemplate。
     *
     *
     * PS：日志聚合和认证等等
     *
     * 现在你已经将关联 ID 传递给每个服务，当它流经调用中所涉及的所有服务时，跟踪一个事务是可能的。要做
     * 到这一点，你需要确保每个服务日志记录到一个集中的日志聚合点，它从所有服务中捕获日志条目到一个点。
     * 在日志聚合服务中捕获的每个日志条目都将有一个与每个条目相关联的关联 ID。实现日志聚合的解决方案超出
     * 了这里的范围，但后续将看到如何使用 Spring Cloud Sleuth。Spring Cloud Sleuth 不会使用你在这
     * 里创建的 TrackingFilter，但它会使用与跟踪关联 ID 相同的概念和确保在每个调用中关联 ID 都被注入。
     */
    public static void main(String[] args) {

    }

}
