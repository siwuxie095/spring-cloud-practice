package com.siwuxie095.spring.cloud.chapter5th.example9th;

/**
 * @author Jiajing Li
 * @date 2021-06-06 16:25:04
 */
@SuppressWarnings("all")
public class Main {

    /**
     * 深入理解 Hystrix
     *
     * 在这一点上，已经看了使用 Hystrix 建立断路器和舱壁模式的基本概念。现在要去看看如何真正定制 Hystrix
     * 断路器的行为。记住，Hystrix 不仅仅是时间长调用。Hystrix 还将监控调用失败的次数，如果有足够多次数的
     * 调用失败，Hystrix 将自动防止后续的请求在请求远程资源之前调用失败。
     *
     * 有两个原因。首先，如果远程资源存在性能问题，那么快速失败将阻止调用应用程序必须等待调用超时。这大大降
     * 低了调用应用程序或服务将经历自身资源耗尽问题和崩溃的风险。第二，快速失败和阻止来自服务客户端的调用将
     * 帮助陷入困境的服务跟上其负载，而不是在负载下完全崩溃。快速失败给出系统经历性能下降的恢复时间。
     *
     * 了解如何在 Hystrix 配置断路器，你需要先了解 Hystrix 如何决定断路器什么时候跳闸的流程。
     *
     * 当远程资源调用失败时，Hystrix 的决策过程如下（Hystrix 经过一系列的检查以确定是否断路器跳闸）：
     * 每当 Hystrix 命令遇到一个服务错误，它将启动一个 10 秒的定时器，计时器将被用来检查服务调用通常是如
     * 何失败的。这个 10 秒的窗口是可配置的。第一件事是 Hystrix 看在 10 秒的窗口内已经发生的调用次数。
     * 如果调用次数小于调用在窗口内需要发生的最小次数，然后 Hystrix 将不会采取行动，即使有些调用失败。例
     * 如，在 10 秒的窗口 Hystrix 将考虑行动之前，需要发生的调用次数缺省值是 20。如果在 10 秒的时间内
     * 调用失败有 15 次，没有足够的调用发生来使断路器跳闸，即使所有 15 次调用都失败。Hystrix 将继续让调用
     * 到达远程服务。
     *
     * 当远程资源调用最小次数在 10 秒的窗口内发生，Hystrix 将开始查看整体故障发生率。如果失败的总百分比超过
     * 阈值，Hystrix 会触发断路器和后续的调用绝大部分均失败。Hystrix 将让部分调用通过来测试，看看服务是否
     * 恢复。错误阈值的默认值是 50%。
     *
     * 当在远程调用上，Hystrix 断路器已经跳闸时，它将尝试为活动启动新的窗口。每五秒（这个个值是可配置的），
     * Hystrix 会让调用到达挣扎中的服务。如果调用成功，Hystrix 将重置断路器并开始重新让调用通过。如果调用
     * 失败，Hystrix 将断路器闭合，在 5 秒之后再尝试。
     *
     * 在此基础上，你可以看到有五个属性可以用来定制断路器的行为。
     *
     * @HystrixCommand 注解通过 commandPoolProperties 属性公开 5 个属性。然而 threadPoolProperties
     * 属性允许你设置用于 Hystrix 命令基础线程池的行为，commandPoolProperties 属性允许你自定义与 Hystrix
     * 命令相关的断路器行为。下面的代码显示属性的名称以及如何在每个属性中设置值。
     *
     *     @HystrixCommand(fallbackMethod = "buildFallbackLicenseList",
     *             threadPoolKey = "licenseByOrgThreadPool",
     *             threadPoolProperties =
     *                     {@HystrixProperty(name = "coreSize",value="30"),
     *                             @HystrixProperty(name="maxQueueSize", value="10")},
     *             commandProperties={
     *                     @HystrixProperty(name="circuitBreaker.requestVolumeThreshold", value="10"),
     *                     @HystrixProperty(name="circuitBreaker.errorThresholdPercentage", value="75"),
     *                     @HystrixProperty(name="circuitBreaker.sleepWindowInMilliseconds", value="7000"),
     *                     @HystrixProperty(name="metrics.rollingStats.timeInMilliseconds", value="15000"),
     *                     @HystrixProperty(name="metrics.rollingStats.numBuckets", value="5")}
     *     )
     *     public List<License> getLicensesByOrg(String organizationId){
     *         logger.debug("LicenseService.getLicensesByOrg  Correlation id: {}",
     *         UserContextHolder.getContext().getCorrelationId());
     *         randomlyRunLong();
     *
     *         return licenseRepository.findByOrganizationId(organizationId);
     *     }
     *
     * 第一个属性，circuitBreaker.requestVolumeTheshold，控制在 Hystrix 将考虑调用断路器跳闸之前，在
     * 一个 10 秒的窗口内必须出现的连续调用数量。第二个属性，circuitBreaker.errorThresholdPercentage，
     * 是 circuitBreaker.requestVolumeThreshold 值在断路器跳闸之前 circuitBreaker
     * .requestVolumeThreshold 值已经通过之后，调用必须失败的百分比（由于超时，将抛出一个异常，或返回
     * HTTP 500）。第三个属性，circuitBreaker.sleepWindowInMilliseconds，是一旦断路器跳闸，Hystrix
     * 允许另外的调用通过来看服务是否恢复健康之前 Hystrix 将休眠的时间。
     *
     * Hystrix 最后的两个属性（metrics.rollingStats.timeInMilliseconds 和 metrics.rollingStats
     * .numBuckets）的命名与前面的属性有些不同，但它们仍然控制断路器的行为。第一个属性，metrics
     * .rollingStats.timeInMilliseconds，用于控制窗口的大小，它将被 Hystrix 用于监控服务调用的问题。
     * 此值的默认值是 10000 毫秒（即 10 秒）。第二个属性，metrics.rollingStats.numBuckets, 控制你在
     * 窗口中定义并被收集的统计次数。Hystrix 收集在窗口中桶的度量并检查这些桶的统计数据来确定远程资源调用
     * 失败。桶数量的定义必须均匀分为以毫秒为单位的总体数量，用 rollingStatus.inMilliseconds 来设置统
     * 计。例如，在前面代码中你的自定义设置，Hystrix 将使用 15 秒窗口并将统计数据收集到五个长度为三秒的
     * 桶中。
     *
     * 注意：你进入的统计窗口越小，窗口中保存的桶数越大，在高容量服务上就可以提高 CPU 和内存利用率。要意识
     * 到这一点，并避免设置度量收集窗口和桶的细粒度，直到是你需要的可见级别。
     *
     *
     *
     * 进一步理解 Hystrix 配置
     *
     * Hystrix 库是可配置的，并让你可以紧紧的控制断路器的行为和你定义的舱壁模式。通过修改一个 Hystrix 断
     * 路器的配置，你可以在远程调用超时之前，控制 Hystrix 将等待的时间。当 Hystrix 断路器将跳闸和当
     * Hystrix 尝试重置断路器时，你也可以控制其行为。
     *
     * 用 Hystrix，你也可以通过定义每个远程服务调用单个线程组然后配置每一个线程组关联的线程数来微调你的舱
     * 壁实现。这允许你对远程服务调用进行微调，因为在其它远程资源调用将具有更高的量期间，某些调用比其它调用
     * 将具有更高的量。
     *
     * 关键是记住，当你看配置 Hystrix 环境时，在 Hystrix 你有三种配置级别：
     * （1）整个应用程序的默认值
     * （2）类默认值
     * （3）类中定义的线程池级别
     *
     * 每个 Hystrix 属性通过默认值设置，将被应用程序里的每个 @HystrixCommand 注解使用，除非他们设置为
     * Java 类级别或者在一个类中单个 Hystrix 线程池重写。
     *
     * Hystrix 让你在类级别设置默认参数，以便在一个特定的类共享相同配置的所有 Hystrix 命令。类级属性通
     * 过一个叫做 @DefaultProperties 的类级别注解进行设置。例如，如果你想让所有的资源在一个特定的类有
     * 一个 10 秒的超时时间，你可以按以下方式设置：
     *
     * @DefaultProperties(
     * 	commandProperties = {
     * 		@HystrixProperty(
     * 			name = "execution.isolation.thread.timeoutInMilliseconds",
     * 			value = "10000")}
     * class MyService { ... }
     *
     * 除非在线程池级别显式重写，否则所有线程池将继承应用程序级别上的默认属性或类中定义的默认属性。Hystrix
     * 的 threadPoolProperties 和 commandproperties 属性也绑上定义的命令键。
     *
     * 注意：对这里的例子，在应用程序代码硬编码了所有 Hystrix 值。在生产系统中，Hystrix 数据最可能需要修
     * 改（超时参数，线程池计数）为 Spring Cloud 配置。这样，如果你需要改变参数值，你可以改变值，然后重新
     * 启动服务实例，而无需重新编译和重新部署应用程序。
     *
     * 对于单个 Hystrix 池，这里将尽可能保持代码的配置，并将线程池配置放在 @HystrixCommand 注解中。如下
     * 总结了用于设置和配置 @HystrixCommand 注解的所有配置值。
     * （1）
     * 属性名称：fallbackMethod
     * 默认值：None
     * 描述：标识如果远程调用超时调用的类里面的方法。回调方法必须在与 @HystrixCommand 注解相同的类中，并
     * 且必须具有与调用类相同的方法参数。如果没有值，将通过 Hystrix 抛出一个异常。
     * （2）
     * 属性名称：threadPoolKey
     * 默认值：None
     * 描述：给 @HystrixCommand 唯一的名称和创建一个线程池，它是独立的默认线程池。如果没有定义值，默认
     * Hystrix 线程池将被使用。
     * （3）
     * 属性名称：threadPoolProperties
     * 默认值：None
     * 描述：用于配置线程池行为的核心 Hystrix 注解属性。
     * （4）
     * 属性名称：coreSize
     * 默认值：10
     * 描述：设置线程池的大小。
     * （5）
     * 属性名称：maxQueueSize
     * 默认值：-1
     * 描述：将在线程池前面设置的最大队列大小。如果设置为 -1，没有采用队列而 Hystrix 将阻塞直到一个线程变
     * 为可用的处理。
     * （6）
     * 属性名称：circuitBreaker.requestVolumeThreshold
     * 默认值：20
     * 描述：必须在滚动窗口内 Hystrix 将开始检查断路器是否会跳闸之前设置请求的最小数量。
     * （7）
     * 属性名称：circuitBreaker.errorThresholdPercentage
     * 默认值：50
     * 描述：在断路器跳闸之前必须在滚动窗口内发生的故障百分比。
     * （8）
     * 属性名称：circuitBreaker.sleepWindowInMilliseconds
     * 默认值：5000
     * 描述：在服务调用前，断路器已跳闸之后 Hystrix 将等待的毫秒数。
     * （9）
     * 属性名称：circuitBreaker.sleepWindowInMilliseconds
     * 默认值：5000
     * 描述：在服务调用前，断路器已跳闸之后 Hystrix 将等待的毫秒数。
     * （10）
     * 属性名称：metricsRollingStats.timeInMilliseconds
     * 默认值：10000
     * 描述：Hystrix 在窗口中将收集和监控统计服务调用的毫秒数。
     * （11）
     * 属性名称：metricsRollingStats.numBuckets
     * 默认值：10
     * 描述：Hystrix 将在它的监控窗口内维护的度量同数量。监视窗口中的桶越多，时间越低，Hystrix 将会监控
     * 窗口内的错误。
     *
     * PS：（6）（7）（8）（9）（10）（11）这几个属性只能用 commandPoolProperties 属性设置。
     */
    public static void main(String[] args) {

    }

}
