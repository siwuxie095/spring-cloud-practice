package com.siwuxie095.spring.cloud.chapter5th.example8th;

/**
 * @author Jiajing Li
 * @date 2021-06-06 15:43:36
 */
@SuppressWarnings("all")
public class Main {

    /**
     * 实现舱壁模式
     *
     * 在基于微服务的应用程序中，开发人员通常需要调用多个微服务来完成特定的任务。在不使用舱壁模式的情况下，这些调用默认
     * 是使用同一批线程来执行调用的，这些线程是为了处理整个 Java 容器的请求而预留的。在存在大量请求的情况下，一个服务
     * 出现性能问题会导致 Java 容器的所有线程被刷爆并等待处理工作，同时堵塞新请求，最终导致 Java 容器崩溃。舱壁模式将
     * 远程资源调用隔离在它们自己的线程池中，以便可以控制单个表现不佳的服务，而不会使该容器崩溃。
     *
     * Hystrix 使用线程池来委派所有对远程服务的请求。在默认情况下，所有的 Hystrix 命令都将共享同一个线程池来处理请求。
     * 这个线程池将有 10 个线程来处理远程服务调用，而这些远程服务调用可以是任何东西，包括 REST 服务调用、数据库调用等。
     * 如下说明了这一点：
     * （1）所有远程资源调用都位于一个共享线程池中，即 默认的 Hystrix 线程池。
     * （2）单个性能较差的服务可能会使 Hystrix 线程池饱和，并导致托管该服务的 Java 容器中的资源耗尽。
     *
     * PS：多种资源类型共享默认的 Hystrix 线程池。
     *
     * 在应用程序中访问少量的远程资源时，这种模型运行良好，并且各个服务的调用量分布相对均匀。问题是，如果某些服务具有比
     * 其他服务高得多的请求量或更长的完成时间，那么最终可能会导致 Hystrix 线程池中的线程耗尽，因为一个服务最终会占据
     * 默认线程池中的所有线程。
     *
     * 幸好，Hystrix 提供了一种易于使用的机制，在不同的远程资源调用之间创建舱壁。如下展示了 Hystrix 管理的资源被隔离
     * 到它们自己的 "舱壁" 时的情况。
     * （1）每个远程资源调用都放置在自己的线程池中，即 Hystrix 线程组。每个线程池都有可用于处理请求的最大线程数。
     * （2）一个性能低下的服务只会影响同一线程池中的其他服务调用，从而限制了调用可能会造成的损害。
     *
     * PS：Hystrix 命令绑定到隔离的线程池。
     *
     * 要实现隔离的线程池，需要使用 @HystrixCommand 注解的其他属性。接下来的代码将完成以下操作。
     * （1）为 getLicensesByOrg() 调用建立一个单独的线程池。
     * （2）设置线程池中的线程数。
     * （3）设置单个线程繁忙时可排队的请求数的队列大小。
     *
     * 如下代码展示了如何围绕服务调用建立一个舱壁，该服务调用从许可证服务查询许可证数据。
     *
     *     @HystrixCommand(
     *             // threadPoolKey 属性定义线程池的唯一名称
     *             threadPoolKey = "licenseByOrgThreadPool",
     *             // threadPoolProperties 属性用于定义和定制 threadPool 的行为
     *             threadPoolProperties =
     *                     // coreSize 属性用于定义线程池中线程的最大数量
     *                     {@HystrixProperty(name = "coreSize",value = "30"),
     *                             // maxQueueSize 用于定义一个位于线程池前的队列，它可以对传入的请求进行排队
     *                             @HystrixProperty(name = "maxQueueSize", value = "10")}
     *     )
     *     public List<License> getLicensesByOrg(String organizationId) {
     *         randomlyRunLong();
     *         return licenseRepository.findByOrganizationId(organizationId);
     *     }
     *
     * 要注意的第一件事是，这里在 @HystrixCommand 注解中引入了一个新属性，即 threadPoolkey 。这向 Hystrix 发出信
     * 号，这里想要建立一个新的线程池。如果在线程池中没有设置任何进一步的值，Hystrix 会使用 threadPoolKey 属性中的
     * 名称搭建一个线程池，并使用所有的默认值来对线程池进行配置。
     *
     * 要定制线程池，应该使用 @HystrixCommand 上的 threadPoolProperties 属性。此属性使用 HystrixProperty 对象
     * 的数组，这些 HystrixProperty 对象用于控制线程池的行为。使用 coreSize 属性可以设置线程池的大小。
     *
     * 开发人员还可以在线程池前创建一个队列，该队列将控制在线程池中线程繁忙时允许堵塞的请求数。此队列大小由 maxQueueSize
     * 属性设置。一旦请求数超过队列大小，对线程池的任何其他请求都将失败，直到队列中有空间。
     *
     * 请注意有关 maxQueueSize 属性的两件事情。首先，如果将其值设置为 −1，则将使用 Java SynchronousQueue 来保存
     * 所有传入的请求。同步队列本质上会强制要求正在处理中的请求数量永远不能超过线程池中可用线程的数量。将 maxQueueSize
     * 设置为大于 1 的值将导致 Hystrix 使用 Java LinkedBlockingQueue。LinkedBlockingQueue 的使用允许开发人员
     * 即使所有线程都在忙于处理请求，也能对请求进行排队。
     *
     * 要注意的第二件事是，maxQueueSize 属性只能在线程池首次初始化时设置（例如，在应用程序启动时）。Hystrix 允许通
     * 过使用 queueSizeRejectionThreshold 属性来动态更改队列的大小，但只有在 maxQueueSize 属性的值大于 0 时，
     * 才能设置此属性。
     *
     * 自定义线程池的适当大小是多少？Netflix 推荐以下公式：
     *
     * 服务在健康状态时每秒支撑的最大请求数 × 第 99 百分位延迟时间（以秒为单位）+ 用于缓冲的少量额外线程
     *
     * 通常情况下，直到服务处于负载状态，开发人员才能知道它的性能特征。线程池属性需要被调整的关键指标就是，即使目标远程
     * 资源是健康的，服务调用仍然超时。
     */
    public static void main(String[] args) {

    }

}
