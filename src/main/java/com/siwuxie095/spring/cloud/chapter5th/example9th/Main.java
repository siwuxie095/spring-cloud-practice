package com.siwuxie095.spring.cloud.chapter5th.example9th;

/**
 * @author Jiajing Li
 * @date 2021-06-06 16:25:04
 */
@SuppressWarnings("all")
public class Main {

    /**
     * 基础进阶：微调 Hystrix
     *
     * 目前已经研究了使用 Hystrix 创建断路器模式和舱壁模式的基本概念。现在来看看如何真正定制 Hystrix 断路器的行为。
     * 记住，Hystrix 不仅能超时长时间运行的调用，它还会监控调用失败的次数，如果调用失败的次数足够多，那么 Hystrix
     * 会在请求发送到远程资源之前，通过使调用失败来自动阻止未来的调用到达服务。
     *
     * 这样做有两个原因。首先，如果远程资源有性能问题，那么快速失败将防止应用程序等待调用超时。这显著降低了调用应用
     * 程序或服务所导致的资源耗尽问题和崩溃的风险。其次，快速失败和阻止来自服务客户端的调用有助于苦苦挣扎的服务保持
     * 其负载，而不会彻底崩溃。快速失败给了性能下降的系统一些时间去进行恢复。
     *
     * 要了解如何在 Hystrix 中配置断路器，需要先了解 Hystrix 如何确定何时跳闸断路器的流程。
     *
     * 如下是 Hystrix 在远程资源调用失败时使用的决策过程（Hystrix 经过一系列检查来确定是否跳闸）：
     * 每当 Hystrix 命令遇到服务错误时，它将开始一个 10 s 的计时器，用于检查服务调用失败的频率。这个 10 s 窗口是
     * 可配置的。Hystrix 做的第一件事就是查看在 10 s 内发生的调用数量。如果调用次数少于在这个窗口内需要发生的最小
     * 调用次数，那么即使有几个调用失败，Hystrix 也不会采取行动。例如，在 Hystrix 考虑采取行动之前，需要在 10 s
     * 之内进行调用的次数的默认值为 20。如果这些调用之中有 15 个在 10 s 内发生调用失败，只要在 10 s 之内调用次数
     * 达不到 20 次，那么即使 15 个调用都失败，这些调用的数量也不足以让断路器发生跳闸。Hystrix 将继续让调用通过，
     * 到达远程服务。
     *
     * 在 10 s 窗口内达到最少的远程资源调用次数时，Hystrix 将开始查看整体故障的百分比。如果故障的总体百分比超过阈
     * 值，Hystrix 将触发断路器，使将来几乎所有的调用都失败。正如稍后即将讨论的那样，Hystrix 将会让部分调用通过来
     * 进行 "测试"，以查看服务是否恢复。错误阈值的默认值为 50%。
     *
     * 如果超过错误阈值的百分比，Hystrix 将 "跳闸" 断路器，防止更多的调用访问远程资源。如果远程调用失败的百分比未
     * 达到要求的阈值，并且 10 s 窗口已过去，Hystrix 将重置断路器的统计信息。
     *
     * 当 Hystrix 在一个远程调用上 "跳闸" 断路器时，它将尝试启动一个新的活动窗口。每隔 5 s（这个值是可配置的），
     * Hystrix 会让一个调用到达这个苦苦挣扎的服务。如果调用成功，Hystrix 将重置断路器并重新开始让调用通过。如果
     * 调用失败，Hystrix 将保持断路器断开，并在另一个 5 s 里再次尝试上述步骤。
     *
     * 基于此，开发人员可以使用 5 个属性来定制断路器的行为。@HystrixCommand 注解通过 commandPoolProperties
     * 属性公开了这 5 个属性。其中，threadPoolProperties 属性用于设置 Hystrix 命令中使用的底层线程池的行为，
     * 而 commandPoolProperties 属性用于定制与 Hystrix 命令关联的断路器的行为。如下代码展示了这些属性的名称
     * 以及如何在每个属性中设置值。
     *
     *     @HystrixCommand(fallbackMethod = "buildFallbackLicenseList",
     *             threadPoolKey = "licenseByOrgThreadPool",
     *             threadPoolProperties =
     *                     {@HystrixProperty(name = "coreSize",value = "30"),
     *                             @HystrixProperty(name = "maxQueueSize", value = "10")},
     *             commandProperties = {
     *                     @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),
     *                     @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "75"),
     *                     @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "7000"),
     *                     @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "15000"),
     *                     @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "5")}
     *     )
     *     public List<License> getLicensesByOrg(String organizationId) {
     *         logger.debug("getLicensesByOrg  Correlation id: {}",
     *                 UserContextHolder.getContext().getCorrelationId());
     *         randomlyRunLong();
     *
     *         return licenseRepository.findByOrganizationId(organizationId);
     *     }
     *
     * 第一个属性 circuitBreaker.requestVolumeThreshold 用于控制 Hystrix 考虑将该断路器跳闸之前，在 10 s
     * 之内必须发生的连续调用数量。
     *
     * 第二个属性 circuitBreaker.errorThresholdPercentage 是在超过 circuitBreaker.requestVolumeThreshold
     * 值之后在断路器跳闸之前必须达到的调用失败（由于超时、抛出异常或返回 HTTP 500）百分比。
     *
     * 第三个属性 circuitBreaker.sleepWindowInMilliseconds 是在断路器跳闸之后，Hystrix 允许另一个调用通过
     * 以便查看服务是否恢复健康之前 Hystrix 的休眠时间。
     *
     * 最后两个 Hystrix 属性 metrics.rollingStats.timeInMilliseconds 和 metrics.rollingStats.numBuckets
     * 的命名与前面的属性有所不同，但它们仍然是控制断路器的行为的。前者用于控制 Hystrix 用来监视服务调用问题的窗口
     * 大小，其默认值为 10 000 ms（即 10 s）。后者控制在定义的滚动窗口中收集统计信息的次数。在这个窗口中，Hystrix
     * 在桶（bucket）中收集度量数据，并检查这些桶中的统计信息，以确定远程资源调用是否失败。给前者设置的值必须能被定
     * 义的桶的数量值整除。例如，在这段代码所示的自定义设置中，Hystrix 将使用 15 s 的窗口，并将统计数据收集到长度
     * 为 3 s 的 5 个桶中。
     *
     * 注意：检查的统计窗口越小且在窗口中保留的桶的数量越多，就越会加剧高请求服务的 CPU 利用率和内存利用率。要意识
     * 到这一点，避免将度量收集窗口和桶设置为太细的粒度，除非你需要这种可见性级别。
     *
     *
     *
     * 重新审视 Hystrix 配置
     *
     * Hystrix 库是高度可配置的，可以让开发人员严格控制使用它定义的断路器模式和舱壁模式的行为。开发人员可以通过修
     * 改 Hystrix 断路器的配置，控制 Hystrix 在超时远程调用之前需要等待的时间。开发人员还可以控制 Hystrix 断路
     * 器何时跳闸以及 Hystrix 何时尝试重置断路器。
     *
     * 使用 Hystrix，开发人员还可以通过为每个远程服务调用定义单独的线程组，然后为每个线程组配置相应的线程数来微调
     * 舱壁实现。这允许开发人员对远程服务调用进行微调，因为某些远程资源调用具有较高的请求量。
     *
     * 在配置 Hystrix 环境时，需要记住的关键点是，开发人员可以使用 Hystrix 的三个配置级别：
     * （1）整个应用程序级别的默认值；
     * （2）类级别的默认值；
     * （3）在类中定义的线程池级别。
     *
     * 每个 Hystrix 属性都有默认设置的值，这些值将被应用程序中的每个 @HystrixCommand 注解所使用，除非这些属性
     * 值在 Java 类级别被设置，或者被类中单个 Hystrix 线程池级别的值覆盖。
     *
     * Hystrix 确实允许开发人员在类级别设置默认参数，以便特定类中的所有 Hystrix 命令共享相同的配置。类级属性是
     * 通过一个名为 @DefaultProperties 的类级注解设置的。例如，如果希望特定类中的所有资源的超时时间均为 10 s，
     * 则可以按以下方式设置 @DefaultProperties：
     *
     * @DefaultProperties(
     *     commandProperties = {
     *         @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",
     *         ➥ value = "10000")}
     * class MyService { ... }
     *
     * 除非在线程池级别上显式地覆盖，否则所有线程池都将继承应用程序级别的默认属性或类中定义的默认属性。Hystrix
     * 的 threadPoolProperties 和 commandProperties 也绑定到已定义的命令键。
     *
     * 注意：这里在编码示例的应用程序代码中硬编码了所有的 Hystrix 值。在生产环境中，最有可能
     * 需要调整的 Hystrix 数据（超时参数、线程池计数）将被外部化到 Spring Cloud Config。
     * 通过这种方式，如果需要更改参数值，就可以在更改完参数值之后重新启动服务实例，而无需重新
     * 编译和重新部署应用程序。
     *
     * 对于单个 Hystrix 池，这里将保持配置尽可能接近代码并将线程池配置置于 @HystrixCommand 注解中。如下总结了
     * 用于创建和配置 @HystrixCommand 注解的所有配置值。
     * （1）
     * 属性名称：fallbackMethod
     * 默认值：None
     * 描述：标识类中的方法，如果远程调用超时，将调用该方法。回调方法必须与 @HystrixCommand 注解在同一个类中，
     * 并且必须具有与调用类相同的方法签名。如果值不存在，Hystrix 会抛出异常。
     * （2）
     * 属性名称：threadPoolKey
     * 默认值：None
     * 描述：给予 @HystrixCommand 一个唯一的名称，并创建一个独立于默认线程池的线程池。如果没有定义任何值，则
     * 将使用默认的 Hystrix 线程池。
     * （3）
     * 属性名称：threadPoolProperties
     * 默认值：None
     * 描述：核心的 Hystrix 注解属性，用于配置线程池的行为。
     * （4）
     * 属性名称：coreSize
     * 默认值：10
     * 描述：设置线程池的大小。
     * （5）
     * 属性名称：maxQueueSize
     * 默认值：-1
     * 描述：设置线程池前面的最大队列大小。如果设置为 −1，则不使用队列，Hystrix 将阻塞请求，直到有一个线程可用
     * 来处理。
     * （6）
     * 属性名称：circuitBreaker.requestVolumeThreshold
     * 默认值：20
     * 描述：设置 Hystrix 开始检查断路器是否跳闸之前滚动窗口中必须处理的最小请求数。
     * （7）
     * 属性名称：circuitBreaker.errorThresholdPercentage
     * 默认值：50
     * 描述：在断路器跳闸之前，滚动窗口内必须达到的故障百分比。
     * （8）
     * 属性名称：circuitBreaker.sleepWindowInMilliseconds
     * 默认值：5000
     * 描述：在断路器跳闸之后，Hystrix 尝试进行服务调用之前将要等待的时间（以毫秒为单位）。
     * （9）
     * 属性名称：metrics.rollingStats.timeInMilliseconds
     * 默认值：10000
     * 描述：Hystrix 收集和监控服务调用的统计信息的滚动窗口（以毫秒为单位）。
     * （10）
     * 属性名称：metrics.rollingStats.numBuckets
     * 默认值：10
     * 描述：Hystrix 在一个监控窗口中维护的度量桶的数量。监视窗口内的桶数越多，Hystrix 在窗口内监控故障的时间
     * 越低。
     *
     * PS：（6）（7）（8）（9）（10）这几个属性只能使用 commandPoolProperties 属性设置。
     */
    public static void main(String[] args) {

    }

}
