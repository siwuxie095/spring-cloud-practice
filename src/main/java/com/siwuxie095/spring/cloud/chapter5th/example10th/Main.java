package com.siwuxie095.spring.cloud.chapter5th.example10th;

/**
 * @author Jiajing Li
 * @date 2021-06-06 17:11:45
 */
@SuppressWarnings("all")
public class Main {

    /**
     * 线程上下文和 Hystrix
     *
     * 当一个 @HystrixCommand 执行时，它可以采用两种不同的隔离策略：线程和信号量。默认情况下，Hystrix
     * 运行在一个线程隔离。每个 Hystrix 命令用来保护运行在一个独立的线程池的调用，不与进行调用的父线程共
     * 享它的上下文。这意味着 Hystrix 可以中断控制的一个线程的执行，而不必担心中断任何与父线程做原始调用
     * 相关联的其他活动。
     *
     * 信号隔离，Hystrix 管理的分布式调用受 @HystrixCommand 注解保护，如果调用超时没有开始一个新的线程
     * 和中断父线程。在同步容器服务器环境（Tomcat）中，中断父线程将导致无法由开发人员捕获的异常抛出。这可
     * 能会导致开发人员编写代码的意外后果，因为他们无法捕获抛出的异常或进行任何资源清理或错误处理。
     *
     * 设置一个命令池来控制隔离，你可以在 @HystrixCommand 注解设置 commandProperties 属性。例如，如
     * 果你想在 Hystrix 命令使用信号隔离设置隔离级别，你会用：
     *
     * @HystrixCommand(
     * 	commandProperties = {
     * 		@HystrixProperty(
     * 			name="execution.isolation.strategy", value="SEMAPHORE")})
     *
     * 注意：默认情况下，Hystrix 团队建议你为大部分命令使用默认的线程隔离策略。这将在你和父线程之间保持更
     * 高级别的隔离级别。线程隔离比使用信号量隔离要重。信号隔离模型是更轻量级的，当在你的服务中你有高容量
     * 且运行在一个异步 I/O 编程模型（你使用异步 I/O 容器如 Netty）时应该被使用。
     *
     *
     *
     * 1、ThreadLocal 和 Hystrix
     *
     * Hystrix，默认情况下，不会将父线程的上下文传播到由 Hystrix 命令管理的线程。例如，任何值设置为在父
     * 线程 ThreadLocal 的值对通过父线程调用的方法默认不可用，且通过 @HystrixCommand 对象保护（这里
     * 是假设你使用线程隔离级别）。
     *
     * 这可能有点不太清晰，所以来看一个具体的例子。通常，在基于 REST 的环境中，你希望将上下文信息传递到服
     * 务调用，这将帮助你在操作上管理服务。例如，你可以在 REST 调用的 HTTP 头中传递关联 ID 或认证令牌，
     * 然后将其传播到任何下游服务调用。关联 ID 允许你拥有一个惟一标识符，该标识符可以跟踪单个事务中的多个
     * 服务调用。
     *
     * 为了使这个值在服务调用的任何地方都可用，你可以使用一个 Spring 过滤器类拦截 REST 服务的每一次调用，
     * 从传入的 HTTP 请求检索此信息，并在一个自定义的用户上下文对象存储上下文信息。然后，在你的代码需要在
     * REST 服务调用访问这个值，你的代码可以从 ThreadLocal 存储变量中检索用户上下文和读取该值。下面的代
     * 码显示了一个 Spring 过滤器示例，你可以在许可服务中使用。
     *
     * @Component
     * public class UserContextFilter implements Filter {
     *     private static final Logger logger = LoggerFactory.getLogger(UserContextFilter.class);
     *
     *     @Override
     *     public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
     *     FilterChain filterChain)
     *             throws IOException, ServletException {
     *
     *         HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
     *
     *         UserContextHolder.getContext().
     *         setCorrelationId(httpServletRequest.getHeader(UserContext.CORRELATION_ID) );
     *         UserContextHolder.getContext().
     *         setUserId(httpServletRequest.getHeader(UserContext.USER_ID));
     *         UserContextHolder.getContext().
     *         setAuthToken(httpServletRequest.getHeader(UserContext.AUTH_TOKEN));
     *         UserContextHolder.getContext().
     *         setOrgId(httpServletRequest.getHeader(UserContext.ORG_ID));
     *
     *         logger.debug("UserContextFilter Correlation id: {}",
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
     * UserContextHolder 类被用于将 UserContext 存储在 ThreadLocal 类中。一旦它被存储在 ThreadLocal
     * 存储，请求任何代码的执行，将使用存储在 UserContextHolder 的 UserContext 对象。
     *
     * public class UserContextHolder {
     *
     *     private static final ThreadLocal<UserContext> userContext = new ThreadLocal<UserContext>();
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
     *         Assert.notNull(context, "Only non-null UserContext instances are permitted");
     *         userContext.set(context);
     *     }
     *
     *     public static final UserContext createEmptyContext(){
     *         return new UserContext();
     *     }
     *
     * }
     *
     * 此时，你可以向你的许可服务添加两条日志语句。你将向以下的许可服务类和方法添加日志记录：
     * （1）UserContextFilter#doFilter() 方法。
     * （2）LicenseServiceController#getLicenses() 方法。
     * （3）LicenseService#getLicensesByOrg() 方法。这个方法使用 @HystrixCommand 注解。
     *
     * 接下来，你将调用你的服务，使用一个名为 tmx-correlation-id 的 HTTP 头传递一个关联 ID，其值为
     * TEST-CORRELATION-ID。例如，可以在 Postman 的 HTTP GET 调用 http://localhost:8080/v1
     * /organizations/e254f8c-c442-4ebe-a82ae2fc1d1ff78a/licenses/。
     *
     * 一旦调用被提交，你应该看到当请求流经 UserContext、LicenseServiceController、
     * LicenseServer 类时，通过关联 ID 输出三个日志信息：
     *
     * UserContext Correlation id: TEST-CORRELATION-ID
     * LicenseServiceController Correlation id: TEST-CORRELATION-ID
     * LicenseService.getLicenseByOrg Correlation id:
     *
     * 正如预期的那样，一旦调用命中 Hystrix 在 LicenseService.getLicenseByOrg() 上保护的方法，你
     * 将不会获得关于关联 ID 的输出值。幸运的是，Hystrix 和 Spring Cloud 提供了一种机制，将父线程的
     * 上下文传播到由 Hystrix 线程池管理的线程。这种机制被称为 HystrixConcurrencyStrategy。
     *
     *
     *
     * 2、Hystrix 并发策略
     *
     * Hystrix 允许你定义一个自定义并发策略，该策略将包装你的 Hystrix 调用，并允许你将任何附加的父线
     * 程上下文注入到由 Hystrix 命令管理的线程中。要实现一个自定义的 HystrixConcurrencyStrategy
     * 你需要进行三个步骤：
     * （1）定义你的自定义 Hystrix 并发策略类。
     * （2）定义一个 Java 调用类将 UserContext 注入到 Hystrix 命令。
     * （3）配置 Spring Cloud 使用自定义的 Hystrix 并发策略。
     *
     *
     * 2.1、定义你的自定义 Hystrix 并发策略类
     *
     * 你需要做的第一件事就是定义你的 HystrixConcurrencyStrategy。默认情况下，Hystrix 只允许你为应
     * 用程序定义一个 HystrixConcurrencyStrategy。Spring Cloud 已经定义了用于处理扩展传播的 Spring
     * 安全信息的并发策略。幸运的是，Spring Cloud 允许你将 Hystrix 并发策略链接在一起，这样你就可以定
     * 义并使用自己的并发策略，将其 "插入" 到 Hystrix 并发策略中。
     *
     * 这里的 Hystrix 并发策略的实现可以在许可服务的 ThreadLocalAwareStrategy 类中看到。如下所示。
     *
     * public class ThreadLocalAwareStrategy extends HystrixConcurrencyStrategy {
     *
     *     private HystrixConcurrencyStrategy existingConcurrencyStrategy;
     *
     *     public ThreadLocalAwareStrategy(
     *             HystrixConcurrencyStrategy existingConcurrencyStrategy) {
     *         this.existingConcurrencyStrategy = existingConcurrencyStrategy;
     *     }
     *
     *     @Override
     *     public BlockingQueue<Runnable> getBlockingQueue(int maxQueueSize) {
     *         return existingConcurrencyStrategy != null
     *                 ? existingConcurrencyStrategy.getBlockingQueue(maxQueueSize)
     *                 : super.getBlockingQueue(maxQueueSize);
     *     }
     *
     *     @Override
     *     public <T> HystrixRequestVariable<T> getRequestVariable(
     *             HystrixRequestVariableLifecycle<T> rv) {
     *         return existingConcurrencyStrategy != null
     *                 ? existingConcurrencyStrategy.getRequestVariable(rv)
     *                 : super.getRequestVariable(rv);
     *     }
     *
     *     @Override
     *     public ThreadPoolExecutor getThreadPool(HystrixThreadPoolKey threadPoolKey,
     *                                             HystrixProperty<Integer> corePoolSize,
     *                                             HystrixProperty<Integer> maximumPoolSize,
     *                                             HystrixProperty<Integer> keepAliveTime, TimeUnit unit,
     *                                             BlockingQueue<Runnable> workQueue) {
     *         return existingConcurrencyStrategy != null
     *                 ? existingConcurrencyStrategy.getThreadPool(threadPoolKey, corePoolSize,
     *                 maximumPoolSize, keepAliveTime, unit, workQueue)
     *                 : super.getThreadPool(threadPoolKey, corePoolSize, maximumPoolSize,
     *                 keepAliveTime, unit, workQueue);
     *     }
     *
     *     @Override
     *     public <T> Callable<T> wrapCallable(Callable<T> callable) {
     *         return existingConcurrencyStrategy != null
     *                 ? existingConcurrencyStrategy
     *                 .wrapCallable(new DelegatingUserContextCallable<T>(callable,
     *                 UserContextHolder.getContext()))
     *                 : super.wrapCallable(new DelegatingUserContextCallable<T>(callable,
     *                 UserContextHolder.getContext()));
     *     }
     *
     * }
     *
     * 注意该类实现中的一些事情。首先，因为 Spring Cloud 已经定义了一个 HystrixConcurrencyStrategy，
     * 可以覆盖的每种方法都需要检查是否存在现有的并发策略，然后调用现有的并发策略方法或基于 Hystrix 并发
     * 策略的方法。
     *
     * 你要做这一约定确保你调用已经存在的处理安全的 Spring Cloud 的 HystrixConcurrencyStrategy。否
     * 则，当试图在你的 Hystrix 保护代码中使用 Spring Security 上下文时，你会有令人不愉快的行为。
     *
     * 要注意的第二点是 wrapCallable() 方法。这里你通过回调实现 DelegatingUserContextCallable，它
     * 将用于设置从父线程执行用户的 REST 服务调用的用户上下文到 Hystrix 命令线程的保护方法，该方法是在
     * 内部进行的工作。
     *
     *
     * 2.2、定义一个 Java 回调类来将 UserContext 注入到 Hystrix 命令
     *
     * 将父线程的线程上下文传播到你的 Hystrix 命令的下一个步骤是实现将执行传播的回调类。如下所示。
     *
     * public final class DelegatingUserContextCallable<V> implements Callable<V> {
     *
     *     private final Callable<V> delegate;
     *     private UserContext originalUserContext;
     *
     *     public DelegatingUserContextCallable(Callable<V> delegate,
     *                                          UserContext userContext) {
     *         this.delegate = delegate;
     *         this.originalUserContext = userContext;
     *     }
     *
     *     @Override
     *     public V call() throws Exception {
     *         UserContextHolder.setContext( originalUserContext );
     *
     *         try {
     *             return delegate.call();
     *         }
     *         finally {
     *             this.originalUserContext = null;
     *         }
     *     }
     *
     *     public static <V> Callable<V> create(Callable<V> delegate,
     *                                          UserContext userContext) {
     *         return new DelegatingUserContextCallable<V>(delegate, userContext);
     *     }
     * }
     *
     * 当调用了一个受 Hystrix 保护的方法，Hystrix 和 Spring Cloud 将实例化 DelegatingUserContextCallable
     * 类的一个实例，通过通常由 Hystrix 命令池管理的线程调用的回调类。回调类存储在称为 delegate
     * 的一个 Java 属性。从概念上讲，你可以将 delegate 属性看作是由 @HystrixCommand 注解保护的方法的句柄。
     *
     * 除了委托回调的类，Spring Cloud 也使 UserContext 对象与初始化调用的父线程隔离。在创建
     * DelegatingUserContextCallable 实例时，这两个值被设置，实际操作会发生在你的类的 call() 方法。
     *
     * 要做的第一件事情是通过 UserContextHolder.setContext() 方法在 call() 方法里设置 UserContext。
     * 记住，setContext() 方法在正在运行的特定线程的一个 ThreadLocal 变量里存储一个 UserContext 对象。
     * 一旦 UserContext 被设置，然后你调用委托回调类的 call() 方法。这个对 delegate.call() 的调用调用
     * 了由 @HystrixCommand 注解保护的方法。
     *
     *
     * 2.3、配置 Spring Cloud 使用自定义的 Hystrix 并发策略
     *
     * 现在你已经有通过 ThreadLocalAwareStrategy 类和通过 DelegatingUserContextCallable 类定义了
     * 回调类的 HystrixConcurrencyStrategy，你需要将它们挂在 Spring Cloud 和 Hystrix 中。为此，你
     * 将定义一个新的配置类。如下所示。
     *
     * @Configuration
     * public class ThreadLocalConfiguration {
     *
     *     @Autowired(required = false)
     *     private HystrixConcurrencyStrategy existingConcurrencyStrategy;
     *
     *     @PostConstruct
     *     public void init() {
     *         // Keeps references of existing Hystrix plugins.
     *         HystrixEventNotifier eventNotifier = HystrixPlugins.getInstance()
     *                 .getEventNotifier();
     *         HystrixMetricsPublisher metricsPublisher = HystrixPlugins.getInstance()
     *                 .getMetricsPublisher();
     *         HystrixPropertiesStrategy propertiesStrategy = HystrixPlugins.getInstance()
     *                 .getPropertiesStrategy();
     *         HystrixCommandExecutionHook commandExecutionHook = HystrixPlugins.getInstance()
     *                 .getCommandExecutionHook();
     *
     *         HystrixPlugins.reset();
     *
     *         HystrixPlugins.getInstance().registerConcurrencyStrategy(
     *         new ThreadLocalAwareStrategy(existingConcurrencyStrategy));
     *         HystrixPlugins.getInstance().registerEventNotifier(eventNotifier);
     *         HystrixPlugins.getInstance().registerMetricsPublisher(metricsPublisher);
     *         HystrixPlugins.getInstance().registerPropertiesStrategy(propertiesStrategy);
     *         HystrixPlugins.getInstance().registerCommandExecutionHook(commandExecutionHook);
     *     }
     *
     * }
     *
     * 这个 Spring 配置类基本上重建了 Hystrix 插件，用于管理在你的服务中运行的所有不同组件。在 init()
     * 方法，你通过插件抓住所有已用的 Hystrix 组件引用。然后你注册你的自定义 HystrixConcurrencyStrategy，
     * 即 ThreadLocalAwareStrategy。
     *
     *         HystrixPlugins.getInstance().registerConcurrencyStrategy(
     *         new ThreadLocalAwareStrategy(existingConcurrencyStrategy));
     *
     * 记住，Hystrix 只允许一个 HystrixConcurrencyStrategy。Spring 将尝试自动装配任何现有的
     * HystrixConcurrencyStrategy（如果它存在的话）。最后，当你做完这一切，你重新注册原始的
     * Hystrix 组件，你使用 Hystrix 插件在 init() 方法开始后抓住这些组件。
     *
     * 你现在可以重建并重新吪劢许可服务，并通过 GET（http://localhost:8080/v1/organizations
     * /e254f8c-c442-4ebea82a-e2fc1d1ff78a /licenses/）调用它。现在，当这个调用完成时，你
     * 应该在控制台窗口中看到以下输出：
     *
     * UserContext Correlation id: TEST-CORRELATION-ID
     * LicenseServiceController Correlation id: TEST-CORRELATION-ID
     * LicenseService.getLicenseByOrg Correlation id: TEST-CORRELATION-ID
     *
     * 产生一个小的结果需要大量的工作，但不幸的是，当你使用 Hystrix 的线程级隔离时，这是必须的。
     */
    public static void main(String[] args) {

    }

}
