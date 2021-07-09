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
     * 当一个 @HystrixCommand 被执行时，它可以使用两种不同的隔离策略：THREAD（线程）和 SEMAPHORE（信号量）来运行。
     * 在默认情况下，Hystrix 以 THREAD 隔离策略运行。用于保护调用的每个 Hystrix 命令都在一个单独的线程池中运行，该
     * 线程池不与父线程共享它的上下文。这意味着 Hystrix 可以在它的控制下中断线程的执行，而不必担心中断与执行原始调用
     * 的父线程相关的其他活动。
     *
     * 通过基于 SEMAPHORE 的隔离，Hystrix 管理由 @HystrixCommand 注解保护的分布式调用，而不需要启动一个新线程，
     * 并且如果调用超时，就会中断父线程。在同步容器服务器环境（Tomcat）中，中断父线程将导致抛出开发人员无法捕获的
     * 异常。这可能会给编写代码的开发人员带来意想不到的后果，因为他们无法捕获抛出的异常或执行任何资源清理或错误处理。
     *
     * 要控制命令池的隔离设置，开发人员可以在自己的 @HystrixCommand 注解上设置 commandProperties 属性。例如，
     * 如果要在 Hystrix 命令中设置隔离级别以便使用 SEMAPHORE 隔离，则可以使用：
     *
     * @HystrixCommand(
     * ➥  commandProperties = {
     *         @HystrixProperty(name="execution.isolation.strategy", value="SEMAPHORE")})
     *
     * 注意：在默认情况下，Hystrix 团队建议开发人员对大多数命令使用默认的 THREAD 隔离策略。这将保持
     * 开发人员和父线程之间更高层次的隔离。THREAD 隔离比 SEMAPHORE 隔离更重，SEMAPHORE 隔离模型更
     * 轻量级，SEMAPHORE 隔离模型适用于服务量很大且正在使用异步 I/O 编程模型（假设使用的是像 Netty
     * 这样的异步 I/O 容器）运行的情况。
     *
     *
     *
     * 1、ThreadLocal 与 Hystrix
     *
     * 在默认情况下，Hystrix 不会将父线程的上下文传播到由 Hystrix 命令管理的线程中。例如，在默认情况下，对被父线程
     * 调用并由 @HystrixCommand 保护的方法而言，在父线程中设置为 ThreadLocal 值的值都是不可用的（再强调一次，这
     * 是假设当前使用的是 THREAD 隔离级别）。
     *
     * 这听起来可能会有一点难以理解，所以看一个具体的例子。通常在基于 REST 的环境中，开发人员希望将上下文信息传递给
     * 服务调用，这将有助于在运维上管理该服务。例如，可以在 REST 调用的 HTTP 首部中传递关联 ID（correlation ID）
     * 或验证令牌，然后将其传播到任何下游服务调用。关联 ID 是唯一标识符，该标识符可用于在单个事务中跨多个服务调用进
     * 行跟踪。
     *
     * 要使服务调用中的任何地方都可以使用此值，开发人员可以使用 Spring 过滤器类来拦截对 REST 服务的每个调用，并从
     * 传入的 HTTP 请求中检索此信息，然后将此上下文信息存储在自定义的 UserContext 对象中。然后，在任何需要在 REST
     * 服务调用中访问该值的时候，可以从 ThreadLocal 存储变量中检索 UserContext 并读取该值。如下代码展示了一个示
     * 例 Spring 过滤器，你可以在许可服务中使用它。
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
     *         // 检索调用的 HTTP 首部中设置的值，将这些值赋给存储在 UserContextHolder 中的 UserContext
     *         UserContextHolder.getContext().
     *                 setCorrelationId(httpServletRequest.getHeader(UserContext.CORRELATION_ID));
     *         UserContextHolder.getContext().
     *                 setUserId(httpServletRequest.getHeader(UserContext.USER_ID));
     *         UserContextHolder.getContext().
     *                 setAuthToken(httpServletRequest.getHeader(UserContext.AUTH_TOKEN));
     *         UserContextHolder.getContext().
     *                 setOrgId(httpServletRequest.getHeader(UserContext.ORG_ID));
     *
     *         logger.debug("UserContextFilter Correlation id: {}",
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
     * UserContextHolder 类用于将 UserContext 存储在 ThreadLocal 类中。一旦存储在 ThreadLocal 中，任何为请
     * 求执行的代码都将使用存储在 UserContextHolder 中的 UserContext 对象。如下代码展示了 UserContextHolder
     * 类。
     *
     * public class UserContextHolder {
     *
     *     // UserContext 存储在一个静态 ThreadLocal 变量中
     *     private static final ThreadLocal<UserContext> userContext = new ThreadLocal<UserContext>();
     *
     *     // getContext() 方法将检索 UserContext 以供使用
     *     public static final UserContext getContext() {
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
     * 此时，可以向许可证服务添加一些日志语句。这里将添加日志记录到以下许可证服务类和方法。
     * （1）UserContextFilter 类的 doFilter() 方法。
     * （2）LicenseServiceController 的 getLicenses() 方法。
     * （3）LicenseService 类的 getLicensesByOrg() 方法。此方法通过 @HystrixCommand 标注。
     *
     * 接下来，将使用名为 tmx-correlation-id 和值为 TEST-CORRELATION-ID 的 HTTP 首部来传递关联 ID 以调用服务。
     *
     * 在 Postman 中使用 HTTP GET 访问：
     * http://localhost:8080/v1/organizations/e254f8c-c442-4ebe-a82a-e2fc1d1ff78a/licenses/
     *
     * 一旦提交了这个调用，当它流经 UserContext、LicenseServiceController 和 LicenseServer 类时，将看到三条
     * 日志消息记录了传入的关联 ID：
     *
     * UserContext Correlation id: TEST-CORRELATION-ID
     * LicenseServiceController Correlation id: TEST-CORRELATION-ID
     * LicenseService.getLicenseByOrg Correlation id:
     *
     * 正如预期的那样，一旦这个调用使用了由 Hystrix 保护的 LicenseService.getLicensesByOrg() 方法，就无法得到
     * 关联 ID 的值。幸运的是，Hystrix 和 Spring Cloud 提供了一种机制，可以将父线程的上下文传播到由 Hystrix 线
     * 程池管理的线程。这种机制被称为 HystrixConcurrencyStrategy。
     *
     *
     *
     * 2、HystrixConcurrencyStrategy 实战
     *
     * Hystrix 允许开发人员定义一种自定义的并发策略，它将包装 Hystrix 调用，并允许开发人员将附加的父线程上下文注入
     * 由 Hystrix 命令管理的线程中。实现自定义 HystrixConcurrencyStrategy 需要执行以下三个操作。
     * （1）定义自定义的 Hystrix 并发策略类。
     * （2）定义一个 Callable 类，将 UserContext 注入 Hystrix 命令中。
     * （3）配置 Spring Cloud 以使用自定义 Hystrix 并发策略。
     *
     *
     * 2.1、自定义 Hystrix 并发策略类
     *
     * 这里需要做的第一件事，就是定义自己的 HystrixConcurrencyStrategy。在默认情况下，Hystrix 只允许为应用程序
     * 定义一个 HystrixConcurrencyStrategy 。Spring Cloud 已经定义了一个并发策略用于处理 Spring 安全信息的
     * 传播。幸运的是，Spring Cloud 允许将 Hystrix 并发策略链接在一起，以便可以定义和使用自己的并发策略，方法是
     * 将其 "插入" 到 Hystrix 并发策略中。
     *
     * Hystrix 并发策略的实现类是 ThreadLocalAwareStrategy，如下所示。
     *
     * // 扩展基本的 HystrixConcurrencyStrategy 类
     * public class ThreadLocalAwareStrategy extends HystrixConcurrencyStrategy {
     *
     *     private HystrixConcurrencyStrategy existingConcurrencyStrategy;
     *
     *     // Spring Cloud 已经定义了一个并发类。将已存在的并发策略传入自定义的
     *     // HystrixConcurrencyStrategy 的类构造器中
     *     public ThreadLocalAwareStrategy(
     *             HystrixConcurrencyStrategy existingConcurrencyStrategy) {
     *         this.existingConcurrencyStrategy = existingConcurrencyStrategy;
     *     }
     *
     *     // 有几个方法需要重写。要么调用 existingConcurrencyStrategy 的方法实现，
     *     // 要么调用基类 HystrixConcurrencyStrategy 的方法实现
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
     *     // 注入 Callable 实现，它将设置 UserContext
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
     * 注意这段代码实现中的两件事情。
     *
     * 首先，因为 Spring Cloud 已经定义了一个 HystrixConcurrencyStrategy，所以所有可能被覆盖的方法都需要检查
     * 现有的并发策略是否存在，然后或调用现有的并发策略的方法或调用基类的 Hystrix 并发策略的方法。开发人员必须将此
     * 作为惯例，以确保正确地调用已存在的 Spring Cloud 的 HystrixConcurrencyStrategy，该并发策略用于处理安全。
     * 否则，在受 Hystrix 保护的代码中尝试使用 Spring 安全上下文时，可能会出现难以解决的问题。
     *
     * 其次，这段代码的 wrapCallable() 方法中传递了 Callable 的实现 DelegatingUserContextCallable，用来将
     * UserContext 从执行用户 REST 服务调用的父线程，设置为保护正在进行工作的方法的 Hystrix 命令线程。
     *
     *
     * 2.2、定义一个 Java Callable 类，将 UserContext 注入 Hystrix 命令中
     *
     * 将父线程的线程上下文传播到 Hystrix 命令的下一步，是实现执行传播的 Callable 类。如下所示。
     *
     * public final class DelegatingUserContextCallable<V> implements Callable<V> {
     *
     *     private final Callable<V> delegate;
     *     private UserContext originalUserContext;
     *
     *     // 原始 Callable 类将被传递到自定义的 Callable 类，自定义 Callable 将调用
     *     // Hystrix 保护的代码和来自父线程的 UserContext
     *     public DelegatingUserContextCallable(Callable<V> delegate,
     *                                          UserContext userContext) {
     *         this.delegate = delegate;
     *         this.originalUserContext = userContext;
     *     }
     *
     *     // call() 方法在被 @HystrixCommand 注解保护的方法之前调用
     *     @Override
     *     public V call() throws Exception {
     *         // 已设置 UserContext。存储 UserContext 的 ThreadLocal 变量与运行
     *         // 受 Hystrix 保护的方法的线程相关联
     *         UserContextHolder.setContext(originalUserContext);
     *
     *         try {
     *             // UserContext 设置之后，在 Hystrix 保护的方法上调用 call()方法，
     *             // 如 LicenseServer.getLicenseByOrg() 方法
     *             return delegate.call();
     *         } finally {
     *             this.originalUserContext = null;
     *         }
     *     }
     *
     *     public static <V> Callable<V> create(Callable<V> delegate,
     *                                          UserContext userContext) {
     *         return new DelegatingUserContextCallable<V>(delegate, userContext);
     *     }
     *
     * }
     *
     * 当调用 Hystrix 保护的方法时，Hystrix 和 Spring Cloud 将实例化 DelegatingUserContextCallable 类的
     * 一个实例，传入一个通常由 Hystrix 命令池管理的线程调用的 Callable 类。在这段代码中，此 Callable 类存储
     * 在名为 delegate 的 Java 属性中。从概念上讲，可以将 delegate 属性视为由 @HystrixCommand 注解保护的方
     * 法的句柄。
     *
     * 除了委托的 Callable 类之外，Spring Cloud 也将 UserContext 对象从发起调用的父线程传递出去。这两个值在
     * 创建 DelegatingUserContextCallable 实例时设置，实际的操作将发生在类的 call() 方法中。
     *
     * 在 call() 方法中要做的第一件事是通过 UserContextHolder.setContext() 方法设置 UserContext。记住，
     * setContext() 方法将 UserContext 对象存储在 ThreadLocal 变量中，这个 ThreadLocal 变量特定于正在
     * 运行的线程。设置了 UserContext 之后，就会调用委托的 Callable 类的 call() 方法。调用 delegate.call()
     * 会调用由 @HystrixCommand 注解保护的方法。
     *
     *
     * 2.3、配置 Spring Cloud 以使用自定义 Hystrix 并发策略
     *
     * 这里已经通过 ThreadLocalAwareStrategy 类实现了 HystrixConcurrencyStrategy 类，并通过
     * DelegatingUserContextCallable 类定义了 Callable 类。
     *
     * 现在，需要将它们挂钩在 Spring Cloud 和 Hystrix 中。要做到这一点，则需要定义一个新的配置类
     * ThreadLocalConfiguration，如下所示。
     *
     * @Configuration
     * public class ThreadLocalConfiguration {
     *
     *     // 当构造配置对象时，它将自动装配在现有的 HystrixConcurrencyStrategy 中
     *     @Autowired(required = false)
     *     private HystrixConcurrencyStrategy existingConcurrencyStrategy;
     *
     *     @PostConstruct
     *     public void init() {
     *         // 保留现有的 Hystrix 插件的引用。因为要注册一个新的并发策略，所以要获取
     *         // 所有其他的 Hystrix 组件，然后重新设置 Hystrix 插件。
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
     *         // 使用 Hystrix 插件注册自定义的 Hystrix 并发策略（ThreadLocalAwareStrategy）
     *         HystrixPlugins.getInstance().registerConcurrencyStrategy(
     *                 new ThreadLocalAwareStrategy(existingConcurrencyStrategy));
     *         // 然后重新注册 Hystrix 插件使用的所有 Hystrix 组件
     *         HystrixPlugins.getInstance().registerEventNotifier(eventNotifier);
     *         HystrixPlugins.getInstance().registerMetricsPublisher(metricsPublisher);
     *         HystrixPlugins.getInstance().registerPropertiesStrategy(propertiesStrategy);
     *         HystrixPlugins.getInstance().registerCommandExecutionHook(commandExecutionHook);
     *     }
     *
     * }
     *
     * 这个 Spring 配置类基本上重新构建了管理运行在服务中所有不同组件的 Hystrix 插件。在 init() 方法中，这里获取
     * 该插件使用的所有 Hystrix 组件的引用。然后注册自定义的 Hystrix 并发策略（ThreadLocalAwareStrategy）。
     *
     * HystrixPlugins.getInstance().registerConcurrencyStrategy(
     *     new ThreadLocalAwareStrategy(existingConcurrencyStrategy));
     *
     * 记住，Hystrix 只允许一个 HystrixConcurrencyStrategy。
     *
     * Spring 将尝试自动装配在现有的任何 HystrixConcurrencyStrategy（如果它存在）中。最后，完成所有的工作之后，
     * 这里使用 Hystrix 插件把在 init() 方法开头获取的原始 Hystrix 组件重新注册回来。
     *
     * 有了这些，现在可以重新构建并重新启动许可证服务，并通过如下 GET 端点来调用这个服务：
     *
     * http://localhost:8080/v1/organizations/e254f8c-c442-4ebe-a82a-e2fc1d1ff78a/licenses/
     *
     * 当这个调用完成后，在控制台窗口中应该看到以下输出：
     *
     * UserContext Correlation id: TEST-CORRELATION-ID
     * LicenseServiceController Correlation id: TEST-CORRELATION-ID
     * LicenseService.getLicenseByOrg Correlation id: TEST-CORRELATION-ID
     *
     * 为了产生一个小小的结果需要做很多工作，但是，当使用 Hystrix 的 THREAD 级别的隔离时，这些工作都是很有必要的。
     */
    public static void main(String[] args) {

    }

}
