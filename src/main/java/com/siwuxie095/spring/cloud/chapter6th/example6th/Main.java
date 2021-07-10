package com.siwuxie095.spring.cloud.chapter6th.example6th;

/**
 * @author Jiajing Li
 * @date 2021-06-12 21:57:12
 */
@SuppressWarnings("all")
public class Main {

    /**
     * 构建第一个生成关联 ID 的 Zuul 前置过滤器
     *
     * 在 Zuul 中构建过滤器是非常简单的。首先将构建一个名为 TrackingFilter 的 Zuul 前置过滤器，该过滤器将检查
     * 所有到网关的传入请求，并确定请求中是否存在名为 tmx-correlation-id 的 HTTP 首部。tmx-correlation-id
     * 首部将包含一个唯一的全局通用 ID（Globally Universal ID，GUID），它可用于跨多个微服务来跟踪用户请求。
     *
     * 如果在 HTTP 首部中不存在 tmx-correlation-id ，那么 Zuul TrackingFilter 将生成并设置该关联 ID。如果
     * 已经存在关联 ID，那么 Zuul 将不会对该关联 ID 进行任何操作。关联 ID 的存在意味着该特定服务调用是执行用户
     * 请求的服务调用链的一部分。在这种情况下，TrackingFilter 类将不执行任何操作。
     *
     * 如下代码是 TrackingFilter 的实现。
     *
     * // 所有 Zuul 过滤器必须扩展 ZuulFilter 类，并覆盖 4 个方法，即 filterType()、
     * // filterOrder()、shouldFilter() 和 run()
     * @Component
     * public class TrackingFilter extends ZuulFilter {
     *
     *     private static final int      FILTER_ORDER =  1;
     *     private static final boolean  SHOULD_FILTER = true;
     *     private static final Logger logger = LoggerFactory.getLogger(TrackingFilter.class);
     *
     *     // 在所有过滤器中使用的常用方法都封装在 FilterUtils 类中
     *     @Autowired
     *     FilterUtils filterUtils;
     *
     *     // filterType() 方法用于告诉 Zuul，该过滤器是前置过滤器、路由过滤器还是后置过滤器
     *     @Override
     *     public String filterType() {
     *         return FilterUtils.PRE_FILTER_TYPE;
     *     }
     *
     *     // filterOrder() 方法返回一个整数值，指示不同类型的过滤器的执行顺序
     *     @Override
     *     public int filterOrder() {
     *         return FILTER_ORDER;
     *     }
     *
     *     // shouldFilter() 方法返回一个布尔值来指示该过滤器是否要执行
     *     @Override
     *     public boolean shouldFilter() {
     *         return SHOULD_FILTER;
     *     }
     *
     *     // 该辅助方法检查 tmx-correlation-id 是否存在
     *     private boolean isCorrelationIdPresent() {
     *         if (filterUtils.getCorrelationId() !=null) {
     *             return true;
     *         }
     *         return false;
     *     }
     *
     *     // 该辅助方法可以生成关联 ID 的 GUID 值
     *     private String generateCorrelationId() {
     *         return java.util.UUID.randomUUID().toString();
     *     }
     *
     *     // run() 方法是每次服务通过过滤器时执行的代码。run() 方法检查 tmx-correlation-id 是否存在，
     *     // 如果不存在，则生成一个关联值，并设置 HTTP 首部 tmx-correlation-id
     *     @Override
     *     public Object run() {
     *
     *         if (isCorrelationIdPresent()) {
     *             logger.debug("tmx-correlation-id found in tracking filter: {}. ",
     *                     filterUtils.getCorrelationId());
     *         } else {
     *             filterUtils.setCorrelationId(generateCorrelationId());
     *             logger.debug("tmx-correlation-id generated in tracking filter: {}.",
     *                     filterUtils.getCorrelationId());
     *         }
     *
     *         RequestContext ctx = RequestContext.getCurrentContext();
     *         logger.debug("Processing incoming request for {}.",
     *                 ctx.getRequest().getRequestURI());
     *         return null;
     *     }
     *
     * }
     *
     * 要在 Zuul 中实现过滤器，必须扩展 ZuulFilter 类，然后覆盖 4 个方法，即 filterType()、filterOrder()、
     * shouldFilter() 和 run() 方法。这段代码中，前三个方法描述了 Zuul 正在构建什么类型的过滤器，与这个类型
     * 的其他过滤器相比它应该以什么顺序运行，以及它是否应该处于活跃状态。最后一个方法 run() 包含过滤器要实现的
     * 业务逻辑。
     *
     * 这里已经实现了一个名为 FilterUtils 的类。这个类用于封装所有过滤器使用的常用功能。这里不会去详细解释整个
     * FilterUtils 类，在这里讨论的关键方法是 getCorrelationId() 和 setCorrelationId()。
     *
     * 如下代码展示了 FilterUtils 类的 getCorrelationId() 方法的代码。
     *
     *     public String getCorrelationId() {
     *         RequestContext ctx = RequestContext.getCurrentContext();
     *         if (ctx.getRequest().getHeader(CORRELATION_ID) != null) {
     *             return ctx.getRequest().getHeader(CORRELATION_ID);
     *         } else {
     *             return  ctx.getZuulRequestHeaders().get(CORRELATION_ID);
     *         }
     *     }
     *
     * 这里要注意的关键点是，首先要检查是否已经在传入请求的 HTTP 首部设置了 tmx-correlation-ID。这里使用
     * ctx.getRequest().getHeader(CORRELATION_ID) 调用来做到这一点。
     *
     * 注意：在一般的 Spring MVC 或 Spring Boot 服务中，RequestContext 是 org.springframework.web
     * .servletsupport.RequestContext 类型的。然而，Zuul 提供了一个专门的 RequestContext，它具有几个
     * 额外的方法来访问 Zuul 特定的值。该请求上下文是 com.netflix.zuul.context 包的一部分。
     *
     * 如果 tmx-correlation-ID 不存在，接下来就检查 ZuulRequestHeaders。Zuul 不允许直接添加或修改传入请求
     * 中的 HTTP 请求首部。如果想要添加 tmx-correlation-id，并且以后在过滤器中能够再次访问到它，实际上在 ctx
     * .getRequestHeader() 调用的结果中并不会包含它。
     *
     * 为了解决这个问题，可以使用 FilterUtils 的 getCorrelationId() 方法。在 TrackingFilter 类的 run()
     * 方法中，使用了以下代码片段：
     *
     * else {
     *     filterUtils.setCorrelationId(generateCorrelationId());
     *     logger.debug("tmx-correlation-id generated in tracking filter: {}.",
     *     ➥  filterUtils.getCorrelationId());
     * }
     *
     * tmx-correlation-id 的设置发生在 FilterUtils 的 setCorrelationId() 方法中：
     *
     *     public void setCorrelationId(String correlationId) {
     *         RequestContext ctx = RequestContext.getCurrentContext();
     *         ctx.addZuulRequestHeader(CORRELATION_ID, correlationId);
     *     }
     *
     * 在 FilterUtils 的 setCorrelationId() 方法中，要向 HTTP 请求首部添加值时，应使用 RequestContext
     * 的 addZuulRequestHeader() 方法。该方法将维护一个单独的 HTTP 首部映射，这个映射是在请求通过 Zuul
     * 服务器流经这些过滤器时添加的。当 Zuul 服务器调用目标服务时，包含在 ZuulRequestHeader 映射中的数据将
     * 被合并。
     *
     *
     *
     * 在服务调用中使用关联 ID
     *
     * 既然已经确保每个流经 Zuul 的微服务调用都添加了关联 ID，那么如何确保：
     * （1）正在被调用的微服务可以很容易访问关联 ID；
     * （2）下游服务调用微服务时可能也会将关联 ID 传播到下游调用中。
     *
     * 要实现这一点，需要为每个微服务构建一组三个类。这些类将协同工作，从传入的 HTTP 请求中读取关联 ID（以及
     * 稍后添加的其他信息），并将它映射到可以由应用程序中的业务逻辑轻松访问和使用的类，然后确保关联 ID 被传播
     * 到任何下游服务调用。
     *
     * 如下展示了如何使用许可证服务来构建这些不同的部分。
     * （1）许可证服务：许可证服务是通过 Zuul 中的路由调用的。
     * （2）UserContextFilter：UserContextFilter 将从 HTTP 首部中检索关联 ID，
     * 并将它们存储在 UserContext 对象中。
     * （3）许可证服务业务逻辑：服务中的业务逻辑可以访问在 UserContext 中检索到的
     * 任何值。
     * （4）RestTemplate 和 UserContextInterceptor：UserContextInterceptor
     * 确保所有出站 REST 调用都具有来自 UserContext 的关联 ID。
     *
     * PS：使用一组公共类，以便将关联 ID 传播到下游服务调用。
     *
     * 下面来看一下如上过程发生了什么。
     * （1）当通过 Zuul 网关对许可证服务进行调用时，TrackingFilter 会为所有进入 Zuul 的调用在传入的
     * HTTP 首部中注入一个关联 ID。
     * （2）UserContextFilter 类是一个自定义的 HTTP servlet 过滤器。它将关联 ID 映射到 UserContext
     * 类。UserContext 存储在本地线程存储中，以便稍后在调用中使用。
     * （3）许可证服务业务逻辑需要执行对组织服务的调用。
     * （4）RestTemplate 用于调用组织服务。RestTemplate 将使用自定义的 Spring 拦截器类将关联 ID 作为
     * HTTP 首部注入出站调用（自定义的 Spring 拦截器也就是 UserContextInterceptor）。
     *
     *
     * PS：重复代码与共享库对比
     *
     * 是否应该在微服务中使用公共库的话题是微服务设计中的一个灰色地带。微服务纯粹主义者会告诉你，不应该
     * 在服务中使用自定义框架，因为它会在服务中引入人为的依赖。业务逻辑的更改或 bug 修正可能会对所有服
     * 务造成大规模的重构。但是，其他微服务实践者会指出，纯粹主义者的方法是不切实际的，因为会存在这样一
     * 些情况（如前面的 UserContextFilter 例子），在这些情况下构建公共库并在服务之间共享它是有意义的。
     *
     * 这里其实存在一个中间地带。在处理基础设施风格的任务时，是很适合使用公共库的。但是，如果开始共享面
     * 向业务的类，就是在自找麻烦，因为这样是在打破服务之间的界限。
     *
     * 在这里的代码示例中，似乎违背了自己的建议，因为如果查看这里的所有服务，你就会发现它们都有自己的
     * UserContextFilter 、UserContext 和 UserContextInterceptor 类的副本。在这里之所以采用
     * 无共享的方法，是因为不希望通过创建一个必须发布到第三方 Maven 存储库的共享库来将代码示例复杂化。
     * 因此，该服务的 utils 包中的所有类都在所有服务之间共享。
     *
     *
     *
     * 1、UserContextFilter：拦截传入的 HTTP 请求
     *
     * 要构建的第一个类是 UserContextFilter 类。这个类是一个 HTTP servlet 过滤器，它将拦截进入服务的所有传入
     * HTTP 请求，并将关联 ID（和其他一些值）从 HTTP 请求映射到 UserContext 类。如下所示。
     *
     * // 这个过滤器是通过使用 Spring 的 @Component 注解和实现一个
     * // javax.servler.Filter 接口来被 Spring 注册与获取的
     * @Component
     * public class UserContextFilter implements Filter {
     *
     *     private static final Logger logger = LoggerFactory.getLogger(UserContextFilter.class);
     *
     *     @Override
     *     public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
     *     FilterChain filterChain)
     *             throws IOException, ServletException {
     *         HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
     *
     *         // 过滤器从首部中检索关联 ID，并将值设置在 UserContext 类
     *         UserContextHolder.getContext()
     *                 .setCorrelationId(httpServletRequest.getHeader(UserContext.CORRELATION_ID));
     *         UserContextHolder.getContext()
     *                 .setUserId(httpServletRequest.getHeader(UserContext.USER_ID));
     *         UserContextHolder.getContext()
     *                 .setAuthToken(httpServletRequest.getHeader(UserContext.AUTH_TOKEN));
     *         UserContextHolder.getContext()
     *                 .setOrgId(httpServletRequest.getHeader(UserContext.ORG_ID));
     *
     *         logger.debug("Special Routes Service Incoming Correlation id: {}",
     *                 UserContextHolder.getContext().getCorrelationId());
     *
     *         filterChain.doFilter(httpServletRequest, servletResponse);
     *     }
     *
     *     @Override
     *     public void init(FilterConfig filterConfig) throws ServletException {}
     *
     *     @Override
     *     public void destroy() {}
     *
     * }
     *
     * 最终，UserContextFilter 用于将感兴趣的 HTTP 首部的值映射到 Java 类 UserContext 中。
     *
     *
     *
     * 2、UserContext：使服务易于访问 HTTP 首部
     *
     * UserContext 类用于保存由微服务处理的单个服务客户端请求的 HTTP 首部值。它由 getter 和 setter 方法组成，
     * 用于从 java.lang.ThreadLocal 中检索和存储值。如下所示。
     *
     * @Component
     * public class UserContext {
     *     public static final String CORRELATION_ID = "tmx-correlation-id";
     *     public static final String AUTH_TOKEN     = "tmx-auth-token";
     *     public static final String USER_ID        = "tmx-user-id";
     *     public static final String ORG_ID         = "tmx-org-id";
     *
     *     private String correlationId = new String();
     *     private String authToken = new String();
     *     private String userId = new String();
     *     private String orgId = new String();
     *
     *     public String getCorrelationId() {
     *         return correlationId;
     *     }
     *
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
     * 现在 UserContext 类只是一个 POJO，它保存从传入的 HTTP 请求中获取的值。使用一个名为 UserContextHolder
     * 的类将 UserContext 存储在 ThreadLocal 变量中，该变量可以在处理用户请求的线程调用的任何方法中访问。如下
     * 所示。
     *
     * public class UserContextHolder {
     *     private static final ThreadLocal<UserContext> userContext =
     *             new ThreadLocal<UserContext>();
     *
     *     public static final UserContext getContext() {
     *         UserContext context = userContext.get();
     *
     *         if (context == null) {
     *             context = createEmptyContext();
     *             userContext.set(context);
     *         }
     *         return userContext.get();
     *     }
     *
     *     public static final void setContext(UserContext context) {
     *         Assert.notNull(context,
     *                 "Only non-null UserContext instances are permitted");
     *         userContext.set(context);
     *     }
     *
     *     public static final UserContext createEmptyContext() {
     *         return new UserContext();
     *     }
     *
     * }
     *
     *
     *
     * 3、自定义 RestTemplate 和 UserContextInteceptor：确保关联 ID 被传播
     *
     * 要看的最后一段代码是 UserContextInterceptor 类。这个类用于将关联 ID 注入基于 HTTP 的传出服务请求中，
     * 这些服务请求由 RestTemplate 实例执行。这样做是为了确保可以建立服务调用之间的联系。
     *
     * 要做到这一点，需要使用一个 Spring 拦截器，它将被注入 RestTemplate 类中。如下所示。
     *
     * // UserContextInterceptor 实现了 Spring 框架的 ClientHttpRequestInterceptor
     * public class UserContextInterceptor implements ClientHttpRequestInterceptor {
     *
     *     // intercept() 方法在 RestTemplate 发生实际的 HTTP 服务调用之前被调用
     *     @Override
     *     public ClientHttpResponse intercept(
     *             HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
     *             throws IOException {
     *
     *         HttpHeaders headers = request.getHeaders();
     *         // 为传出服务调用准备 HTTP 请求首部，并添加存储在 UserContext 中的关联 ID
     *         headers.add(UserContext.CORRELATION_ID,
     *                 UserContextHolder.getContext().getCorrelationId());
     *         headers.add(UserContext.AUTH_TOKEN,
     *                 UserContextHolder.getContext().getAuthToken());
     *
     *         return execution.execute(request, body);
     *     }
     *
     * }
     *
     * 为了使用 UserContextInterceptor，需要定义一个 RestTemplate bean，然后将 UserContextInterceptor
     * 添加进去。为此，需要将自己的 RestTemplate bean 定义添加到 Application 类中。
     *
     *     @LoadBalanced
     *     @Bean
     *     public RestTemplate getRestTemplate() {
     *         RestTemplate template = new RestTemplate();
     *         List interceptors = template.getInterceptors();
     *         if (interceptors == null) {
     *             template.setInterceptors(
     *             Collections.singletonList(new UserContextInterceptor()));
     *         } else {
     *             interceptors.add(new UserContextInterceptor());
     *             template.setInterceptors(interceptors);
     *         }
     *         return template;
     *     }
     *
     * 有了这个 bean 定义，每当使用 @Autowired 注解将 RestTemplate 注入一个类，就会使用这段代码中创建的
     * RestTemplate，它附带了 UserContextInterceptor。
     *
     *
     * PS：日志聚合和验证等
     *
     * 既然已经将关联 ID 传递给每个服务，那么就可以跟踪事务了，因为关联 ID 流经所有涉及调用的服务。
     * 要做到这一点，需要确保每个服务都记录到一个中央日志聚合点，该聚合点将从所有服务中捕获日志条目
     * 到一个点。在日志聚合服务中捕获的每个日志条目将具有与每个条目关联的关联 ID。实施日志聚合解决
     * 方案超出了这里的讨论范围，后续将了解如何使用 Spring Cloud Sleuth。Spring Cloud Sleuth
     * 不会使用这里构建的 TrackingFilter，但它将使用相同的概念 —— 跟踪关联 ID，并确保在每次调用
     * 中注入它。
     */
    public static void main(String[] args) {

    }

}
