package com.siwuxie095.spring.cloud.chapter5th.example8th;

/**
 * @author Jiajing Li
 * @date 2021-06-06 15:43:36
 */
@SuppressWarnings("all")
public class Main {

    /**
     * 舱壁模式的实现
     *
     * 在微服务应用中，你为了完成特定任务需要调用多个微服务。没有使用舱壁模式，这些调用的默认行为是使用
     * 预留处理整个 Java 容器请求的同一线程执行。大量地，一个服务的性能问题可以导致 Java 容器所有的线
     * 程被刷爆并等待处理，在此期间新的工作请求被堵塞。Java 容器最终会崩溃。舱壁模式将在自己的线程池中
     * 隔离远程资源调用，一个性能不好的服务可以包含在容器内而不会导致容器崩溃。
     *
     * Hystrix 使用一个线程池代理远程服务请求。默认情况下，所有 Hystrix 命令将共享相同的线程池来处理
     * 请求。这个线程池将有 10 个线程用于处理远程服务调用，这些远程服务调用可以是任何东西，包括其 REST
     * 服务调用，数据库调用等等。
     *
     * 当你在应用程序中访问了少量的远程资源，并且每个服务的调用量相对均匀分布时，此模型可以较好的工作。
     * 问题是如果你有更高的量或完成时间较长之后的其他服务，你可以最终引入线程耗尽你的 Hystrix 线程池，
     * 因为服务结束了对默认线程池中所有线程的控制。
     *
     * PS：这就是跨多个资源类型共享的默认 Hystrix 线程池。所有远程资源调用都在一个共享线程池中。一个
     * 单一的执行速度较慢的服务可以使 Hystrix 线程池饱和，并导致托管服务的 Java 容器资源枯竭。
     *
     * 幸运的是，Hystrix 为创建舱壁提供了用于不同的远程资源之间的一种易于使用的机制。即 Hystrix 所管
     * 理的资源看起来像他们隔离自己的 "隔壁"。
     *
     * 为了实现隔离线程池，你需要使用通过 @HystrixCommand 注解公开的附加属性。下面看看一些代码：
     * （1）为 getLicensesByOrg() 调用创建一个隔离的线程池；
     * （2）设置线程池中的线程数；
     * （3）如果单个线程繁忙，可以为队列请求的请求数量设置队列大小；
     *
     * 下面的代码演示了如何为调用许可服务的许可数据的所有调用设置一个舱壁。
     *
     *     @HystrixCommand(
     *             threadPoolKey = "licenseByOrgThreadPool",
     *             threadPoolProperties =
     *                     {@HystrixProperty(name = "coreSize",value="30"),
     *                             @HystrixProperty(name="maxQueueSize", value="10")}
     *     )
     *     public List<License> getLicensesByOrg(String organizationId){
     *         randomlyRunLong();
     *         return licenseRepository.findByOrganizationId(organizationId);
     *     }
     *
     * 你应该注意的第一件事是，这里在 @HystrixCommand 注解引入了一个新的属性，threadPoolkey。这个
     * Hystrix 属性表明，你想建立一个新的线程池。如果你没有进一步的对线程池设置值，Hystrix 在
     * threadPoolKey 属性中设置线程池的主键名称，但将使用所有的默认值作为线程池的配置。
     *
     * 你在 @HystrixCommand 注解中使用 threadPoolProperties 属性，自定义你的线程池。这个属性接受
     * 一系列 HystrixProperty 对象。这些 HystrixProperty 对象可以用来控制线程池的行为。你可以用
     * coreSize 属性设置线程池的大小。
     *
     * 你还可以在线程池前面设置一个队列，该线程控制线程池中的线程忙时会允许多少请求堵塞。这个队列的大小
     * 是由 maxQueueSize 属性设置。一旦请求数量超过队列大小，任何向线程池额外的请求都会失败，直到队列
     * 中有空间为止。
     *
     * 注意 maxQueueSize 属性的两件事情。首先，如果你设置为 -1，一个 Java 同步队列用来存储所有传入的
     * 请求。同步队列本质上限制你在进程中不能有更多的请求，即线程池中可用的线程数。设置 maxQueueSize
     * 值大于一个会导致 Hystrix 使用 Java 的 LinkedBlockingQueue 的值。一个 LinkedBlockingQueue
     * 的使用允许开发者排队请求，即使所有的线程都忙着处理请求。
     *
     * 第二点需要注意的是，maxQueueSize 属性只能在线程池首次初始化时（例如，在应用程序启动）被设置。
     * Hystrix 会允许你用 queueSizeRejectionThreshold 属性动态改变队列的大小，但是这个属性只能
     * 设置在 maxQueueSize 属性，且其值一个大于 0。
     *
     * 自定义线程池的正确大小是多少？Netflix 推荐以下公式：
     *
     * （当服务正常时每秒的峰值请求 * 99% 平均响应时间）+ 少量额外线程用于开销
     *
     * 你通常不知道服务的性能特性，直到它处于负载状态。线程池属性需要调整的一个关键指标是当服务调用超时
     * 时，即使目标远程资源是健康的。
     */
    public static void main(String[] args) {

    }

}
